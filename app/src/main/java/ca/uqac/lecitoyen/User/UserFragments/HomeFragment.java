package ca.uqac.lecitoyen.User.UserFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.User.PostActivity;
import ca.uqac.lecitoyen.User.UserMainActivity;
import ca.uqac.lecitoyen.adapter.HomeAdapter;
import ca.uqac.lecitoyen.adapter.HomeRecyclerViewAdapter;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostTest;
import ca.uqac.lecitoyen.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    final private static String TAG = "HomeFragment";

    private iHandleFragment mHandleFragment;
    private UserMainActivity mParentActivity;

    private Post mPost;

    DatabaseReference mReferenceFrag;



    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<Post> postList;
    private ArrayList<PostTest> postTestList;
    private ArrayList<User> userList;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandleFragment.setToolbarTitle(getTag());
        mParentActivity = (UserMainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.home_fragment_add_message).setOnClickListener(this);

        mRecyclerView = view.findViewById(R.id.home_fragment_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        initUI();

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

    private void initUI() {
        Log.d(TAG, "initUI");
        postTestList = DatabaseManager.getInstance().getPostListOrderByDate();
        userList = DatabaseManager.getInstance().getUserList();

        mAdapter = new HomeAdapter(postTestList, userList);
        mRecyclerView.setAdapter(mAdapter);
    }


    private void updateUI() {
        Log.d(TAG, "updateUI");
        postTestList = DatabaseManager.getInstance().getPostListOrderByDate();
        userList = DatabaseManager.getInstance().getUserList();

        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }
}
