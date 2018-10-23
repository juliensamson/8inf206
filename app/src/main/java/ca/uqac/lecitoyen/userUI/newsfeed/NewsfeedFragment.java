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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapter.FeedAdapter;
import ca.uqac.lecitoyen.adapter.PublicationAdapter;
import ca.uqac.lecitoyen.database.PostModification;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;
import me.shaohui.bottomdialog.BottomDialog;

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
    private Query mPostsQuery;

    private RecyclerView mNewsfeedRecyclerView;
    private RecyclerView.Adapter mNewsfeedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Post> mPublicationList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();


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

        //  Get data
        //mPublicationList = userMainActivity.getPublicationList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);
        Log.d(TAG, "onCreateView");

        //  Toolbar
        //mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(view, userMainActivity, R.id.toolbar_newsfeed, getTag(), false);

        //  View
        mNewsfeedRecyclerView = view.findViewById(R.id.newsfeed_recycler_view);
        mNewsfeedRecyclerView.setNestedScrollingEnabled(false);

        //  Button
        view.findViewById(R.id.newsfeed_add_message).setOnClickListener(this);

        //  Set recycler view
        mLayoutManager = new LinearLayoutManager(getActivity());
        mNewsfeedRecyclerView.setLayoutManager(mLayoutManager);

        //mNewsfeedAdapter = new PublicationAdapter(getContext(), mPublicationList);
        //mNewsfeedRecyclerView.setAdapter(mNewsfeedAdapter);

        Log.d(TAG, "onCreateView" + mPublicationList.size());

        //else
        //    Log.e(TAG, "publication list is empty");

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
                mPostsQuery = dbPosts.orderByChild("inverseDate").limitToLast(1);

                dbPosts.orderByChild("inverseDate").limitToFirst(5).addListenerForSingleValueEvent(readPublicationListOnce());
                //updateUI();
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
                //showBottomDialog();
                startActivity(new Intent(getContext(), PostActivity.class));
                break;
            default:
                break;
        }
    }

    private void updateUI() {
        //dbPosts.addValueEventListener(loadUserPostData());
        ///dbPosts.addChildEventListener(readPostsUpdate());
        //dbPosts.
        //mPostsQuery.addChildEventListener(readPostsUpdate());
    }

    @SuppressWarnings("unchecked")
    private void updateDB(final Post post) {

        dbUsersData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren())
                {
                    long currentTime = System.currentTimeMillis();
                    User userData = dataSnapshot.child(fbUser.getUid()).getValue(User.class);

                    if(userData != null) {
                        post.setUid(userData.getUid());
                        post.setDate(currentTime);

                        List modifications = new ArrayList<PostModification>();
                        PostModification postModification = new PostModification(
                                0,
                                post.getPost(),
                                currentTime
                        );

                        modifications.add(postModification);
                        post.setModifications(modifications);

                        //dbManager.writePost(dbManager.getReference(), post);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
        hideProgressDialog();
    }

    private ValueEventListener readPublicationListOnce() {
        Log.d(TAG, "readPublicationListOnce");
        showProgressDialog();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPublicationList.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()) {
                    mPublicationList.add(post.getValue(Post.class));
                }
                mNewsfeedAdapter = new PublicationAdapter(getContext(), mPublicationList);
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

    private ChildEventListener readPostsUpdate() {
        Log.d(TAG, "readPostsUpdate");
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //mPublicationsList.clear();
                //mNewsfeedAdapter.notifyItemInserted(0);
                Post newPost = dataSnapshot.getValue(Post.class);

                if (newPost != null) {
                    Log.d(TAG, newPost.getUid() + " " +newPost.getPost() );
                    //mPublicationsList.add(newPost);
                } else {
                    Log.e(TAG, "post is empty");
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
