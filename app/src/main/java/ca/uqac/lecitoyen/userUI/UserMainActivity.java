package ca.uqac.lecitoyen.userUI;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.cityfeed.CityfeedFragment;
import ca.uqac.lecitoyen.userUI.newsfeed.NewsfeedFragment;
import ca.uqac.lecitoyen.userUI.messaging.MessageFragment;
import ca.uqac.lecitoyen.userUI.profile.ProfilFragment;
import ca.uqac.lecitoyen.userUI.search.SearchFragment;
import ca.uqac.lecitoyen.userUI.settings.UserSettingsActivity;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

//TODO: Make the RecyclerView load automatically after making a post

public class UserMainActivity extends BaseActivity implements iHandleFragment {

    final private static String TAG = "UserMainActivity";

    private iHandleFragment mHandleFragment;

    private DatabaseManager dbManager;
    private DatabaseReference dbUsersData;
    private DatabaseReference dbUserProfilPicture;
    private DatabaseReference dbUsersProfilPicture;

    public UserStorage mUserStorage;
    public ArrayList<Post> mPostList = new ArrayList<>();
    public ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserStorage> listUserProfilPicture = new ArrayList<>();

    private Toolbar mUserToolbar;
    private TextView mUserToolbarTitle;

    //Firebase
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;


    private BottomNavigation mBottomNavigation;
    private BottomNavigation.OnMenuItemSelectionListener mOnNavigationItemSelectedListener;


        /*@Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Log.d(TAG, "OnNavigationItem");

            switch (item.getItemId())
            {
                case R.id.navigation_newsfeed:
                    inflateFragment(R.string.fragment_newsfeed, "");
                    return true;
                case R.id.navigation_search:
                    inflateFragment(R.string.fragment_search, "");
                    return true;
                case R.id.navigation_cityfeed:
                    inflateFragment(R.string.fragment_cityfeed, "");
                    return true;
                case R.id.navigation_messages:
                    inflateFragment(R.string.fragment_messages, "");
                    return true;
                case R.id.navigation_profil:
                    inflateFragment(R.string.fragment_profil, "");
                    return true;
            }
            return false;
        }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Log.d(TAG, "Activity created");

        //  Database
        dbManager = DatabaseManager.getInstance();

        //  Initialize auth
        fbAuth = FirebaseAuth.getInstance();

        init();

        //getThreadsData();
        //  Views
        //mUserToolbar = findViewById(R.id.toolbar_user);
        //mUserToolbarTitle = findViewById(R.id.toolbar_title);
        //setSupportActionBar(mUserToolbar);

        //  Bottom navigation
        mBottomNavigation = findViewById(R.id.navigation);
        mBottomNavigation.setDefaultSelectedIndex(0);
        mBottomNavigation.setDefaultTypeface(Typeface.DEFAULT_BOLD);
        mBottomNavigation.setOnMenuItemClickListener(loadBottonNavigation());
        //BottomNavigationViewHelper.disableShiftMode(navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

                //  Get main user profil picture
                dbUserProfilPicture = dbManager.getDatabaseUserProfilPicture(uid);

                //  Get the list of all users
                dbUsersData = dbManager.getDatabaseUser("");

                //  Get the list of all picture id
                dbUsersProfilPicture = dbManager.getReference().child("user-picture");

                updateUI();
            }
        } else {
            Log.e(TAG, "auth is null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.w(TAG, "menu created");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG, "item selected");
        switch (item.getItemId())
        {
            case R.id.menu_setting:
                Log.w(TAG, "menu_setting clicked");
                startActivityWithBundle(UserSettingsActivity.class, "userid", fbUser.getUid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    private void updateUI() {
        //  Main user profil picture
        //dbUserProfilPicture.addListenerForSingleValueEvent(loadUserProfilPicture());

        //  List of user list data
        dbUsersData.addListenerForSingleValueEvent(loadUserList());

        //  List of user profil picture
        //dbUsersProfilPicture.addListenerForSingleValueEvent(loadListUserProfilPicture());
    }

    private void init() {
        Log.d(TAG, "init");
        NewsfeedFragment fragment = new NewsfeedFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_newsfeed), false, "");
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
        //mUserToolbarTitle.setText(fragmentTag);
    }

    @Override
    public void inflateFragment(int fragmentTagId, String message) {

        Fragment fragment;

        switch (fragmentTagId)
        {
            case R.string.fragment_newsfeed:
                fragment = new NewsfeedFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_newsfeed), false, "");
                break;
            case R.string.fragment_search:
                fragment = new SearchFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_search), false, "");
                break;
            case R.string.fragment_cityfeed:
                fragment = new CityfeedFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_cityfeed), false, "");
                break;
            case R.string.fragment_messages:
                fragment = new MessageFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_messages), false, "");
                break;
            case R.string.fragment_profil:
                fragment = new ProfilFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_profil), false, "");
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
                        inflateFragment(R.string.fragment_newsfeed, "");
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
            }

        };
    }

    /*

            Firebase data

     */

    private ValueEventListener loadUserList() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    mUserList.add(userSnapshot.getValue(User.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "loadUserData failed " + databaseError.getMessage());
            }
        };
    }

    /*private ValueEventListener loadListUserProfilPicture() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot userPicture = dataSnapshot.child("profil-picture");

                Log.e(TAG, "User: " + userPicture.getValue());
                for(DataSnapshot picture: userPicture.getChildren()) {
                    Log.e(TAG, "User: " + picture.getValue());
                    if(picture.getValue(UserStorage.class).isProfilPicture())
                        listUserProfilPicture.add(picture.getValue(UserStorage.class));
                    //for(DataSnapshot userPicture: user.getChildren()) {
                    //    if(userPicture.getValue(UserStorage.class).isProfilPicture())
                    //        listUserProfilPicture.add(userPicture.getValue(UserStorage.class));
                    //}
                }
                Log.e(TAG, "loadUserSProfilPicture succeed");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "loadUserProfilPicture failed " + databaseError.getMessage());
            }
        };
    }*/

    /*private ValueEventListener loadUserProfilPicture() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserStorage = dataSnapshot.getValue(UserStorage.class);
                Log.e(TAG, "loadUserProfilPicture succeed");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "loadUserProfilPicture failed " + databaseError.getMessage());
            }
        };
    }*/

    //  TODO: - Make Key,Value a list, map, etc. in order to add more "extras" to the Bundle
    //        - Allow to sent the class User to get the info directly. and not call mAuth on setting. (make it faster)
    void startActivityWithBundle(Class activity, String key, String value) {

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
