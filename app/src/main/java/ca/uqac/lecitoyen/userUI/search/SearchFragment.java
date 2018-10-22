package ca.uqac.lecitoyen.userUI.search;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mancj.materialsearchbar.MaterialSearchBar;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.userUI.UserMainActivity;

//  TODO: Handle research of (user, post, etc.)


public class SearchFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SearchFragment";

    private UserMainActivity activity;
    private iHandleFragment mHandleFragment;

    //  Views
    private MaterialSearchBar mSearchBar;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (UserMainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //mHandleFragment.setToolbarTitle(getTag());

        //  Search bar
        mSearchBar = view.findViewById(R.id.search_bar);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (iHandleFragment) getActivity();
    }

    @Override
    public void onClick(View view) {

    }
}
