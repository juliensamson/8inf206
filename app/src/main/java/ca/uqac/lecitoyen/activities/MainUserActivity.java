package ca.uqac.lecitoyen.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.fragments.userUI.CityfeedFragment;
import ca.uqac.lecitoyen.fragments.userUI.MessageFragment;
import ca.uqac.lecitoyen.fragments.userUI.ForumFragment;
import ca.uqac.lecitoyen.fragments.userUI.UserProfileFragment;
import ca.uqac.lecitoyen.models.UserStorage;
import ca.uqac.lecitoyen.fragments.userUI.ProfilFragment;
import ca.uqac.lecitoyen.fragments.userUI.SearchFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.BottomNavigationFixedItemView;

//TODO: Make the RecyclerView load automatically after making a post

public class MainUserActivity extends BaseActivity implements iHandleFragment {

    final private static String TAG = "MainUserActivity";

    private iHandleFragment mHandleFragment;
    private ForumFragment forumFragment;
    private SearchFragment searchFragment;
    private CityfeedFragment cityfeedFragment;
    private MessageFragment messageFragment;
    private ProfilFragment profilFragment;
    private UserProfileFragment userProfileFragment;

    private DatabaseManager dbManager;
    private DatabaseReference dbUsersData;
    private DatabaseReference dbUserProfilPicture;
    private DatabaseReference dbPosts;

    private User mUserdata;

    public UserStorage mUserStorage;
    private ArrayList<Post> mPosts = new ArrayList<>();
    public ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserStorage> listUserProfilPicture = new ArrayList<>();

    private Toolbar mUserToolbar;
    private TextView mUserToolbarTitle;

    private String currentFragmentTag;
    private boolean isFragmentInitialize = false;

    //Firebase
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;

    private BottomNavigationFixedItemView bottomNavigationFixedItemView;
    private BottomNavigation mBottomNavigation;
    private BottomNavigation.OnMenuItemSelectionListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Log.d(TAG, "Activity created");

        //  Database
        dbManager = DatabaseManager.getInstance();

        //Toolbar userToolbar = findViewById(R.id.toolbar_user);
        //setSupportActionBar(userToolbar);

        //  Initialize auth
        fbAuth = FirebaseAuth.getInstance();

        //  Initialize fragment
        forumFragment = new ForumFragment();
        searchFragment = new SearchFragment();
        cityfeedFragment = new CityfeedFragment();
        messageFragment = new MessageFragment();
        profilFragment   = new ProfilFragment();

        doFragmentTransaction(forumFragment, getString(R.string.fragment_forum), false, "");

