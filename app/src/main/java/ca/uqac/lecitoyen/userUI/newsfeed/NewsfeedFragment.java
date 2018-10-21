package ca.uqac.lecitoyen.userUI.newsfeed;


import android.content.Context;
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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private UserMainActivity activity;

    private Post mPost;

    private DatabaseManager dbManager;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseReference dbUsersData;
    private Query dbPostsOrderByDate;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Post> postList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();


    public NewsfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (UserMainActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.fbAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_newsfeed, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());

        //  View
        mRecyclerView = view.findViewById(R.id.newsfeed_recycler_view);

        //  Button
        view.findViewById(R.id.newsfeed_add_message).setOnClickListener(this);

        //  Set recycler view
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

       // mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(fbAuth != null) {

            fbUser = fbAuth.getCurrentUser();

            if(fbUser != null) {

                String uid = fbUser.getUid();

                //  Get database & storage reference
                dbUsersData = dbManager.getDatabaseUsers();
                dbPostsOrderByDate = dbManager.getDatabasePostsOrderByDate();
                //dbUserProfilPicture = dbManager.getDatabaseUserProfilPicture(uid);
                //dbUserPost = dbManager.getDatabaseUserPost(uid);
                //stUserProfilPicture = dbManager.getStorageUserProfilPicture(uid);

                updateUI();
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
                showBottomDialog();
                //startActivity(new Intent(getContext(), PostActivity.class));
                break;
            default:
                break;
        }
    }

    private void showBottomDialog() {

        final BottomDialog bottomDialog = BottomDialog.create(getFragmentManager());
        bottomDialog.setViewListener(new BottomDialog.ViewListener() {
                    @Override
                    public void bindView(View v) {
                        final EditText postMessage = v.findViewById(R.id.dialog_post_message);
                        ImageButton sendButton = v.findViewById(R.id.dialog_post_send);

                        sendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(postMessage.getText() != null && !postMessage.getText().toString().equals("")) {
                                    Post post = new Post();
                                    post.setPost(postMessage.getText().toString());
                                    updateDB(post);
                                    bottomDialog.dismiss();
                                } else {
                                    Toast.makeText(activity, "No text", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setLayoutRes(R.layout.dialog_layout)
                .setDimAmount(0.2f)            // Dialog window dim amount(can change window background color）, range：0 to 1，default is : 0.2f
                .setCancelOutside(true)     // click the external area whether is closed, default is : true
                .setTag("BottomDialog")     // setting the DialogFragment tag
                .show();
    }

    private void updateUI() {
        //  Load user-data
        //dbUsersData.addListenerForSingleValueEvent();
        //  Load posts
        dbPostsOrderByDate.addValueEventListener(loadUserPostData());

    }

    @SuppressWarnings("unchecked")
    private void updateDB(final Post post) {

        showProgressDialog();
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

                        dbManager.writePost(dbManager.getReference(), post);
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

    private ValueEventListener loadUserPostData() {
        return new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postList.clear();

                    final long[] pendingLoadCount = { dataSnapshot.getChildrenCount() };

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        postList.add(postSnapshot.getValue(Post.class));
                        pendingLoadCount[0] = pendingLoadCount[0] - 1;
                    }


                    if (pendingLoadCount[0] == 0) {
                        mAdapter = new FeedAdapter(getContext(), fbUser, postList, activity.getUserList());
                        mRecyclerView.setAdapter(mAdapter);
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
