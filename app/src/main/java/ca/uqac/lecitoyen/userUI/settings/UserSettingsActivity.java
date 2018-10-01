package ca.uqac.lecitoyen.userUI.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


import com.facebook.login.LoginManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;

//TODO: Add Verify email button
//TODO: Make sure FAcebook user is added on the database
//TODO: When you change NAMe, username, change the post
//      So if I store only id, so you don't have to change every time the data in post with (name, username)

public class UserSettingsActivity extends BaseActivity implements iHandleFragment {

    private static String TAG = "UserSettingsActivity";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private User mUserData;

    private EditText mNameField;
    private EditText mUserNameField;
    private TextView mEmail;

    Toolbar mSettingToolbar;
    TextView mSettingToolbarTitle;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private AuthCredential mCredential;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        try {
            mUserId = getIntent().getExtras().getString("userid");
        } catch (NullPointerException e) {
            Log.e(TAG, "No Bundle was sent with the intent");
        }

        UserSettingsFragment fragment = new UserSettingsFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_main_user_settings), true, "");


        mDatabaseManager = DatabaseManager.getInstance();
        mUserData = new User();

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //  Toolbar
        mSettingToolbar = findViewById(R.id.toolbar_setting);
        mSettingToolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(mSettingToolbar);
    }

    @Override
    public void setToolbarTitle(String fragmentTag) {
        mSettingToolbarTitle.setText(fragmentTag);
    }

    @Override
    public void inflateFragment(int fragmentTagId, String message) {

        Fragment fragment;

        switch (fragmentTagId)
        {
            case R.string.fragment_verify_account:
                fragment = new VerifyAccountFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_verify_account), false, "");
                break;
            case R.string.fragment_main_user_settings:
                fragment = new UserSettingsFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_main_user_settings), false, "");
                break;
            case R.string.fragment_change_email:
                fragment = new ChangeEmailFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_change_email), false, "");
                break;
            case R.string.fragment_change_password:
                fragment = new ChangePasswordFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_change_password), false, "");
                break;
            case R.string.fragment_delete_account:
                fragment = new DeleteAccountFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_delete_account), true, "");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Fragment fragment = new UserSettingsFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_main_user_settings), true, "");
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message) {
        Log.d(TAG, "doFragmentTransaction");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.setting_container, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    public void signOutAccount() {
        Log.d(TAG, "signOutAccount");
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        destroyPreviousActivity(UserSettingsActivity.this, MainActivity.class);
    }


}
