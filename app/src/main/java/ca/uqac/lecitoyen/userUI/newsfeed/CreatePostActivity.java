package ca.uqac.lecitoyen.userUI.newsfeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Image;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostHistory;
import ca.uqac.lecitoyen.database.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreatePostActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "CreatePostActivity";

    private static final int CAMERA_REQUEST_CODE= 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int DELETE_REQUEST_CODE = 0;

    private Context mContext;

    private Toolbar mToolbar;
    private FrameLayout mPictureLayout;
    private TextView mToolbarTitle;
    private TextView mToolbarButton;
    private EditText mPublicationView;
    private CircleImageView mCircleImageView;
    private ImageView mPicture;
    private ImageButton mCameraButton;
    private ImageButton mGalleryButton;
    private ImageButton mAttachmentutton;

    private Uri mImageUri;
    private User mUserdata;

    //  Firebase Authentification
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseManager dbManager;
    private DatabaseReference dbReference;
    private DatabaseReference dbUserdata;
    private DatabaseReference dbPosts;
    private StorageReference stPosts;
    private StorageReference stUserProfilPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Log.d(TAG, "Created");

        //  Initialize auth
        fbAuth = FirebaseAuth.getInstance();

        //  Context
        mContext = this;

        //  Initiate database
        dbManager = DatabaseManager.getInstance();


        //  View
        setToolbar();
        mPublicationView = findViewById(R.id.post_message);
        mCircleImageView = findViewById(R.id.post_profil_picture);
        mPicture = findViewById(R.id.publication_picture);
        mPictureLayout = findViewById(R.id.publication_picture_layout);

        //  Button
        findViewById(R.id.toolbar_post_publish).setOnClickListener(this);
        findViewById(R.id.post_picture_gallery).setOnClickListener(this);
        findViewById(R.id.post_picture_camera).setOnClickListener(this);
        findViewById(R.id.post_attachment).setOnClickListener(this);
        findViewById(R.id.publication_picture_remove).setVisibility(View.VISIBLE);
        findViewById(R.id.publication_picture_remove).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        if(fbAuth != null) {

            fbUser = fbAuth.getCurrentUser();

            if(fbUser != null) {
                String uid = fbUser.getUid();
                //  Get database & storage reference
                dbReference = dbManager.getReference();
                dbUserdata = dbManager.getDatabaseUser(uid);
                dbPosts = dbManager.getDatabasePosts();
                stUserProfilPicture = dbManager.getStorageUserProfilPicture(uid);

                //  read user data
                dbUserdata.addValueEventListener(readUserdata());
            }
        } else {
            Log.e(TAG, "auth is null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.toolbar_post_publish:
                updateDB();
                break;
            case R.id.post_picture_camera:
                break;
            case R.id.post_picture_gallery:
                Log.d(TAG, "gallery clicked");
                openGallery();
                break;
            case R.id.post_attachment:
                break;
            case R.id.publication_picture_remove:
                mPictureLayout.setVisibility(View.GONE);
                mPicture.setImageDrawable(null);
                break;
        }
    }

    private void setToolbar() {
        mToolbar = findViewById(R.id.toolbar_post);
        mToolbarTitle = findViewById(R.id.toolbar_post_title);

        setSupportActionBar(mToolbar);
        mToolbarTitle.setText("");

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        } catch (NullPointerException npe) {

            Log.e(TAG, npe.getMessage());

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mImageUri = data.getData();

        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case GALLERY_REQUEST_CODE:
                    mPictureLayout.setVisibility(View.VISIBLE);
                    Glide.with(this).load(mImageUri).into(mPicture);
                    break;
                case CAMERA_REQUEST_CODE:    //TODO: Make this work somehow
                    //checkInternalStorage();
                    //updateStorage(imageUri);
                    break;
                case DELETE_REQUEST_CODE:
                    break;
                default:
                    break;
            }
        }
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        if (openGalleryIntent.resolveActivity(getPackageManager()) != null) {
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    /*

            Firebase, ValueEventListener, update database.

     */

    @SuppressWarnings("unchecked")
    private void updateDB() {

        if(!validateForm()) {
            return;
        }

        if(mUserdata != null) {
            String publication = mPublicationView.getText().toString();
            long   currentTime = System.currentTimeMillis();

            //  Create post object
            Post post = new Post(
                    getPostKey(),
                    mUserdata,
                    publication,
                    currentTime
            );

            //  Create post-history object
            ArrayList postHistoryList = new ArrayList<PostHistory>();
            PostHistory postHistory = new PostHistory(
                    0,
                    publication,
                    currentTime
            );
            //  Add publications history to post
            postHistoryList.add(postHistory);
            post.setHistories(postHistoryList);

            //  Create storage reference if there is an image added to the post
            if(mPictureLayout.getVisibility() != View.GONE && mImageUri != null) {
                ArrayList<Image> postImageList = new ArrayList<>();
                postImageList.add(new Image("image" + post.getPostid() + "1000"));
                post.setImages(postImageList);
                updateStorage(post);
            } else {
                this.finish();
            }
            //  Add data to fire base
            writePublicationToFirebase(post);
        } else {
            Log.e(TAG, "mUserdata empty");
        }
    }

    private void updateStorage(Post post) {

        stPosts = dbManager.getStoragePost(post.getPostid());

        showProgressDialog();

        if(post.getImages() != null && !post.getImages().isEmpty()) {
            stPosts.child(post.getImages().get(0).getImageId())
                    .putFile(mImageUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d(TAG, "Byte transferred: " + taskSnapshot.getBytesTransferred());

                            //TODO: add byte transfer to ProgressDialog
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            hideProgressDialog();
                            onBackPressed();
                            Log.d(TAG, "image uploaded");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.getMessage());
                            hideProgressDialog();
                        }
            });
        } else
            Log.e(TAG, "picture id is null");
    }



    private ValueEventListener readUserdata() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserdata = dataSnapshot.getValue(User.class);

                if(mUserdata != null) {
                    if (mUserdata.getPid() != null && !mUserdata.getPid().isEmpty())
                        Glide.with(mContext).load(stUserProfilPicture.child(mUserdata.getPid())).into(mCircleImageView);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        };
    }

    private void setPostKey(Post post) {

    }

    private String getPostKey() {
        return dbPosts.push().getKey();
    }

    private void writePublicationToFirebase(Post post) {
            Log.d(TAG, "writePost");
            Map<String, Object> postValues = post.toMap();

            //  write on firebase
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + post.getPostid(), postValues);
            childUpdates.put("/user-posts/" + post.getUser().getUid() + "/" + post.getPostid(), postValues);

            dbReference.updateChildren(childUpdates);
    }

    private boolean validateForm() {
        boolean valid = true;

        String post = mPublicationView.getText().toString();
        if (TextUtils.isEmpty(post)) {
            mPublicationView.setError("Vide");
            valid = false;
        } else {
            mPublicationView.setError(null);
        }

        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
