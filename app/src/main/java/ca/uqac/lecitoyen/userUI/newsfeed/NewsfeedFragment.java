package ca.uqac.lecitoyen.userUI.newsfeed;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


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

    private RecyclerView mNewsfeedRecyclerView;
    private RecyclerView.Adapter mNewsfeedAdapter;
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

        //  Toolbar
        setFragmentToolbar(view, userMainActivity, R.id.toolbar_newsfeed, getTag(), false);

        //  View
        mNewsfeedRecyclerView = view.findViewById(R.id.newsfeed_recycler_view);
        mNewsfeedRecyclerView.setNestedScrollingEnabled(false);

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
                mPostsQuery = dbPosts.orderByChild("date-inverse").limitToLast(1);

                dbPosts.orderByChild("date-inverse").limitToFirst(10).addListenerForSingleValueEvent(readPublicationListOnce());
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

    @SuppressWarnings("unchecked")
    private void updateDB(final Post post) {

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
                        DatabaseReference dbUserUpvotes = dbManager.getDatabaseUserUpvotes(fbUser.getUid());
                        dbUserUpvotes.addValueEventListener(readUserUpvotes());
                        mPublicationList.add(postSnapshot.getValue(Post.class));
                    }
                }
                DatabaseReference dbUserUpvotes = dbManager.getDatabaseUserUpvotes(fbUser.getUid());
                dbUserUpvotes.addValueEventListener(readUserUpvotes());

                mNewsfeedAdapter = new PublicationAdapter(getContext(), fbUser, mPublicationList);
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

    private ValueEventListener readUserUpvotes() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e(TAG, String.valueOf(dataSnapshot.getChildrenCount()));

                for (DataSnapshot postUpvotedByUser : dataSnapshot.getChildren()) {
                    Post postUpvoted = postUpvotedByUser.getValue(Post.class);
                    if(postUpvoted != null) {
                        Log.e(TAG, postUpvoted.getPostid());

                    }
                }
                /*ArrayList<Post> listUpvoted = new ArrayList<>();
                for (DataSnapshot postUpvotedByUser : dataSnapshot.getChildren()) {
                    listUpvoted.add(postUpvotedByUser.getValue(Post.class));
                }
                Log.i(TAG, String.valueOf(mPostList.size()));
                for (int i = 0; i < mPostList.size(); i++)
                {
                    for(int j = 0; j < listUpvoted.size(); j++)
                    {
                        if (mPostList.get(i).getPostid().equals(listUpvoted.get(j).getPostid())) {
                            Log.e(TAG, String.valueOf(isUpvoteByUser));
                            Log.e(TAG, mPostList.get(i).getPostid());
                            Log.e(TAG, listUpvoted.get(j).getPostid());
                            setUpvoteButtonOn(holder);
                            break;
                        } else {
                            Log.e(TAG, String.valueOf(isUpvoteByUser));
                            setUpvoteButtonOff(holder);
                        }
                    }
                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
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

    private ValueEventListener loadUserData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    userList.add(userSnapshot.getValue(User.class));
                    Log.e(TAG, "Size UserList: " + userList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "loadUserData failed " + databaseError.getMessage());
            }
        };
    }


}
