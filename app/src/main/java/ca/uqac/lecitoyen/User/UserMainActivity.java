package ca.uqac.lecitoyen.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
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
import ca.uqac.lecitoyen.User.UserFragments.CityFragment;
import ca.uqac.lecitoyen.User.UserFragments.HomeFragment;
import ca.uqac.lecitoyen.User.UserFragments.MessageFragment;
import ca.uqac.lecitoyen.User.UserSettings.UserSettingsActivity;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;

//TODO: Make the RecyclerView load automatically after making a post

public class UserMainActivity extends BaseActivity implements iHandleFragment {

    final private static String TAG = "UserMainActivity";

    private iHandleFragment mHandleFragment;

    public DatabaseManager mDatabaseManager;
    public DatabaseReference mReference;

    public ArrayList<Post> mPostList = new ArrayList<>();

    private Toolbar mUserToolbar;
    private TextView mUserToolbarTitle;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Log.d(TAG, "OnNavigationItem");

            switch (item.getItemId()) {
                case R.id.navigation_city:
                    inflateFragment(getString(R.string.fragment_city), "");
                    return true;
                case R.id.navigation_home:
                    inflateFragment(getString(R.string.fragment_home), "");
                    return true;
                case R.id.navigation_messages:
                    inflateFragment(getString(R.string.fragment_messages), "");
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Log.d(TAG, "Activity created");

        //  Database
        mDatabaseManager = DatabaseManager.getInstance();
        mReference = mDatabaseManager.getReference();

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

        init();

        getThreadsData();
        //  Views
        mUserToolbar = findViewById(R.id.toolbar_user);
        mUserToolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(mUserToolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
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
                startActivityWithBundle(UserSettingsActivity.class, "userid", mUser.getUid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    public ArrayList<Post> getPostArrayList() {
        return this.mPostList;
    }

    private void init() {
        Log.d(TAG, "init");
        CityFragment fragment = new CityFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_city), false, "");
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
    public void inflateFragment(String fragmentTag, String message) {
        Log.d(TAG, "Inflate " + fragmentTag);

        if(fragmentTag.equals(getString(R.string.fragment_city)))
        {
            CityFragment fragment = new CityFragment();
            doFragmentTransaction(fragment, fragmentTag, false, message);
        }
        else if (fragmentTag.equals(getString(R.string.fragment_home)))
        {
            HomeFragment fragment = new HomeFragment();
            doFragmentTransaction(fragment, fragmentTag, false, message);
        }
        else if (fragmentTag.equals(getString(R.string.fragment_messages)))
        {
            MessageFragment fragment = new MessageFragment();
            doFragmentTransaction(fragment, fragmentTag, false, message);
        }
    }

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

    private void getThreadsData() {
        DatabaseManager
                .getInstance()
                .getReference()
                .child("threads")
                .orderByChild("inverseDate")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Log.d(TAG, "onDataChange:" + dataSnapshot.getKey());
                    Post post = postSnapshot.getValue(Post.class);
                    mPostList.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });

    }



}