        //  Bottom navigation
        mBottomNavigation = findViewById(R.id.navigation);
        mBottomNavigation.setDefaultSelectedIndex(0);
        mBottomNavigation.setDefaultTypeface(Typeface.DEFAULT_BOLD);
        mBottomNavigation.setOnMenuItemClickListener(loadBottonNavigation());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(fbAuth != null)
        {
            fbUser = fbAuth.getCurrentUser();

            if(fbUser != null)
            {
                String uid = fbUser.getUid();
                //  Reference to the user profil picture
                dbUserProfilPicture = dbManager.getDatabaseUserProfilPicture(uid);
                //  Reference to the list of users
                dbUsersData = dbManager.getDatabaseUsers();
                //  Reference to the list of publications
                dbPosts = dbManager.getDatabasePosts();

                updateUI();
            }
        } else {
            Log.e(TAG, "auth is null");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        /*if(currentFragmentTag.equals(userProfileFragment.getTag())) {

            if(userProfileFragment.getArguments() != null) {
                String uid = userProfileFragment.getArguments().getString("user_auth");
                User userSelect = (User) userProfileFragment.getArguments().getSerializable("user_select");

                if(!uid.equals(userSelect.getUid())) {
                    inflateFragment(R.string.fragment_forum, "");
                } else {
                    moveTaskToBack(true);
                }
            }

        } else {
        }*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //FragmentManager manager = getSupportFragmentManager();
        //manager.putFragment(outState, );
    }

    private void updateUI() {

        DatabaseManager dbManager = DatabaseManager.getInstance();
        DatabaseReference dbUser = dbManager.getDatabaseUser(fbUser.getUid());

        dbUser.addListenerForSingleValueEvent(getUserdata());

        dbPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Post post = postSnapshot.getValue(Post.class);

                    if(post != null) {
                        mPosts.add(post);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initFragment(FragmentTransaction transaction) {
        forumFragment = new ForumFragment();
        searchFragment = new SearchFragment();
        cityfeedFragment = new CityfeedFragment();
        messageFragment = new MessageFragment();
        profilFragment   = new ProfilFragment();

        transaction
                .add(R.id.user_container, forumFragment, forumFragment.getTag())
                .add(R.id.user_container, searchFragment, searchFragment.getTag())
                .add(R.id.user_container, cityfeedFragment, cityfeedFragment.getTag())
                .add(R.id.user_container, messageFragment, messageFragment.getTag())
                .add(R.id.user_container, profilFragment, profilFragment.getTag());

        currentFragmentTag = forumFragment.getTag();

        isFragmentInitialize = true;
    }

    public void doUserProfilFragmentTransaction(Fragment fragment, boolean addToBackStack) {

        String tag = fragment.getTag();
        currentFragmentTag = tag;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        transaction.replace(R.id.user_container, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message) {
        Log.d(TAG, "doFragmentTransaction");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);

        transaction.replace(R.id.user_container, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();

        currentFragmentTag = tag;
    }

    private void doFragmentTransaction(Fragment nextFragment, String nextFragmentTag) {
        Log.d(TAG, "doFragmentTransaction");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(!isFragmentInitialize) {
            initFragment(transaction);
        }

        Fragment currentFragment = manager.getPrimaryNavigationFragment();

        if(!transaction.isEmpty()) {
            transaction.replace(R.id.user_container, nextFragment, nextFragmentTag);
            /*transaction
                    .hide(currentFragment)
                    .show(nextFragment)
                    .commit();
                    */

            currentFragmentTag = nextFragmentTag;
        }

    }


    @Override
    public void setToolbarTitle(String fragmentTag) {
        mUserToolbarTitle.setText(fragmentTag);
    }

    @Override
    public void inflateFragment(int fragmentTagId, String message) {

        switch (fragmentTagId)
        {
            case R.string.fragment_forum:
                doFragmentTransaction(forumFragment, getString(R.string.fragment_forum), true, "");
                //doFragmentTransaction(forumFragment, getString(R.string.fragment_forum));
                break;
            case R.string.fragment_search:
                doFragmentTransaction(searchFragment, getString(R.string.fragment_search), true, "");
                //doFragmentTransaction(searchFragment, getString(R.string.fragment_search));
                break;
            case R.string.fragment_cityfeed:
                doFragmentTransaction(cityfeedFragment, getString(R.string.fragment_cityfeed), true, "");
                //doFragmentTransaction(cityfeedFragment, getString(R.string.fragment_cityfeed));
                break;
            case R.string.fragment_messages:
                doFragmentTransaction(messageFragment, getString(R.string.fragment_messages), true, "");
                //doFragmentTransaction(messageFragment, getString(R.string.fragment_messages));
                break;
            case R.string.fragment_profil:
                if(mUserdata != null) {
                    userProfileFragment = UserProfileFragment.newInstance(mUserdata.getUid(), mUserdata);
                    doUserProfilFragmentTransaction(userProfileFragment, true);
                } else {
                    doFragmentTransaction(profilFragment, getString(R.string.fragment_profil), true, "");
                }
                break;
            default:
                break;
        }
    }

    private BottomNavigation.OnMenuItemSelectionListener loadBottonNavigation() {
        return new BottomNavigation.OnMenuItemSelectionListener() {

            @Override
            public void onMenuItemSelect(int navId, int listId, boolean b) {
                Log.d(TAG, "OnNavigationItem");
                switch (navId) {
                    case R.id.navigation_newsfeed:
                        inflateFragment(R.string.fragment_forum, "");
                        break;
                    case R.id.navigation_search:
                        inflateFragment(R.string.fragment_search, "");
                        break;
                    case R.id.navigation_cityfeed:
                        inflateFragment(R.string.fragment_cityfeed, "");
                        break;
                    case R.id.navigation_messages:
                        inflateFragment(R.string.fragment_messages, "");
                        break;
                    case R.id.navigation_profil:
                        inflateFragment(R.string.fragment_profil, "");
                        break;
                }
            }

            @Override
            public void onMenuItemReselect(int navId, int listId, boolean b) {
                //  Update feed
                //  Or goe to the top
            }

        };
    }

    public void setBottomNavigationItem(int pos) {
        mBottomNavigation.setSelectedIndex(pos, true);
    }

    //  TODO: - Make Key,Value a list, map, etc. in order to add more "extras" to the Bundle
    //        - Allow to sent the class User to get the info directly. and not call mAuth on setting. (make it faster)
    public void startActivityWithBundle(Class activity, String key, String value) {

        //  Create Bundle sent to the next activity
        Bundle extras = new Bundle();
        extras.putString(key, value);

        //  Add extras to the intent and start
        Intent intent = new Intent(this, activity);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private ValueEventListener getUserdata() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserdata = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public ArrayList<User> getUserList() {
        return this.mUserList;
    }

    public ArrayList<Post> getPosts() { return this.mPosts;}

    public ArrayList<UserStorage> getListUserProfilPicture() {
        return this.listUserProfilPicture;
    }


    public FirebaseAuth getUserAuth() {
        return this.fbAuth;
    }

    public UserStorage getUserStorage() {
        return this.mUserStorage;
    }
}
