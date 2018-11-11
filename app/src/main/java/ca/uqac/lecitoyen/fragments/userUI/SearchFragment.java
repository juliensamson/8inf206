package ca.uqac.lecitoyen.fragments.userUI;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SearchUserAdapter;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.views.ToolbarView;

//  TODO: Handle research of (user, post, etc.)


public class SearchFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = SearchFragment.class.getSimpleName();

    private final static String ARG_USERAUTH = "user";
    private final static String ARG_USERS = "users";

    private MainUserActivity activity;
    private iHandleFragment mHandleFragment;
    private DatabaseManager dbManager;

    //  Views
    private ToolbarView mSearchToolbar;
    private RecyclerView mSearchRecyclerView;
    //private RecyclerView.Adapter mNewsfeedAdapter;
    private RecyclerView.Adapter mSearchAdapter;

    //  Data Structure
    private User mUserAuth;
    private ArrayList<User> mUsersList = new ArrayList<>();


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(User userAuth, ArrayList<User> users) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USERAUTH, userAuth);
        args.putParcelableArrayList(ARG_USERS, users);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (MainUserActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();

        if (getArguments() != null) {
            mUserAuth = getArguments().getParcelable(ARG_USERAUTH);
            mUsersList = getArguments().getParcelableArrayList(ARG_USERS);
            mSearchAdapter = new SearchUserAdapter(activity, mUsersList);
        } else {
            Log.e(TAG, "Arguments are null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        mSearchToolbar = view.findViewById(R.id.search_toolbar);
        mSearchToolbar.searchToolbar(
                activity,
                getResources().getString(R.string.fragment_search),
                dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid())
        );

        mSearchToolbar.onImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mUserAuth != null) {
                    UserProfileFragment fragment = UserProfileFragment.newInstance(mUserAuth);
                    activity.doUserProfileTransaction(fragment, MainUserActivity.AUTH_USER);
                }
            }
        });


        mSearchRecyclerView = view.findViewById(R.id.search_recycler_view);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        mSearchRecyclerView.setLayoutManager(llm);
        mSearchRecyclerView.setAdapter(mSearchAdapter);

        //  Search bar
        /*mSearchBar = view.findViewById(R.id.search_bar);
        mSearchBar.enableSearch();
        mSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });*/


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainUserActivity) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                return true;
            case R.id.action_search:
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }

    public interface OnSearchFragmentInteractionListener {

        void onFragmentInteraction(String something);

    }
}
