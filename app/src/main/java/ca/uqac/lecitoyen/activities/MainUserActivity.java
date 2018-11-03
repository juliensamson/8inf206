package ca.uqac.lecitoyen.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.fragments.userUI.CityfeedFragment;
import ca.uqac.lecitoyen.fragments.userUI.MessageFragment;
import ca.uqac.lecitoyen.fragments.userUI.ForumFragment;
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

    private DatabaseManager dbManager;
    private DatabaseReference dbUsersData;
    private DatabaseReference dbUserProfilPicture;
    private DatabaseReference dbPosts;

    public UserStorage mUserStorage;
    private ArrayList<Post> mPublicationList = new ArrayList<>();
    public ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserStorage> listUserProfilPicture = new ArrayList<>();

    private Toolbar mUserToolbar;
    private TextView mUserToolbarTitle;

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

        //  Create fragment

        //  Initialize auth
        fbAuth = FirebaseAuth.getInstance();

        //  Initialize fragment
        forumFragment = new ForumFragment();
        searchFragment = new SearchFragment();
        cityfeedFragment = new CityfeedFragment();
        messageFragment = new MessageFragment();
        profilFragment   = new ProfilFragment();
        doFragmentTransaction(forumFragment, getString(R.string.fragment_forum), true, "");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    private void updateUI() {

    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message) {
        Log.d(TAG, "doFragmentTransaction");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.user_container, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
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
                break;
            case R.string.fragment_search:
                doFragmentTransaction(searchFragment, getString(R.string.fragment_search), false, "");
                break;
            case R.string.fragment_cityfeed:
                doFragmentTransaction(cityfeedFragment, getString(R.string.fragment_cityfeed), false, "");
                break;
            case R.string.fragment_messages:
                doFragmentTransaction(messageFragment, getString(R.string.fragment_messages), false, "");
                break;
            case R.string.fragment_profil:
                doFragmentTransaction(profilFragment, getString(R.string.fragment_profil), false, "");
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

    public ArrayList<User> getUserList() {
        return this.mUserList;
    }

    public ArrayList<Post> getPublicationList() { return this.mPublicationList;}

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
