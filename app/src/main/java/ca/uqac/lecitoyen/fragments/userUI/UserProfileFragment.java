package ca.uqac.lecitoyen.fragments.userUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.EditProfilActivity;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.activities.SettingsActivity;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = UserProfileFragment.class.getSimpleName();
    private static final String ARG_USER_AUTH = "user_auth";
    private static final String ARG_USER_SELECT = "user_select";

    private OnFragmentInteractionListener mListener;

    private MainUserActivity mUserActivity;
    private iHandleFragment mHandleFragment;

    private String mUserAuthId;
    private User mUserSelect;

    private boolean isAppBarExpanded = true;
    private Menu mCollapsedMenu;
    private MenuItem mSettingsMenuItem;
    private MenuItem mEditMenuItem;

    //  Toolbar expanded false
    private CircleImageView mToolbarProfilPictureView;
    private TextView mToolbarName;
    private TextView mToolbarPostCount;
    //  Toolbar expanded true
    private ImageView mBackgroundView;
    private CircleImageView mProfilPictureView;
    private TextView mNameView;
    private TextView mUsernameView;
    private TextView mBiographyView;
    private TextView mFollowerCount;
    private TextView mFollowingCount;
    private TextView mEditAccountButton;
    private FloatingActionButton mAddPostButton;
    private RecyclerView mProfileRecyclerView;
    private RecyclerSwipeAdapter mProfileAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(User userSelect) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER_SELECT, userSelect);
        fragment.setArguments(args);
        return fragment;
    }

    public static UserProfileFragment newInstance(String userAuthId, User userSelect) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_USER_AUTH, userAuthId);
        args.putSerializable(ARG_USER_SELECT, userSelect);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mUserActivity = (MainUserActivity) getActivity();
            //mUserAuthId = getArguments().getString(ARG_USER_AUTH);
            mUserSelect = (User) getArguments().getSerializable(ARG_USER_SELECT);
        } else {
            Log.e(TAG, "User select is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_profile, container, false);


        //  Views
        AppBarLayout toolbarLayout = view.findViewById(R.id.toolbar_profil_layout);
        mToolbarProfilPictureView = view.findViewById(R.id.toolbar_profil_picture);
        mToolbarName = view.findViewById(R.id.toolbar_profil_name);
        mToolbarPostCount= view.findViewById(R.id.toolbar_profil_post_count);
        mBackgroundView = view.findViewById(R.id.toolbar_profil_collapsing_background);
        mProfilPictureView = view.findViewById(R.id.toolbar_profil_collapsing_picture);
        mNameView = view.findViewById(R.id.toolbar_profil_collapsing_name);
        mUsernameView = view.findViewById(R.id.toolbar_profil_collapsing_username);
        mBiographyView = view.findViewById(R.id.toolbar_profil_collapsing_biography);
        mFollowerCount = view.findViewById(R.id.toolbar_profil_collapsing_follower_count);
        mFollowingCount = view.findViewById(R.id.toolbar_profil_collapsing_following_count);
        mAddPostButton = view.findViewById(R.id.user_profile_add_message);
        mSwipeRefreshLayout = view.findViewById(R.id.profile_refresh_layout);

        //  Toolbar Listner
        toolbarLayout.addOnOffsetChangedListener(onOffsetChangedListener(view));

        //  Recycler View
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mUserActivity);
        mProfileRecyclerView = view.findViewById(R.id.user_profile_recycler_view);
        mProfileRecyclerView.setNestedScrollingEnabled(false);
        mProfileRecyclerView.setLayoutManager(layoutManager);


        /**
         *
         *      Handle toolbar if user own profile or someone else profile
         *
         */
        Toolbar toolbar = view.findViewById(R.id.toolbar_user_profil);
        mUserActivity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        if(mUserActivity.getSupportActionBar() != null) {

            //if (mUserAuthId.equals(mUserSelect.getUid())) {
                //mAddPostButton.setVisibility(View.VISIBLE);
                //mUserActivity.setBottomNavigationItem(4);
                //mUserActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            //} else {
                mAddPostButton.setVisibility(View.GONE);
                mUserActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //}

        }


        return view;
    }

    public void sendBack(String something) {

        if(mListener != null) {
            mListener.onFragmentInteraction(something);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainUserActivity) getActivity();
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandleFragment = null;
        mUserSelect = null;
        mCollapsedMenu = null;
        mSettingsMenuItem = null;
        mEditMenuItem = null;
    }

    @Override
    public void onClick(View view) {

    }

    private void updateUI() {

        DatabaseManager dbManager = DatabaseManager.getInstance();
        DatabaseReference dbUserPost = dbManager.getDatabaseUserPosts(mUserSelect.getUid());
        StorageReference stUserProfilePicture =
                dbManager.getStorageUserProfilPicture(mUserSelect.getUid(), mUserSelect.getPid());

        ArrayList<Post> postsList = new ArrayList<>();

        initUserdata(stUserProfilePicture);

        dbUserPost.orderByChild("dateInverse").addListenerForSingleValueEvent(initPostsList(postsList));

        mSwipeRefreshLayout.setOnRefreshListener(refreshListener(dbManager, postsList));

    }

    private void initUserdata(StorageReference stUserProfilPicture) {

        if(mUserSelect.getPid() != null && !mUserSelect.getPid().isEmpty()) {
            Glide.with(mUserActivity).load(stUserProfilPicture).into(mToolbarProfilPictureView);
            Glide.with(mUserActivity).load(stUserProfilPicture).into(mProfilPictureView);
        }

        if (mUserSelect.getName() != null && !mUserSelect.getName().isEmpty()) {
            mToolbarName.setText(mUserSelect.getName());
            mNameView.setText(mUserSelect.getName());
        }

        if (mUserSelect.getUsername() != null && !mUserSelect.getUsername().isEmpty())
            mUsernameView.setText(mUserSelect.getUsername());

        if (mUserSelect.getBiography() != null && !mUserSelect.getBiography().isEmpty())
            mBiographyView.setText(mUserSelect.getBiography());

        mFollowerCount.setText(Util.setStringPlurial(
                0,
                getString(R.string.textview_follower)));
        mFollowingCount.setText(Util.setStringPlurial(
                0,
                getString(R.string.textview_following)));
        //  Following & Follower
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
        //if (mUserAuthId.equals(mUserSelect.getUid())) {
        inflater.inflate(R.menu.user_menu, menu);
        if(mCollapsedMenu == null)
            mCollapsedMenu = menu;
        if(mSettingsMenuItem == null)
            mSettingsMenuItem = mCollapsedMenu.findItem(R.id.menu_settings);
        if(mEditMenuItem == null)
            mEditMenuItem = mCollapsedMenu.findItem(R.id.menu_edit);
        // }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(mCollapsedMenu);

        if(mCollapsedMenu != null) {

            if (!isAppBarExpanded) {

                mSettingsMenuItem.setIcon(R.drawable.ic_more_vert_primary_24dp);
                mSettingsMenuItem.setVisible(true);
                mEditMenuItem.setVisible(true);

            } else {

                mSettingsMenuItem.setVisible(true);
                mSettingsMenuItem.setIcon(R.drawable.ic_more_vert_white_24dp);
                mEditMenuItem.setVisible(false);

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG, "item selected");
        switch (item.getItemId())
        {
            case android.R.id.home:

                //mHandleFragment.inflateFragment(R.string.fragment_forum, "");
                sendBack("allo");
                return true;
            case R.id.menu_edit:
                Log.w(TAG, "menu_edit clicked");
                startActivity(new Intent(mUserActivity, EditProfilActivity.class ));
                return true;
            case R.id.menu_settings:
                Log.w(TAG, "menu_setting clicked");
                startActivity(new Intent(mUserActivity, SettingsActivity.class ));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener(final View view) {
        return new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                int imageRadius = (mProfilPictureView.getWidth()/2);
                float percentage = ((float)Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange());
                float imageScaleRatio = ((float)Math.abs(verticalOffset)/mBackgroundView.getHeight());

                mProfilPictureView.setScaleX(1.06f - imageScaleRatio);
                mProfilPictureView.setScaleY(1.06f - imageScaleRatio);
                mProfilPictureView.setTranslationX(0 - imageScaleRatio * imageRadius);

                if(isAppBarExpanded)
                    mUserActivity.invalidateOptionsMenu();
                if(Math.abs(verticalOffset) >  415) {
                    isAppBarExpanded = false;
                    mProfilPictureView.setVisibility(View.GONE);
                    mToolbarProfilPictureView.setVisibility(View.VISIBLE);
                    mToolbarPostCount.setVisibility(View.VISIBLE);
                    mToolbarPostCount.setAlpha(percentage);
                    mToolbarName.setVisibility(View.VISIBLE);
                    mToolbarName.setAlpha((percentage));

                    if(mUserActivity.getSupportActionBar() != null)
                        mUserActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_primary_24dp);
                } else {
                    isAppBarExpanded = true;
                    mProfilPictureView.setVisibility(View.VISIBLE);
                    mToolbarProfilPictureView.setVisibility(View.GONE);

                    mToolbarName.setVisibility(View.GONE);
                    mToolbarPostCount.setVisibility(View.GONE);
                    mToolbarProfilPictureView.setVisibility(View.GONE);

                    if(mUserActivity.getSupportActionBar() != null)
                    mUserActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
                }
            }
        };
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener(final DatabaseManager dbManager, final ArrayList<Post> postsList) {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(postsList.size() != 0) {

                    int postLastPostLoaded = postsList.size() - 1;
                    Post lastPostLoaded = postsList.get(postLastPostLoaded);

                    long currentTime = System.currentTimeMillis();

                    dbManager.getDatabasePosts()
                            .startAt(lastPostLoaded.getDate())
                            .addChildEventListener(childEventListener(postsList));

                    mSwipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
                        @Override
                        public boolean canChildScrollUp(@NonNull SwipeRefreshLayout parent, @Nullable View child) {
                            parent.setRefreshing(false);
                            return false;
                        }
                    });
                }
            }
        };
    }

    private ValueEventListener initPostsList(final ArrayList<Post> userPostsList) {
        Log.d(TAG, "readPublicationListOnce");
        //showProgressDialog();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if(post != null) {
                        userPostsList.add(post);
                    }
                }
                if(userPostsList != null) {
                    if(userPostsList.size() != 0) {
                        mToolbarPostCount.setText(Util.setStringPlurial(
                                userPostsList.size(),
                                getString(R.string.textview_post))
                        );
                        mProfileAdapter = new SwipePostAdapter(mUserActivity, mUserSelect, userPostsList);
                        mProfileRecyclerView.setAdapter(mProfileAdapter);
                    }
                }
                //hideProgressDialog();
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
                mProfileAdapter.notifyDataSetChanged();
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


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(String something);

    }

}
