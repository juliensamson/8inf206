package ca.uqac.lecitoyen.dialogs;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.models.Audio;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Image;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.PostHistory;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.util.ImageHandler;
import ca.uqac.lecitoyen.views.MultimediaView;
import ca.uqac.lecitoyen.util.Util;
import ca.uqac.lecitoyen.views.ToolbarView;
import de.hdodenhof.circleimageview.CircleImageView;
import nl.changer.audiowife.AudioWife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class CreateDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = CreateDialog.class.getSimpleName() ;

    private static final String ARG_POST = "post";
    private static final String ARG_USER = "user";

    private Activity mActivity;
    private ImageHandler mImageHandler;

    private Bitmap mBitmapImage;
    private File mLocalFile;
    private Uri mImageUri, mAudioUri;

    //  Views
    private ToolbarView mToolbar;
    private CircleImageView mProfileImage;
    private TextView mRemoveLayout;
    private EditText mMessage;
    private EditText mImageTitle, mImageGenre, mAddImageEditText;
    private EditText mAudioTitle, mAudioGenre, mAddAudioEditText, mAddAudioImage;
    private LinearLayout mImageLayout, mAudioLayout;
    private FloatingActionMenu mAddLayoutMenu;

    private FrameLayout mPlayerView;
    private MultimediaView mImageView, mAudioImageView;

    private DatabaseManager dbManager;
    private Post mPostSelect;
    private User mUserAuth;

    public CreateDialog() {}

    public static CreateDialog newInstance(Post post, User user) {
        CreateDialog createDialog = new CreateDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POST, post);
        args.putParcelable(ARG_USER, user);
        createDialog.setArguments(args);
        return createDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        this.mActivity = getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.mImageHandler = ImageHandler.getInstance();

        if(getArguments() != null) {
            mPostSelect = getArguments().getParcelable(ARG_POST);
            mUserAuth = getArguments().getParcelable(ARG_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_bottom_create, container, false);
        Log.d(TAG, "onCreateView");
        //  Toolbar
        mToolbar = rootView.findViewById(R.id.create_post_toolbar);

        //  Main Views
        mProfileImage = rootView.findViewById(R.id.create_post_profile_picture);
        mMessage = rootView.findViewById(R.id.create_post_message);
        mAddLayoutMenu = rootView.findViewById(R.id.create_post_add_menu);
        mRemoveLayout = rootView.findViewById(R.id.create_post_remove_layout);
        mImageLayout = rootView.findViewById(R.id.create_post_image_layout);
        mAudioLayout = rootView.findViewById(R.id.create_post_audio_layout);

        //  Image Layout Views
        mImageTitle = rootView.findViewById(R.id.create_post_image_title);
        mImageGenre = rootView.findViewById(R.id.create_post_image_add_genre);
        mImageView  = rootView.findViewById(R.id.create_post_image);

        //  Audio Layout Views
        mAudioTitle = rootView.findViewById(R.id.create_post_audio_title);
        mPlayerView = rootView.findViewById(R.id.create_post_audio_player);
        mAudioImageView = rootView.findViewById(R.id.create_post_audio_image);

        //  Button
        mToolbar.onButtonClickListener(this);

        rootView.findViewById(R.id.create_post_remove_layout).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_add_image_layout).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_add_audio_layout).setOnClickListener(this);

        rootView.findViewById(R.id.create_post_add_image).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_image_add_genre).setOnClickListener(this);

        rootView.findViewById(R.id.create_post_audio_add_audio).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_audio_add_image).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_audio_add_genre).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        updateUI();
    }

    /**
     *
     *
     *      Public methods
     *
     *
     */


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.toolbar_view_close:
                dismiss();
                break;
            case R.id.toolbar_view_button:
                updateDB();
                break;
            case R.id.create_post_remove_layout:
                hideAllMediaLayout();
                break;
            case R.id.create_post_add_image_layout:
                showImageLayout();
                break;
            case R.id.create_post_add_audio_layout:
                showAudioLayout();
                break;
            case R.id.create_post_add_image:
                showImageSelection();
                break;
            case R.id.create_post_audio_add_audio:
                openDeviceStorage();
                break;
            case R.id.create_post_audio_add_image:
                showImageSelection();
                break;
            case R.id.create_post_audio_add_genre:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case Constants.REQUEST_GALLERY_CODE:
                    Log.d(TAG, "onRequestGalleryCode");
                    setImageUri(data.getData());
                    break;
                case Constants.REQUEST_CAMERA_CODE:    //TODO: Make this work somehow
                    try {
                        Bitmap photo = (Bitmap) data.getExtras().get("data");
                        setImageBitmap(photo);
                        Log.d(TAG, "onRequestCameraCode");
                    } catch (NullPointerException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    setImageUri(data.getData());
                    break;
                case Constants.REQUEST_AUDIO_CODE:
                    Log.d(TAG, "resquest Audio");
                    setAudioUri(data.getData());
                    break;
                default:
                    break;
            }

        } else if (requestCode == RESULT_CANCELED) {
            Log.e(TAG, "Some error occured");
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mActivity.finish();
        mImageHandler = null;
        mBitmapImage = null;
        mAudioUri = null;

        mToolbar = null;
        mProfileImage = null;
        mRemoveLayout = null;
        mMessage = null;
        mImageTitle = null; mImageGenre = null; mAddImageEditText = null;
        mAudioTitle = null; mAudioGenre = null; mAddAudioEditText = null; mAddAudioImage = null;
        mImageLayout = null; mAudioLayout = null;
        mAddLayoutMenu = null;

        mPlayerView = null;
        mImageView = null; mAudioImageView =null;

        dbManager = null;
        mPostSelect = null;
        mUserAuth = null;
    }

    /**
     *
     *
     *      Private methods
     *
     *
     */

    private void updateUI() {

        if(mPostSelect == null)
            mToolbar.buttonToolbar(mActivity, "Publier");
        else
            mToolbar.buttonToolbar(mActivity, "Modifier");


        //StorageReference stUserImage = dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid());

        //if(mUserAuth.getPid() != null && !mUserAuth.getPid().isEmpty())
        //    Glide.with(mActivity).load(stUserImage).into(mProfileImage);

        if(mPostSelect != null) {

            mMessage.setText(mPostSelect.getMessage());

            if(mPostSelect.getImages() != null) {

                StorageReference stPost = dbManager.getStoragePost(mPostSelect.getPostid());

                Image image = mPostSelect.getImages().get(0);
                mImageLayout.setVisibility(View.VISIBLE);
                mImageTitle.setText(image.getName());

                try {

                    final File localFile = File.createTempFile(image.getName(), "jpg");

                    stPost.child(image.getImageid()).getFile(localFile).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            setImageFile(localFile);
                        }
                    });

                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }

            }

            if(mPostSelect.getAudio() != null) {

                StorageReference stPost = dbManager.getStoragePost(mPostSelect.getPostid());

                Audio audio = mPostSelect.getAudio();
                mAudioLayout.setVisibility(View.VISIBLE);
                mAudioTitle.setText(audio.getTitle());

                /*stPost.child(audio.getPid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setImageUri(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "2+2" + e.getMessage());


                    }
                });*/

                stPost.child(audio.getAid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        setAudioUri(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });

            }


        } else {

            Log.d(TAG, "new post is created");

        }

    }

    private void setImageFile(File file) {

        mLocalFile = file;


        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            mBitmapImage = BitmapFactory.decodeStream(new FileInputStream(mLocalFile), null, options);

            if (mImageLayout.getVisibility() == View.VISIBLE) {
                mImageView.with(mActivity)
                        .setEditable(true)
                        .setFrameSize()
                        .loadImages(mBitmapImage, "");
            } else if (mAudioLayout.getVisibility() == View.VISIBLE) {
                mAudioImageView.with(mActivity)
                        .setEditable(true)
                        .setFrameSize()
                        .loadImages(mBitmapImage, "");
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void setImageUri(Uri uri) {

        mImageUri = uri;

        try {

            mBitmapImage = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), mImageUri);

            if(mImageLayout.getVisibility() == View.VISIBLE) {
                mImageView.with(mActivity)
                        .setEditable(true)
                        .setFrameSize()
                        .loadImages(mBitmapImage, "");
            } else if (mAudioLayout.getVisibility() == View.VISIBLE){
                mAudioImageView.with(mActivity)
                        .setEditable(true)
                        .setFrameSize()
                        .loadImages(mBitmapImage, "");
            }

        } catch (IOException e) {
            Log.e(TAG, "3" + e.getMessage());
        }
    }

    private void setImageBitmap(Bitmap bitmap) {
        mBitmapImage = bitmap;

        if(mImageLayout.getVisibility() == View.VISIBLE) {
            mImageView.setEditable(true).loadImages(mBitmapImage, "");
        } else if (mAudioLayout.getVisibility() == View.VISIBLE){
            mAudioImageView.setEditable(true).loadImages(mBitmapImage, "");
        }

    }

    private void setAudioUri(Uri uri) {
        mAudioUri = uri;
        AudioWife.getInstance().init(mActivity, mAudioUri)
                .useDefaultUi(mPlayerView, mActivity.getLayoutInflater());
    }

    private void hideAllMediaLayout() {
        mRemoveLayout.setVisibility(View.GONE);
        mImageLayout.setVisibility(View.GONE);
        mAudioLayout.setVisibility(View.GONE);
    }

    private void showImageLayout() {
        mAddLayoutMenu.close(true);
        mRemoveLayout.setVisibility(View.VISIBLE);
        mImageLayout.setVisibility(View.VISIBLE);
        mAudioLayout.setVisibility(View.GONE);
    }

    private void showAudioLayout() {
        mAddLayoutMenu.close(true);
        mRemoveLayout.setVisibility(View.VISIBLE);
        mAudioLayout.setVisibility(View.VISIBLE);
        mImageLayout.setVisibility(View.GONE);
    }

    private void showImageSelection() {
        final SelectImageTypeDialog dialog = new SelectImageTypeDialog(mActivity);
        dialog.create().OnCameraClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mImageHandler.openCamera(mActivity);
                startActivityForResult(intent, Constants.REQUEST_CAMERA_CODE);
                dialog.dismiss();
            }
        }).OnGalleryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = mImageHandler.openGallery(mActivity);
                startActivityForResult(intent, Constants.REQUEST_GALLERY_CODE);
                dialog.dismiss();
            }
        }).show();
    }

    private void openDeviceStorage() {

        if (isExternalStorageWritable()) {

            if (isExternalStorageReadable()) {

                Intent openStorage = new Intent(Intent.ACTION_GET_CONTENT);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                openStorage.setDataAndType(uri, "audio/*");
                if (openStorage.resolveActivity(mActivity.getPackageManager()) != null) {
                    //startActivity(Intent.createChooser(openStorage, "Open folder"));
                    startActivityForResult(openStorage, Constants.REQUEST_AUDIO_CODE);
                }
            } else {
                Log.e(TAG, "Storage not readable");
            }
        } else {
            Log.e(TAG, "Storage not writable");
        }
        //String path = Environment.getExternalStorageDirectory() + File.separator;
    }

    private void updateDB() {

        Log.d(TAG, "updateDB");

        if (!validateForm()) {
            return;
        }

        try {

            if (mUserAuth == null)
                throw new NullPointerException("Userauth is null");



            if(mPostSelect ==  null) {

                String message = mMessage.getText().toString();
                long currentTime = System.currentTimeMillis();

                //  Create post object
                Post post = new Post(
                        dbManager.getDatabasePosts().push().getKey(),
                        mUserAuth,
                        message,
                        currentTime
                );

                DatabaseReference dbPost = dbManager.getDatabasePost(post.getPostid());

                //  Create post-history object
                PostHistory postHistory = new PostHistory(0, post.getMessage(), post.getDate());
                ArrayList postHistoryList = new ArrayList<PostHistory>();
                postHistoryList.add(postHistory);
                post.setHistories(postHistoryList);

                if (post.getImages() != null) {
                    Log.d(TAG, "avant images not null");
                }

                if (post.getAudio() != null) {
                    Log.d(TAG, "avant audio not null");
                }

                updateImages(post);
                updateAudio(post);

                if (post.getImages() != null) {
                    Log.d(TAG, "apres images not null");
                }
                if (post.getAudio() != null) {
                    Log.d(TAG, "apres audio not null");
                }

                dbManager.writePostToFirebase(post);
                dismiss();

            } else {

                Post post = mPostSelect;
                String message = mMessage.getText().toString();
                long currentTime = System.currentTimeMillis();

                ArrayList postHistoryList = post.getHistories();

                PostHistory postHistory = new PostHistory(
                        postHistoryList.size() + 1,
                        message,
                        currentTime
                );
                postHistoryList.add(postHistory);

                post.setHistories(postHistoryList);
                post.setMessage(message);

                updateImages(post);
                updateAudio(post);

                dbManager.writePostToFirebase(post);
                dismiss();

            }
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void updateImages(Post post) {

        if(mImageLayout.getVisibility() != View.GONE) {

            ArrayList<Image> postImageList = new ArrayList<>();
            Image image = new Image();
            image.setImageid("image" + post.getPostid() + Util.getRandomNumber());

            if(!mImageTitle.getText().toString().equals(""))
                image.setName(mImageTitle.getText().toString());
            else
                image.setName("");

            //TODO add genre

            postImageList.add(image);
            post.setImages(postImageList);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmapImage.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();

            StorageReference stPost = dbManager.getStoragePost(post.getPostid());

            try {

                if (post.getImages() == null)
                    throw new NullPointerException("There is no image in this post. ");

                if (post.getImages().isEmpty())
                    throw new IllegalArgumentException("The  list of image is empty");

                UploadTask uploadTask = stPost.child(post.getImages().get(0).getImageid()).putBytes(data);

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
                                dismiss();
                                Log.d(TAG, "image uploaded");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        });

            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }

        }
    }

    private void updateAudio(Post post) {

        if (mAudioLayout.getVisibility() != View.GONE) {

            //  Set audio data structure

            Audio audio = new Audio();

            audio.setAid("audio" + post.getPostid() + Util.getRandomNumber());
            audio.setCreatorid(post.getUser().getUid());
            if(!mAudioTitle.getText().toString().isEmpty())
                audio.setTitle(mImageTitle.getText().toString());
            else
                audio.setTitle("");

            if(mAudioImageView.getVisibility() == View.VISIBLE) {
                //audio.setPid();
            }

            post.setAudio(audio);

            //  Upload audio to firebase

            StorageReference stPost = dbManager.getStoragePost(post.getPostid());

            try {

                if (post.getAudio() == null)
                    throw new NullPointerException("There is no audio in this post. ");

                if (mAudioUri == null)
                    throw new NullPointerException("Audio uri is null");

                UploadTask uploadTask = stPost.child(post.getAudio().getAid()).putFile(mAudioUri);

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
                                dismiss();
                                Log.d(TAG, "audio uploaded");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                });

            } catch (NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }

        }

    }


    /**
     *
     *
     *      Validation
     *
     *
     */

    private boolean validateForm() {
        boolean valid = true;

        String post = mMessage.getText().toString();
        if (TextUtils.isEmpty(post)) {
            Toast.makeText(mActivity, "Il y n'y a aucun message", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            mMessage.setError(null);
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
    /*
    // TODO: Si le message n'est pas changé, assuré qu'il ne fasse pas de mise à jour (éviter de dupliqué donné)
    private void updateDB() {

        final DatabaseReference ref = DatabaseManager.getInstance().getReference();


        showProgressDialog();
        ref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren())
                {
                    mCurrentPost.setMessage(mMessage.getText().toString());

                    //ArrayList<PostHistory> history = mCurrentPost.getHistories();

                    /*PostHistory postHistory = new PostHistory(
                            history.size(),
                            mMessage.getText().toString(),
                            System.currentTimeMillis()
                    );
                    history.add(postHistory);
                    mCurrentPost.setHistories(history);
                    /*
                    DatabaseManager.getInstance().getReference(.child("posts")
                            .child(mCurrentPost.getPostid())
                            .setValue(mCurrentPost);
                    DatabaseManager.getInstance().getReference()
                            .child("user-post")
                            .child(mCurrentPost.getUser().getUid())
                            .child(mCurrentPost.getPostid())
                            .setValue(mCurrentPost);
                    Toast.makeText(getApplicationContext(), "Data modified", Toast.LENGTH_SHORT).show();
                }*//*
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
        hideProgressDialog();
        this.finish();
    }*/
}
