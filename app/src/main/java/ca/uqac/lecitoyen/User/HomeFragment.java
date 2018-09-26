package ca.uqac.lecitoyen.User;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapter.HomeRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    final private static String TAG = "HomeFragment";

    private iHandleFragment mHandleFragment;

    private FloatingActionButton mAddMessageButton;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ArrayList<String> mRealNameList = new ArrayList<>();
    private ArrayList<String> mUserNameList = new ArrayList<>();
    private ArrayList<String> mMessageList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandleFragment.setToolbarTitle(getTag());

        for(int i = 1; i <= 30; i++) {
            mRealNameList.add("Real name " + i);
            mUserNameList.add("Username " + i);
            mMessageList.add("Message " + i);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAddMessageButton = view.findViewById(R.id.home_fragment_add_message);

        mRecyclerView = view.findViewById(R.id.home_fragment_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.d(TAG, "view created");

        mAdapter = new HomeRecyclerViewAdapter(mRealNameList, mUserNameList, mMessageList);
        mRecyclerView.setAdapter(mAdapter);

        //initRecyclerView(view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (UserActivity) getActivity();
    }
}
