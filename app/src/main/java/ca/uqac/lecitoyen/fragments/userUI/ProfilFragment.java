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
    private boolean appBarExpanded;

    private AppBarLayout appBarLayout;
    //  Toolbar expanded false
    private CircleImageView mToolbarProfilPictureView;
    private TextView mToolbarName;
    private TextView mToolbarPostCount;
    //  Toolbar expanded true
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


    private Menu collapsedMenu;


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
        toolbar = view.findViewById(R.id.toolbar_profil);
        ((AppCompatActivity) mainUserActivity).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        //  Views
        AppBarLayout toolbarLayout = view.findViewById(R.id.toolbar_profil_layout);
        mToolbarProfilPictureView = view.findViewById(R.id.toolbar_profil_picture);
        mToolbarName = view.findViewById(R.id.toolbar_profil_name);
        mToolbarPostCount= view.findViewById(R.id.toolbar_profil_post_count);
        mProfilPictureView = view.findViewById(R.id.toolbar_profil_collapsing_picture);
        mNameView = view.findViewById(R.id.toolbar_profil_collapsing_name);
        mUsernameView = view.findViewById(R.id.toolbar_profil_collapsing_username);
        mBiographyView = view.findViewById(R.id.toolbar_profil_collapsing_biography);
        mFollowerCount = view.findViewById(R.id.profil_followers_count);
        mFollowingCount = view.findViewById(R.id.profil_following_count);

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
        inflater.inflate(R.menu.user_menu, menu);
        //collapsedMenu = menu;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(collapsedMenu);
        /*if (collapsedMenu != null && (!appBarExpanded || collapsedMenu.size() != 1)) {
            //collapsed
            collapsedMenu.add("Add")
                    .setIcon(R.drawable.ic_edit_black_24dp)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            //expanded

        }*/
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
            case R.id.menu_setting:
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
                float percentage = ((float)Math.abs(verticalOffset)/appBarLayout.getTotalScrollRange());
                //float horizontalRa = ((float)Math.abs(verticalOffset)/(appBarLayout.getWidth()/2));
                //float horizontal = ((float)Math.abs(verticalOffset)/(toolbarCollapsingPicture.getX()));
                //too.setAlpha(percentage);

                if(Math.abs(verticalOffset) >  250) {
                    appBarExpanded = false;
                    mToolbarName.setVisibility(View.VISIBLE);
                    mToolbarPostCount.setVisibility(View.VISIBLE);
                    mToolbarProfilPictureView.setVisibility(View.VISIBLE);
                    mToolbarName.setAlpha((2 * percentage));
                    mToolbarProfilPictureView.setAlpha(2 * percentage);
                    mToolbarPostCount.setAlpha(2 * percentage);

                    mProfilPictureView.setScaleX(1 - ( percentage));
                    mProfilPictureView.setScaleY(1 - (percentage));

                } else {
                    appBarExpanded = true;
                    mToolbarName.setVisibility(View.INVISIBLE);
                    mToolbarPostCount.setVisibility(View.INVISIBLE);
                    mToolbarProfilPictureView.setVisibility(View.INVISIBLE);
                }

                //float middle = toolbarCollapsingPicture.getX();
                //toolbarCollapsingPicture.setTranslationX(0 - (horizontal * middle));

/*
                if(Math.abs(verticalOffset) > 550) {
                    appBarExpanded = true;
                    toolbarPicture.setVisibility(View.GONE);
                    toolbarName.setVisibility(View.GONE);
                    toolbarPostCount.setVisibility(View.GONE);
                    //toolbarCollapsingPicture.startAnimation(scaleDownLeft);
                    //invalidateOptionsMenu();
                } else {
                    toolbarPicture.setVisibility(View.VISIBLE);
                    toolbarName.setVisibility(View.VISIBLE);
                    toolbarPostCount.setVisibility(View.VISIBLE);
                }*/

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
                User userData = dataSnapshot.getValue(User.class);

                if(userData != null)
                {
                    if(userData.getPid() != null && !userData.getPid().isEmpty()) {
                        Glide.with(mainUserActivity).load(stUserProfilPicture.child(userData.getPid())).into(mToolbarProfilPictureView);
                        Glide.with(mainUserActivity).load(stUserProfilPicture.child(userData.getPid())).into(mProfilPictureView);
                    }
                    if (userData.getName() != null && !userData.getName().isEmpty()) {
                        mToolbarName.setText(userData.getName());
                        mNameView.setText(userData.getName());
                    }
                    if (userData.getUsername() != null && !userData.getUsername().isEmpty())
                        mUsernameView.setText(userData.getUsername());
                    if (userData.getBiography() != null && !userData.getBiography().isEmpty())
                        mBiographyView.setText(userData.getBiography());
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
                    mToolbarPostCount.setText((Integer.toString(listUserPost.size())));
                    mAdapter = new SwipePostAdapter(mainUserActivity, fbUser, listUserPost);
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
