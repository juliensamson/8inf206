package ca.uqac.lecitoyen.userUI.newsfeed;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.adapter.HomeAdapter;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;

public class HomeFragment extends BaseFragment implements View.OnClickListener {

    final private static String TAG = "HomeFragment";

    private ProgressBar mLoadingBar;

    private iHandleFragment mHandleFragment;
    private UserMainActivity activity;

    private Post mPost;

    DatabaseReference mRootRef;



    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Post> postList = new ArrayList<>();
    private ArrayList<User> userList = new ArrayList<>();


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandleFragment.setToolbarTitle(getTag());
        activity = (UserMainActivity) getActivity();

        mRootRef = DatabaseManager.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //  Button
        view.findViewById(R.id.home_fragment_add_message).setOnClickListener(this);

        //  View
        mRecyclerView = view.findViewById(R.id.home_fragment_recycler_view);;


        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new HomeAdapter(getContext(), postList, activity.getUserList());
        mRecyclerView.setAdapter(mAdapter);

        //initUI();

        //updateUI();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
        Log.d(TAG, "onStart");
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
            case R.id.home_fragment_add_message:
                startActivity(new Intent(getContext(), PostActivity.class));
                break;
            default:
                break;
        }
    }

    private void updateUI() {
        Log.d(TAG, "updateUI");

        mRootRef.child("posts")
                .orderByChild("inverseDate")
                .addValueEventListener(loadUserPostData());
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
                            mAdapter = new HomeAdapter(getContext(), postList, activity.getUserList());
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
