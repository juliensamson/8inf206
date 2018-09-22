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

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;

public class UserActivity extends BaseActivity implements iHandleFragment {

    final private static String TAG = "UserActivity";

    private iHandleFragment mHandleFragment;

    private Toolbar mUserToolbar;
    private TextView mUserToolbarTitle;

    private TextView mTextMessage;

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

        init();

        //  Views
        mUserToolbar = findViewById(R.id.toolbar_user);
        mUserToolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(mUserToolbar);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
                startActivity(new Intent(this, UserSettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
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
        Log.d(TAG, "Inflate " + fragmentTag + " " + message);

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
}
