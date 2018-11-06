package ca.uqac.lecitoyen.fragments.userUI;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ca.uqac.lecitoyen.activities.EditProfilActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.SettingsActivity;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ProfilFragment";
    private MainUserActivity mainUserActivity;
    private iHandleFragment mHandleFragment;

    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseManager dbManager;
    private DatabaseReference dbUserData;
    private DatabaseReference dbUserProfilPicture;
    private DatabaseReference dbUserPost;
    private StorageReference stUserProfilPicture;

    private ArrayList<Post> listUserPost = new ArrayList<>();

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private boolean appBarExpanded = true;

    private AppBarLayout appBarLayout;

    private User mUserdata;

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
    private RecyclerView mProfilRecyclerView;
    private RecyclerSwipeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int previousOffset;
    private Menu collapsedMenu;

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainUserActivity = (MainUserActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.fbAuth = mainUserActivity.getUserAuth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profil, container, false);

        Log.d(TAG, "onCreateView");
        //  Handle toolbar
        toolbar = view.findViewById(R.id.toolbar_user_profil);
        ((AppCompatActivity) mainUserActivity).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

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

        //  Button
        view.findViewById(R.id.toolbar_profil_collapsing_button).setOnClickListener(this);

        //  Toolbar Listner
        toolbarLayout.addOnOffsetChangedListener(onOffsetChangedListener(view));

        //  Recycler View
        mProfilRecyclerView = view.findViewById(R.id.profil_publication_recycler_view);
        mProfilRecyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(mainUserActivity);
        mProfilRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(fbAuth != null) {
            fbUser = fbAuth.getCurrentUser();
            if(fbUser != null) {
                String uid = fbUser.getUid();
                dbUserData = dbManager.getDatabaseUser(uid);
                dbUserProfilPicture = dbManager.getDatabaseUserProfilPicture(uid);
                dbUserPost = dbManager.getDatabaseUserPosts(uid);
                stUserProfilPicture = dbManager.getStorageUserProfilPicture(uid);
                updateUI();
            }
        } else {
            Log.e(TAG, "auth is null");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (iHandleFragment) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
        inflater.inflate(R.menu.user_menu, menu);
        collapsedMenu = menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(collapsedMenu);

        if(collapsedMenu != null) {
            MenuItem settingsItem = collapsedMenu.findItem(R.id.menu_settings);
            MenuItem editItem = collapsedMenu.findItem(R.id.menu_edit);

            Log.d(TAG, "onPrepareOptionsMenu");
            Log.d(TAG, editItem.toString());

            if (!appBarExpanded) {
                settingsItem.setVisible(true);
                settingsItem.setIcon(R.drawable.ic_more_vert_primary_24dp);
                editItem.setVisible(true);
            } else {
                settingsItem.setVisible(true);
                settingsItem.setIcon(R.drawable.ic_more_vert_white_24dp);
                editItem.setVisible(false);
            }
        }
        //return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG, "item selected");
        switch (item.getItemId())
        {
            case R.id.menu_edit:
                Log.w(TAG, "menu_edit clicked");
                startActivity(new Intent(mainUserActivity.getApplicationContext(), EditProfilActivity.class ));
                return true;
            case R.id.menu_settings:
                Log.w(TAG, "menu_setting clicked");
                startActivity(new Intent(mainUserActivity.getApplicationContext(), SettingsActivity.class ));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.toolbar_profil_collapsing_button:
                startActivity(new Intent(mainUserActivity.getApplicationContext(), EditProfilActivity.class ));
                mainUserActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                break;
        }
    }

    private AppBarLayout.OnOffsetChangedListener onOffsetChangedListener(final View view) {
        return new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {


                int width = appBarLayout.getWidth();
                int imageRadius = (mProfilPictureView.getWidth()/2);
                //Log.d(TAG, String.valueOf(imageWidth));
                float percentage = ((float)Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange());
                float imageScaleRatio = ((float)Math.abs(verticalOffset)/mBackgroundView.getHeight());
                float imageTranslationRatio =((float)Math.abs(verticalOffset)/imageRadius);

                //float horizontalRa = ((float)Math.abs(verticalOffset)/(appBarLayout.getWidth()/2));
                float horizontal = ((float)Math.abs(verticalOffset)/(mProfilPictureView.getX()));
                //too.setAlpha(percentastyle="@style/LightToolbarTheme.SubTitle"m);



                mProfilPictureView.setScaleX(1.06f - imageScaleRatio);
                mProfilPictureView.setScaleY(1.06f - imageScaleRatio);
                mProfilPictureView.setTranslationX(0 - imageScaleRatio * imageRadius);

                Log.e(TAG, "Offset: " + verticalOffset + " Percentage: " + percentage);
                //if(mProfilPictureView.getVisibility() == View.GON )

                if(!appBarExpanded)
                    mainUserActivity.invalidateOptionsMenu();
                if(Math.abs(verticalOffset) >  415) {
                    appBarExpanded = false;
                    mProfilPictureView.setVisibility(View.GONE);
                    mToolbarProfilPictureView.setVisibility(View.VISIBLE);
                    mToolbarPostCount.setVisibility(View.VISIBLE);
                    mToolbarPostCount.setAlpha(percentage);
                    mToolbarName.setVisibility(View.VISIBLE);
                    mToolbarName.setAlpha((percentage));
                } else {
                    appBarExpanded = true;
                    mProfilPictureView.setVisibility(View.VISIBLE);
                    mToolbarProfilPictureView.setVisibility(View.GONE);

                    mToolbarName.setVisibility(View.GONE);
                    mToolbarPostCount.setVisibility(View.GONE);
                    mToolbarProfilPictureView.setVisibility(View.GONE);
                }


                if(Math.abs(verticalOffset) == 0)
                    Log.d(TAG, "Offset = "+ verticalOffset);
                if(Math.abs(verticalOffset) == 100)
                    Log.d(TAG, "Offset = "+ verticalOffset);
                if(Math.abs(verticalOffset) == 200)
                    Log.d(TAG, "Offset = "+ verticalOffset);
                if(Math.abs(verticalOffset) == 300)
                    Log.d(TAG, "Offset = "+ verticalOffset);
                if(Math.abs(verticalOffset) == 400)
                    Log.d(TAG, "Offset = "+ verticalOffset);
                if(Math.abs(verticalOffset) == 500)
                    Log.d(TAG, "Offset = "+ verticalOffset);
                if(Math.abs(verticalOffset) == 600)
                    Log.d(TAG, "Offset = "+ verticalOffset);
                if(Math.abs(verticalOffset) == 700)
                    Log.d(TAG, "Offset = "+ verticalOffset);
            }
        };
    }


    private void updateUI() {
        //  Load database user data & update UI
        dbUserData.addListenerForSingleValueEvent(loadUserData());
        //  Load database user post & update UI
        dbUserPost.orderByChild("dateInverse").addListenerForSingleValueEvent(loadUserPost());
    }

    private ValueEventListener loadUserData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "loadUserData");
                mUserdata = dataSnapshot.getValue(User.class);

                if(mUserdata != null)
                {
                    if(mUserdata.getPid() != null && !mUserdata.getPid().isEmpty()) {
                        Glide.with(mainUserActivity).load(stUserProfilPicture.child(mUserdata.getPid())).into(mToolbarProfilPictureView);
                        Glide.with(mainUserActivity).load(stUserProfilPicture.child(mUserdata.getPid())).into(mProfilPictureView);
                    }
                    if (mUserdata.getName() != null && !mUserdata.getName().isEmpty()) {
                        mToolbarName.setText(mUserdata.getName());
                        mNameView.setText(mUserdata.getName());
                    }
                    if (mUserdata.getUsername() != null && !mUserdata.getUsername().isEmpty())
                        mUsernameView.setText(mUserdata.getUsername());
                    if (mUserdata.getBiography() != null && !mUserdata.getBiography().isEmpty())
                        mBiographyView.setText(mUserdata.getBiography());

                    mFollowerCount.setText(Util.setStringPlurial(
                            0,
                            getString(R.string.textview_follower)));
                    mFollowingCount.setText(Util.setStringPlurial(
                            0,
                            getString(R.string.textview_following)));
                    //  Following & Follower
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    private ValueEventListener loadUserPost() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "loadUserPost");
                listUserPost.clear();

                for(DataSnapshot userPost: dataSnapshot.getChildren()) {
                    listUserPost.add(userPost.getValue(Post.class));
                }

                if(listUserPost != null || listUserPost.size() != 0) {
                    mToolbarPostCount.setText(Util.setStringPlurial(
                            listUserPost.size(),
                            getString(R.string.textview_post))
                    );
                    mAdapter = new SwipePostAdapter(mainUserActivity, mUserdata, listUserPost);
                    mProfilRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }
}
