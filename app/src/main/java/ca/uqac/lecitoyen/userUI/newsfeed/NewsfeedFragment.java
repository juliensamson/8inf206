package ca.uqac.lecitoyen.userUI.newsfeed;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapter.PublicationAdapter;
import ca.uqac.lecitoyen.adapter.SwipePostAdapter;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;

public class NewsfeedFragment extends BaseFragment implements View.OnClickListener {

    final private static String TAG = "NewsfeedFragment";

    private ProgressBar mLoadingBar;

    private iHandleFragment mHandleFragment;
    private UserMainActivity userMainActivity;

    private Post mPost;

    private DatabaseManager dbManager;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseReference dbUsersData;
    private DatabaseReference dbPosts;
    private DatabaseReference dbPostsSocial;
    private Query mPostsQuery;

    private NestedScrollView mNestedScrollView;
    private RecyclerView mNewsfeedRecyclerView;
    //private RecyclerView.Adapter mNewsfeedAdapter;
    private RecyclerSwipeAdapter mNewsfeedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Post> mPublicationList = new ArrayList<>();
    private ArrayList<String> mPostUpvotes = new ArrayList<>();
    private ArrayList<String> mPostRepost = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();

    //  TODO: CREATE SINGLETRON


    public NewsfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        this.userMainActivity = (UserMainActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.fbAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        Log.d(TAG, "onCreateView");

        /*PullRefreshLayout layout = view.findViewById(R.id.newsfeed_refresh_layout);
        layout.setRefreshStyle(PullRefreshLayout.STYLE_MATERIAL);

        // listen refresh event
        layout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(userMainActivity, "Refreshing", Toast.LENGTH_SHORT).show();
                // refresh complete
            }
        });*/
        // refresh complete
        //layout.setRefreshing(false);
        //  Toolbar
        //Toolbar toolbar = view.findViewById(R.id.toolbar_newsfeed);
        //setFragmentToolbar(view, userMainActivity, R.id.toolbar_newsfeed, getTag(), false);

        //AppBarLayout appBarLayout = view.findViewById(R.id.toolbar_newsfeed_layout);
        //android.widget.Toolbar toolbar = view.findViewById(R.id.toolbar_newsfeed_1);
        //TextView title = view.findViewById(R.id.toolbar_newsfeed_title);
        //title.setText(getTag());

        //userMainActivity.setActionBar(toolbar);
        //userMainActivity.setSupportActionBar(toolbar);


        //  View
        //mNestedScrollView = view.findViewById(R.id.profil_nested_scroll_view);
        //  Put the view to the top
        //mNestedScrollView.getParent().requestChildFocus(mNestedScrollView, mNestedScrollView);
        mNewsfeedRecyclerView = view.findViewById(R.id.newsfeed_recycler_view);
        mNewsfeedRecyclerView.setNestedScrollingEnabled(false);
        //mNewsfeedRecyclerView.getParent().requestChildFocus(toolbar, toolbar);


        //  Button
        view.findViewById(R.id.newsfeed_add_message).setOnClickListener(this);

        //  Set recycler view
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNewsfeedRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Start");
        Log.d(TAG, "Start" + mPublicationList.size());
        if(fbAuth != null) {

            fbUser = fbAuth.getCurrentUser();

            if(fbUser != null) {

                String uid = fbUser.getUid();
                //  Get database & storage reference
                dbPosts = dbManager.getDatabasePosts();
                dbUsersData = dbManager.getDatabaseUsers();
                mPostsQuery = dbPosts.orderByChild("dateInverse").limitToLast(1);

                dbPosts.orderByChild("dateInverse").limitToFirst(10).addListenerForSingleValueEvent(readPublicationListOnce());
            }
        } else {
            Log.e(TAG, "auth is null");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (UserMainActivity) getActivity();
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

    private ValueEventListener readPublicationListOnce() {
        Log.d(TAG, "readPublicationListOnce");
        showProgressDialog();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPublicationList.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if(post != null) {
                        mPublicationList.add(postSnapshot.getValue(Post.class));
                    }
                }

                //mNewsfeedAdapter = new PublicationAdapter(getContext(), fbUser, mPublicationList);
                //mNewsfeedRecyclerView.setAdapter(mNewsfeedAdapter);
                mNewsfeedAdapter = new SwipePostAdapter(getContext(), fbUser, mPublicationList);
                mNewsfeedRecyclerView.setAdapter(mNewsfeedAdapter);
                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                hideProgressDialog();
            }
        };
    }

    private ValueEventListener loadUserPostData() {
        return new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mPublicationList.clear();

                    final long[] pendingLoadCount = { dataSnapshot.getChildrenCount() };

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        mPublicationList.add(postSnapshot.getValue(Post.class));
                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                    }


                    if (pendingLoadCount[0] == 0) {
                        //mNewsfeedAdapter = new FeedAdapter(getContext(), fbUser, mPublicationsList, userMainActivity.getUserList());
                        mNewsfeedRecyclerView.setAdapter(mNewsfeedAdapter);
                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "loadUserPostData failed " + databaseError.getMessage());
                }
        };
    }

}
