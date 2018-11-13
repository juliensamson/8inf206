package ca.uqac.lecitoyen.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Image;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.PostHistory;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.MultimediaView;
import ca.uqac.lecitoyen.views.ToolbarView;
import de.hdodenhof.circleimageview.CircleImageView;
import nl.changer.audiowife.AudioWife;

public class CreateAndEditActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "CreateAndEditActivity";

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int AUDIO_REQUEST_CODE = 3;
    private static final int DELETE_REQUEST_CODE = 0;

    private Context mContext;

    private ToolbarView mPostToolbar;
    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private TextView mToolbarButton;
    private EditText mPublicationView;
    private CircleImageView mCircleImageView;
    private MultimediaView mMultimediaView;
    private ImageView mPicture;
    private FrameLayout mPlayerLayout;
    private ImageButton mCameraButton;
    private ImageButton mGalleryButton;
    private ImageButton mAttachmentutton;

    private Uri mImageUri;
    private Uri mAudioUri;
    private User mUserdata;
    private User mUserAuth;

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
        setContentView(R.layout.activity_create_and_edit);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        if(bundle != null) {
            mUserAuth = bundle.getParcelable("user");
            Log.e(TAG, mUserAuth.getName());
        }



        //  Initialize auth
        fbAuth = FirebaseAuth.getInstance();

        //  Context
        mContext = this;

        //  Initiate database
        dbManager = DatabaseManager.getInstance();

        //  View
        mPostToolbar = findViewById(R.id.create_post_toolbar);
        mPublicationView = findViewById(R.id.post_message);
        mCircleImageView = findViewById(R.id.post_profil_picture);
        mMultimediaView = findViewById(R.id.create_post_multimedia);
        mPicture = findViewById(R.id.publication_picture);
        mPlayerLayout = findViewById(R.id.create_post_audioplayer);

        //  Button
        mPostToolbar.onButtonClickListener(this);
        findViewById(R.id.create_post_picture_gallery).setOnClickListener(this);
        findViewById(R.id.create_post_picture_camera).setOnClickListener(this);
        findViewById(R.id.create_post_music).setOnClickListener(this);
        findViewById(R.id.create_post_link).setOnClickListener(this);
        //findViewById(R.id.publication_picture_remove).setVisibility(View.VISIBLE);
        //findViewById(R.id.publication_picture_remove).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_view_button:
                updateDB();
                break;
            case R.id.create_post_picture_camera:
                break;
            case R.id.create_post_picture_gallery:
                Log.d(TAG, "gallery clicked");
                openGallery();
                break;
            case R.id.create_post_music:
                Log.d(TAG, "music clicked");
                openStorage();
                break;
            case R.id.create_post_link:
                break;
        }
    }

    private void updateUI() {

        try {

            /*if(mPostToolbar == null)
                throw new NullPointerException("Toolbar instance is null");

            if(mUserAuth == null)
                throw new NullPointerException("User instance is null");

            if(mUserAuth.getPid() == null || mUserAuth.getPid().isEmpty()) {
                Glide.with(this).load(R.color.black_200).into(mCircleImageView);
                throw new NullPointerException("User pid cannot be null");
            }*/

            mPostToolbar.buttonToolbar(this, "Publier");

            setImageView(mCircleImageView, dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid()));

            //Glide.with(this)
            //        .load(dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid()))
            //        .into(mCircleImageView);

        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    mImageUri = data.getData();
                    mMultimediaView
                            .setEditable(true)
                            .loadImages(mImageUri)
                            .setFullHeight();
                    //mPictureLayout.setVisibility(View.VISIBLE);
                    //Glide.with(this).load(mImageUri).into(mPicture);
                    break;
                case CAMERA_REQUEST_CODE:    //TODO: Make this work somehow
                    //checkInternalStorage();
                    //updateStorage(imageUri);
                    break;
                case AUDIO_REQUEST_CODE:
                    mAudioUri = data.getData();
                    AudioWife.getInstance().init(mContext, mAudioUri)
                            .useDefaultUi(mPlayerLayout, getLayoutInflater());
                    Log.d(TAG, "Audio request code");
                    //showAudioSetup(mAudioUri);
                    break;
                case DELETE_REQUEST_CODE:
                    break;
                default:
                    break;
            }
        } else if (requestCode == RESULT_CANCELED) {
            Log.e(TAG, "Some error occured");
        }
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        if (openGalleryIntent.resolveActivity(getPackageManager()) != null) {
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    private void openStorage() {

        if (isExternalStorageWritable()) {

            if (isExternalStorageReadable()) {

                Intent openStorage = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                openStorage.setDataAndType(uri, "audio/*");
                if (openStorage.resolveActivity(getPackageManager()) != null) {
                    //startActivity(Intent.createChooser(openStorage, "Open folder"));
                    startActivityForResult(openStorage, AUDIO_REQUEST_CODE);
                }
            } else {
                Log.e(TAG, "Storage not readable");
            }
        } else {
            Log.e(TAG, "Storage not writable");
        }
        //String path = Environment.getExternalStorageDirectory() + File.separator;
    }

    /*

            Firebase, ValueEventListener, update database.

     */

    @SuppressWarnings("unchecked")
    private void updateDB() {

        if (!validateForm()) {
            return;
        }

        if (mUserdata != null) {
            String publication = mPublicationView.getText().toString();
            long currentTime = System.currentTimeMillis();

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

            stPosts = dbManager.getStoragePost(post.getPostid());
            //  Create storage reference if there is an image added to the post
            if (mMultimediaView.getVisibility() != View.GONE && mImageUri != null) {

                ArrayList<Image> postImageList = new ArrayList<>();
                postImageList.add(new Image("image" + post.getPostid() + "1000"));
                post.setImages(postImageList);

                if (post.getImages() != null && !post.getImages().isEmpty())
                    updateImageStorage(stPosts, post.getImages().get(0).getImageId());
                else
                    Log.e(TAG, "picture is null");
            } else {
                this.finish();
            }
            if (mPlayerLayout.getVisibility() != View.GONE && mAudioUri != null) {

                post.setAudio("audio" + post.getPostid() + "1000");

                if (post.getAudio() != null && !post.getAudio().isEmpty()) {
                    updateAudioStorage(stPosts, post.getAudio());
                } else {
                    Log.e(TAG, "audio is null");
                }
            } else {
                this.finish();
            }
            //  Add data to fire base
            writePublicationToFirebase(post);
        } else {
            Log.e(TAG, "mUserdata empty");
        }
    }

    private void updateImageStorage(StorageReference storageReference, String imagesId) {

        showProgressDialog();

        try {
            File file = new File(mImageUri.getPath());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            //Bitmap compressor = new Compressor(mContext).compressToBitmap(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();


            UploadTask uploadTask = storageReference.child(imagesId).putBytes(data);
            uploadTask
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

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void updateAudioStorage(StorageReference storageReference, String audioId) {
        Log.e(TAG, "updateAudio");
        showProgressDialog();
        storageReference.child(audioId)
                .putFile(mAudioUri)
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
    /*
    private void showAudioSetup(Uri uri) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_audio_layout, null);

        FrameLayout playerAudio = dialogView.findViewById(R.id.dialog_audio_player);
        final EditText titleAudio = dialogView.findViewById(R.id.dialog_audio_title);
        final ImageView imageAudio = dialogView.findViewById(R.id.dialog_audio_image);
        imageAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        AudioWife.getInstance().init(mContext, uri).useDefaultUi(playerAudio,getLayoutInflater());

        builder.setTitle("Configurer votre fichier audio")
                .setIcon(R.drawable.ic_music_note_black_24dp)
                .setView(dialogView)
                .setPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(titleAudio.getText() == null && titleAudio.getText().toString().isEmpty())
                            return;
                        if(imageAudio.getDrawable() == null)
                            return;
                        mMultimediaView.setMultimediaMusic(imageAudio.getD);
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
    */

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

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
