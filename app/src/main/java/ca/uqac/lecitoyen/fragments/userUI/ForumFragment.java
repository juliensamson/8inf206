package ca.uqac.lecitoyen.fragments.userUI;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.activities.CreatePostActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.helpers.RecyclerTouchListener;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;

public class ForumFragment extends BaseFragment implements View.OnClickListener {

    final private static String TAG = "ForumFragment";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mLoadingBar;

    private iHandleFragment mHandleFragment;
    private MainUserActivity mainUserActivity;

    private Post mPost;

    private DatabaseManager dbManager;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseReference dbUsersData;
    private DatabaseReference dbPosts;
    private DatabaseReference dbPostsSocial;
    private Query mPostsQuery;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        this.mainUserActivity = (MainUserActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.fbAuth = FirebaseAuth.getInstance();
        //if(fbAuth.getCurrentUser() != null)
        //    dbManager.getReference().addListenerForSingleValueEvent(initPostsList(fbAuth.getCurrentUser(), mPostsList));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        //  Views
        mSwipeRefreshLayout = view.findViewById(R.id.forum_refresh_layout);
        mForumRecyclerView = view.findViewById(R.id.newsfeed_recycler_view);
        mForumRecyclerView.setNestedScrollingEnabled(false);

        //  Button
        view.findViewById(R.id.newsfeed_add_message).setOnClickListener(this);

        //  Set recycler view
        mLayoutManager = new LinearLayoutManager(mainUserActivity);
        mForumRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(fbAuth != null) {

            FirebaseUser fbUser = fbAuth.getCurrentUser();

            if(fbUser != null) {
                //  Get database & storage reference
                dbPosts = dbManager.getDatabasePosts();
                dbUsersData = dbManager.getDatabaseUsers();
                DatabaseReference dbRef = dbManager.getReference();
                DatabaseReference dbPosts = dbManager.getDatabasePosts();
                updateUI(fbUser, dbRef);
             }
        } else {
            Log.e(TAG, "auth is null");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainUserActivity) getActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.newsfeed_add_message:
                startActivity(new Intent(getContext(), CreatePostActivity.class));
                break;
            default:
                break;
        }
    }

    private void updateUI(final FirebaseUser user, final DatabaseReference dbRef) {

        final ArrayList<Post> postsList = new ArrayList<>();

        //Log.e(TAG, mForumRecyclerView.getAdapter().toString());
        //if(mForumRecyclerView.getAdapter() == null)
        dbManager.getReference().addListenerForSingleValueEvent(initPostsList(user, postsList));



        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                int postLastPostLoaded = postsList.size() - 1;
                Post lastPostLoaded = postsList.get(postLastPostLoaded);

                long currentTime = System.currentTimeMillis();

                dbManager.getDatabasePosts()
                        .startAt(lastPostLoaded.getDate())
                        .addChildEventListener(childEventListener(postsList));

                /*while (true) {
                    if(System.currentTimeMillis() >= currentTime + 5000) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    }
                }*/
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

    private ValueEventListener initPostsList(final FirebaseUser fbUser, final ArrayList<Post> postsList) {
        Log.d(TAG, "readPublicationListOnce");
        showProgressDialog();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //  Get the current user data
                DataSnapshot userSnapshot = dataSnapshot.child("users").child(fbUser.getUid());
                final User user =  userSnapshot.getValue(User.class);

                //  List all the posts in order of dateInverse & set Adapater
                Query dbPosts = dataSnapshot.getRef().child("posts").orderByChild("dateInverse");
                dbPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final long[] pendingLoadCount = { dataSnapshot.getChildrenCount() };

                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Post post = postSnapshot.getValue(Post.class);
                            if(post != null) {
                                postsList.add(post);
                                pendingLoadCount[0] = pendingLoadCount[0] - 1;
                            }
                        }
                        if(user != null) {
                            mForumAdapter = new SwipePostAdapter(mainUserActivity, user, postsList);
                            mForumRecyclerView.setAdapter(mForumAdapter);
                        }
                        if (pendingLoadCount[0] == 0) {
                            //make custum dialog bar with progresss
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                hideProgressDialog();
            }
        };
    }

    private ChildEventListener childEventListener(final ArrayList<Post> postsList) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Added " + dataSnapshot.toString());
                mForumAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Changed " + dataSnapshot.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Removed " + dataSnapshot.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Moved " + dataSnapshot.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        };
    }
}
