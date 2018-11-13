package ca.uqac.lecitoyen.dialogs;


import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Image;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.PostHistory;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.MultimediaView;
import ca.uqac.lecitoyen.util.Util;
import ca.uqac.lecitoyen.views.ToolbarView;
import nl.changer.audiowife.AudioWife;

public class CreateDialog implements View.OnClickListener, BottomSheetDialog.OnDismissListener {

    private static final String TAG = CreateDialog.class.getSimpleName() ;

    public static final int MESSAGE_POST_TYPE = 665;
    public static final int IMAGE_POST_TYPE = 666;
    public static final int AUDIO_POST_TYPE = 667;

    private static final int CREATE_POST_LAYOUT = 999;
    private static final int CREATE_EVENT_LAYOUT = 1000;

    private int layoutInflated = 0;

    private Activity mActivity;
    private Fragment mFragment;
    private RecyclerSwipeAdapter mAdpater;
    private ArrayList<Post> mPostsList;

    private BottomSheetDialog mBottomSheetDialog;
    private View mRootView;

    //  Views
    private ToolbarView mToolbar;
    private EditText mMessageEditText;
    private EditText mAddImageEditText;

    private DatabaseManager dbManager;
    private User mUserAuth;

    public CreateDialog(Fragment fragment, User user) {

        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.mUserAuth = user;

        if(mBottomSheetDialog == null)
            mBottomSheetDialog = new BottomSheetDialog(mActivity);

    }

    public CreateDialog(Fragment fragment, User user, RecyclerSwipeAdapter adapter, ArrayList<Post> posts) {

        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.mUserAuth = user;
        this.mAdpater = adapter;
        this.mPostsList = posts;

        if(mBottomSheetDialog == null)
            mBottomSheetDialog = new BottomSheetDialog(mActivity);

    }

    /**
     *
     *
     *      Public methods
     *
     *
     */

    public CreateDialog createPostView(int postType, Uri uri) {

        switch (postType) {

            case MESSAGE_POST_TYPE:
                mRootView = setMessagePostType();
                break;
            case IMAGE_POST_TYPE:
                mRootView = setImagePostType(uri);
                break;
            case AUDIO_POST_TYPE:
                mRootView = setAudioPostType(uri);
                break;
            default:
                mRootView = new View(mActivity);
                break;
        }

        layoutInflated = CREATE_POST_LAYOUT;

        //  General Views
        mToolbar = mRootView.findViewById(R.id.create_post_toolbar);
        mToolbar.buttonToolbar(mFragment, "Publier");

        //  General Button
        mToolbar.onButtonClickListener(this);
        mToolbar.onCloseClickListener(this);

        //  Set bottom sheet view
        mBottomSheetDialog.setContentView(mRootView);
        setBottomSheetHeight(mRootView);

        return this;
    }


    public CreateDialog createEventView() {
        View view = View.inflate(mActivity, R.layout.dialog_bottom_create_event, null);

        layoutInflated = CREATE_EVENT_LAYOUT;

        //  Views
        mToolbar = view.findViewById(R.id.create_event_toolbar);
        mToolbar.buttonToolbar(mFragment, "Créer");

        //  Button
        mToolbar.onButtonClickListener(this);
        mToolbar.onCloseClickListener(this);
        view.findViewById(R.id.create_event_add_image).setOnClickListener(this);


        //  Set bottom sheet view
        mBottomSheetDialog.setContentView(view);
        setBottomSheetHeight(view);

        return this;
    }

    public void show() {
        mBottomSheetDialog.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.toolbar_view_close:
                onDismiss(mBottomSheetDialog);
                break;
            case R.id.toolbar_view_button:
                Log.d(TAG, "create event click");
                updateDB();
                break;
        }

        switch (layoutInflated) {
            case CREATE_EVENT_LAYOUT:
                setEventOnClick(view);
                break;
            case CREATE_POST_LAYOUT:
                setPostOnClick(view);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    /**
     *
     *
     *      Private methods
     *
     *
     */

    private View setMessagePostType() {
        View messageView = View.inflate(mActivity, R.layout.dialog_bottom_create_post, null);
        mMessageEditText = messageView.findViewById(R.id.create_post_message);
        return messageView;
    }

    private View setImagePostType(Uri uri) {
        View imageView = View.inflate(mActivity, R.layout.dialog_bottom_create_post, null);
        MultimediaView mMultimediaView = imageView.findViewById(R.id.create_post_multimedia);
        mMultimediaView.setEditable(true).loadImages(uri).setFullHeight();

        return imageView;
    }

    private View setAudioPostType(Uri uri) {
        View audioView = View.inflate(mActivity, R.layout.dialog_bottom_create_post_audio, null);

        FrameLayout playerView = audioView.findViewById(R.id.create_post_audio_player);
        AudioWife.getInstance()
                .init(mActivity, uri)
                .useDefaultUi(playerView, mActivity.getLayoutInflater());

        return audioView;
    }

    private void setBottomSheetHeight(View view) {
        View parentView = (View) view.getParent();
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parentView);
        bottomSheetBehavior.setPeekHeight(Util.getScreenHeightPixel(mActivity));
    }

    private void setEventOnClick(View view) {

        switch (view.getId()) {
            case R.id.create_event_add_image:
                ImageBottomDialog dialog = new ImageBottomDialog(mActivity);
                break;
        }
    }

    private void setPostOnClick(View view) {

        switch (view.getId()) {

        }
    }

    private void updateDB() {

        if (!validateForm()) {
            return;
        }

        if (mUserAuth != null) {
            String message = mMessageEditText.getText().toString();
            long currentTime = System.currentTimeMillis();

            //  Create post object
            Post post = new Post(
                    dbManager.getDatabasePosts().push().getKey(),
                    mUserAuth,
                    message,
                    currentTime
            );

            //  Create post-history object
            ArrayList postHistoryList = new ArrayList<PostHistory>();
            PostHistory postHistory = new PostHistory(
                    0,
                    message,
                    currentTime
            );
            //  Add publications history to post
            postHistoryList.add(postHistory);
            post.setHistories(postHistoryList);

            /*stPosts = dbManager.getStoragePost(post.getPostid());
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
            }*/
            //  Add data to fire base
            Log.e(TAG, "Size " + mPostsList.size());
            //mPostsList.add(0, post);
            //mAdpater.notifyItemInserted(0);

            //mAdpater.notifyDataSetChanged();

            dbManager.writePostToFirebase(post);
            onDismiss(mBottomSheetDialog);

        } else {
            Log.e(TAG, "mUserdata empty");
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String post = mMessageEditText.getText().toString();
        if (TextUtils.isEmpty(post)) {
            Toast.makeText(mActivity, "Il y n'y a aucun message", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            mMessageEditText.setError(null);
        }

        return valid;
    }
}
