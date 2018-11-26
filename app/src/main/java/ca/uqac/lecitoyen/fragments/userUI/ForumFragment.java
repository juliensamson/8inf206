package ca.uqac.lecitoyen.fragments.userUI;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.dialogs.CreateDialog;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.views.ToolbarView;

public class ForumFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = ForumFragment.class.getSimpleName();

    private final static String ARG_USER = "user";
    private final static String ARG_POSTS = "posts";

    private iHandleFragment mHandleFragment;
    private MainUserActivity mainUserActivity;

    //data structure
    private DatabaseManager dbManager;
    private User mUserAuth;
    private ArrayList<Post> mPostsList = new ArrayList<>();

    //View
    private ToolbarView mForumToolbar;
    private RecyclerView mForumRecyclerView;
    private RecyclerSwipeAdapter mForumAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;


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
        this.mainUserActivity = (MainUserActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();

        if (getArguments() != null) {
            mUserAuth = (User) getArguments().getSerializable(ARG_USER);
            mPostsList = getArguments().getParcelableArrayList(ARG_POSTS);
            mForumAdapter = new SwipePostAdapter(mainUserActivity, mUserAuth, mPostsList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        //  Toolbar
        mForumToolbar = view.findViewById(R.id.forum_toolbar);
        mSwipeRefreshLayout = view.findViewById(R.id.forum_refresh_layout);
        mForumRecyclerView = view.findViewById(R.id.newsfeed_recycler_view);

        //  Button
        mForumToolbar.onImageClickListener(this);
        view.findViewById(R.id.forum_add_post).setOnClickListener(this);

        //  Set recycler view
        LinearLayoutManager lm = new LinearLayoutManager(mainUserActivity);
        mForumRecyclerView.setLayoutManager(lm);
        mForumRecyclerView.setAdapter(mForumAdapter);

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
    public void onClick(View view) {

        try {

            if(mUserAuth == null)
                throw new NullPointerException("User auth is null");

            switch (view.getId()) {
                case R.id.toolbar_view_image_view:
                    UserProfileFragment fragment = UserProfileFragment.newInstance(Constants.FROM_PROFILE, mUserAuth);
                    mainUserActivity.doUserProfileTransaction(fragment, MainUserActivity.AUTH_USER);
                    break;
                case R.id.forum_add_post:
                    CreateDialog createPostDialog = CreateDialog.newInstance(null, mUserAuth);
                    createPostDialog.show(mainUserActivity.getSupportFragmentManager(), getTag());
                    break;
                default:
                    break;
            }

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    //TODO: Only update when refreshing. Work only once because listener is not removed
    private void updateUI() {

        mForumToolbar.defaultToolbar(
                mainUserActivity,
                ToolbarView.GRAVITY_END,
                getResources().getString(R.string.fragment_forum),
                dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid())
        );

        mForumRecyclerView.setNestedScrollingEnabled(false);

        long startAt = mPostsList.get(0).getDateInverse() - 1000;

        Query query = dbManager.getDatabasePosts().orderByChild("dateInverse").endAt(startAt);
        ChildEventListener listener = query.addChildEventListener(childEventListener());

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {

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
                if(mForumAdapter != null)
                    mForumAdapter.notifyItemInserted(0);
                Log.e(TAG, "Post add " + dataSnapshot.getValue(Post.class).getMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                mainUserActivity.sendNotification(dataSnapshot.getValue(Post.class));
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

    /**
     *
     *      Notification
     *
     */

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(String something);

    }
}
