package ca.uqac.lecitoyen.dialogs;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
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
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.fragments.userUI.ForumFragment;
import ca.uqac.lecitoyen.models.Audio;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Image;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.PostHistory;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.util.ImageHandler;
import ca.uqac.lecitoyen.util.MultimediaView;
import ca.uqac.lecitoyen.util.Util;
import ca.uqac.lecitoyen.views.ToolbarView;
import de.hdodenhof.circleimageview.CircleImageView;
import nl.changer.audiowife.AudioWife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class CreateDialog extends BottomSheetDialogFragment implements View.OnClickListener, BottomSheetDialog.OnDismissListener {

    private static final String TAG = CreateDialog.class.getSimpleName() ;

    private static final String ARG_POST_TYPE = "post-type";
    private static final String ARG_USER = "user";

    public static final int MESSAGE_POST_TYPE = 665;
    public static final int IMAGE_POST_TYPE = 666;
    public static final int AUDIO_POST_TYPE = 667;

    private static final int CREATE_POST_LAYOUT = 999;
    private static final int CREATE_EVENT_LAYOUT = 1000;

    private CreateDialog mCreateDialog;

    private int layoutInflated = 0;

    private Activity mActivity;
    private Fragment mFragment;
    private ImageHandler mImageHandler;
    private RecyclerSwipeAdapter mAdpater;

    private BottomSheetDialog mBottomSheetDialog;
    private View mRootView;

    private Bitmap mBitmapImage;
    private Uri mAudioUri;

    //  Views
    private ToolbarView mToolbar;
    private TextView mRemoveLayout;
    private EditText mMessage;
    private EditText mImageTitle, mImageGenre, mAddImageEditText;
    private EditText mAudioTitle, mAudioGenre, mAddAudioEditText, mAddAudioImage;
    private LinearLayout mImageLayout, mAudioLayout;
    private FloatingActionMenu mAddLayoutMenu;
    private FloatingActionButton mAddImageButton;

    private FrameLayout mPlayerView;
    private MultimediaView mImageView, mAudioImageView;

    private DatabaseManager dbManager;
    private int  mPostType;
    private User mUserAuth;

    public CreateDialog() {}

    public static CreateDialog newInstance(int postType, User user) {
        CreateDialog createDialog = new CreateDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_TYPE, postType);
        args.putParcelable(ARG_USER, user);
        createDialog.setArguments(args);
        return createDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.mImageHandler = ImageHandler.getInstance();

        if(getArguments() != null) {
            mPostType = getArguments().getInt(ARG_POST_TYPE);
            mUserAuth = getArguments().getParcelable(ARG_USER);
        }

        if(mBottomSheetDialog == null)
            mBottomSheetDialog = new BottomSheetDialog(mActivity);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_bottom_create, container, false);

        //  Toolbar
        mToolbar = rootView.findViewById(R.id.create_post_toolbar);
        mToolbar.buttonToolbar(mActivity, "Publier");
        mToolbar.onButtonClickListener(this);
        mToolbar.onCloseClickListener(this);

        //  Main Views
        CircleImageView profileImage = rootView.findViewById(R.id.create_post_profile_picture);
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
        //if(postType == AUDIO_POST_TYPE && uri != null) {
            //mRemoveLayout.setVisibility(View.VISIBLE);
            //mAudioLayout.setVisibility(View.VISIBLE);
            //AudioWife.getInstance().init(mActivity, uri).useDefaultUi(mPlayerView, mActivity.getLayoutInflater());
        //}

        //  Button
        rootView.findViewById(R.id.create_post_remove_layout).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_add_image_layout).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_add_audio_layout).setOnClickListener(this);

        rootView.findViewById(R.id.create_post_add_image).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_image_add_genre).setOnClickListener(this);

        rootView.findViewById(R.id.create_post_audio_add_audio).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_audio_add_image).setOnClickListener(this);
        rootView.findViewById(R.id.create_post_audio_add_genre).setOnClickListener(this);

        //RecyclerView mImagesRecyclerView = messageView.findViewById(R.id.create_post_images);
        //LinearLayoutManager llm = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        //mImagesRecyclerView.setHasFixedSize(true);
        //mImagesRecyclerView.setLayoutManager(llm);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
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

    /**
     *
     *
     *      Private methods
     *
     *
     */

    private void setImageUri(Uri uri) {

        try {

            mBitmapImage = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), uri);

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
            Log.e(TAG, e.getMessage());
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

            updateImages(post);
            updateAudio(post);

            dbManager.writePostToFirebase(post);
            dismiss();

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void updateImages(Post post) {

        if(mImageView.getVisibility() != View.GONE) {

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

        if (mPlayerView.getVisibility() != View.GONE) {

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
}
