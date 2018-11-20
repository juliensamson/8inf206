package ca.uqac.lecitoyen.fragments.userUI;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.activities.CreateAndEditActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.dialogs.CreateDialog;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.views.ToolbarView;
import nl.changer.audiowife.AudioWife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ForumFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = ForumFragment.class.getSimpleName();

    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int AUDIO_REQUEST_CODE = 3;

    private final static String ARG_USER = "user";
    private final static String ARG_POSTS = "posts";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mLoadingBar;

    private iHandleFragment mHandleFragment;
    private MainUserActivity mainUserActivity;

    private CreateDialog mCreatePostDialog;

    private User mUserAuth;
    private Post mPost;

    private DatabaseManager dbManager;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseReference dbUsersData;
    private DatabaseReference dbPosts;
    private DatabaseReference dbPostsSocial;
    private Query mPostsQuery;

    private ToolbarView mForumToolbar;
    private FloatingActionMenu mAddPostMenu;
    private NestedScrollView mNestedScrollView;
    private RecyclerView mForumRecyclerView;
    //private RecyclerView.Adapter mNewsfeedAdapter;
    private RecyclerSwipeAdapter mForumAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Map<String,Post> mPublicationList = new HashMap<>();
    private ArrayList<Post> mPostsList = new ArrayList<>();
    private ArrayList<String> mPostUpvotes = new ArrayList<>();
    private ArrayList<String> mPostRepost = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();

    //  TODO: CREATE SINGLETRON


    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment newInstance(User userAuth, ArrayList<Post> posts) {
        ForumFragment fragment = new ForumFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, userAuth);
        args.putParcelableArrayList(ARG_POSTS, posts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        this.mainUserActivity = (MainUserActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.fbAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            mUserAuth = (User) getArguments().getSerializable(ARG_USER);
            mPostsList = getArguments().getParcelableArrayList(ARG_POSTS);
            mForumAdapter = new SwipePostAdapter(mainUserActivity, mUserAuth, mPostsList);
            //mPostsList.clear();
            //mForumAdapter = mainUserActivity.getForumAdapter();
            //mPostsList = mainUserActivity.getPostsList();
        } else {
            if(savedInstanceState != null) {
                mUserAuth = (User) savedInstanceState.getSerializable(ARG_USER);
            } else {
                Log.e(TAG, "SavedInstanceState is null");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        //  Toolbar
        mForumToolbar = view.findViewById(R.id.forum_toolbar);
        mForumToolbar.defaultToolbar(
                mainUserActivity,
                ToolbarView.GRAVITY_END,
                getResources().getString(R.string.fragment_forum),
                dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid())
        );

        //  Views
        mAddPostMenu = view.findViewById(R.id.forum_add_menu);
        mSwipeRefreshLayout = view.findViewById(R.id.forum_refresh_layout);
        mForumRecyclerView = view.findViewById(R.id.newsfeed_recycler_view);
        mForumRecyclerView.setNestedScrollingEnabled(false);

        //  Button
        view.findViewById(R.id.forum_add_post).setOnClickListener(this);
        view.findViewById(R.id.forum_add_images).setOnClickListener(this);
        view.findViewById(R.id.forum_add_audio).setOnClickListener(this);

        //  Set recycler view
        mLayoutManager = new LinearLayoutManager(mainUserActivity);
        mForumRecyclerView.setLayoutManager(mLayoutManager);

        Log.e(TAG, "Adapter " + mainUserActivity.getForumAdapter().toString());

        if(mForumAdapter != null)
            mForumRecyclerView.setAdapter(mForumAdapter);

        mForumToolbar.onImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUserAuth != null) {
                    UserProfileFragment fragment = UserProfileFragment.newInstance(mUserAuth);
                    mainUserActivity.doUserProfileTransaction(fragment, MainUserActivity.AUTH_USER);
                }
            }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainUserActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mForumAdapter = null;
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    break;
                //case CAMERA_REQUEST_CODE:    //TODO: Make this work somehow
                    //checkInternalStorage();
                    //updateStorage(imageUri);
                //    break;
                case AUDIO_REQUEST_CODE:
                    CreateDialog audioDialog = CreateDialog.newInstance(CreateDialog.AUDIO_POST_TYPE, mUserAuth);
                    break;
                default:
                    break;
            }
        } else if (requestCode == RESULT_CANCELED) {
            Log.e(TAG, "Some error occured");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.forum_add_post:
                mCreatePostDialog =CreateDialog.newInstance(CreateDialog.IMAGE_POST_TYPE, mUserAuth);
                mCreatePostDialog.show(mainUserActivity.getSupportFragmentManager(), getTag());
                mAddPostMenu.close(false);
                break;
            case R.id.forum_add_images:
                openGallery();
                mAddPostMenu.close(false);
                break;
            case R.id.forum_add_audio:
                openStorage();
                mAddPostMenu.close(false);
                break;
            default:
                break;
        }
    }

    //TODO: Only update when refreshing. Work only once because listener is not removed
    private void updateUI() {

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {
                    long startAt = mPostsList.get(0).getDateInverse() - 1000;

                    Query query = dbManager.getDatabasePosts().orderByChild("dateInverse").endAt(startAt);
                    ChildEventListener listener = query.addChildEventListener(childEventListener());

                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, e.getMessage());
                }


            }
        });

        mSwipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                parent.setRefreshing(false);
                return false;
            }
        });

    }

    private ChildEventListener childEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mPostsList.add(0, dataSnapshot.getValue(Post.class));
                mForumAdapter.notifyItemInserted(0);
                Log.e(TAG, "Post add " + dataSnapshot.getValue(Post.class).getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(mainUserActivity, "Feed is up to date", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "Post change " + dataSnapshot.getValue(Post.class).getMessage());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "Post remove " + dataSnapshot.getValue(Post.class).getMessage());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "Post move " + dataSnapshot.getValue(Post.class).getMessage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        if (openGalleryIntent.resolveActivity(mainUserActivity.getPackageManager()) != null) {
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
                if (openStorage.resolveActivity(mainUserActivity.getPackageManager()) != null) {
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

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(String something);

    }
}
