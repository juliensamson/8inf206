package ca.uqac.lecitoyen.activities;

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

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.fragments.settings.DeleteUserFragment;
import ca.uqac.lecitoyen.fragments.settings.EditPasswordFragment;
import ca.uqac.lecitoyen.fragments.settings.VerifyUserFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.fragments.settings.SettingsMainFragment;

//TODO: Add Verify email button
//TODO: Make sure FAcebook user is added on the database
//TODO: When you change NAMe, username, change the post
//      So if I store only id, so you don't have to change every time the data in post with (name, username)

public class SettingsActivity extends BaseActivity implements iHandleFragment {

    private static String TAG = "SettingsActivity";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private User mUserData;

    private String currFragment;

    private EditText mNameField;
    private EditText mUserNameField;
    private TextView mEmail;

    Toolbar mSettingToolbar;
    TextView mSettingToolbarTitle;

    private SettingsMainFragment mSettingsFragment;
    private VerifyUserFragment mVerifyUserFragment;
    private EditPasswordFragment mEditPasswordFragment;
    private DeleteUserFragment mDeleteUserFragment;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private AuthCredential mCredential;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //  Inintialize fragment
        mSettingsFragment = new SettingsMainFragment();
        mVerifyUserFragment = new VerifyUserFragment();
        mDeleteUserFragment = new DeleteUserFragment();
        mEditPasswordFragment = new EditPasswordFragment();

        //  Inflate initial fragment
        doFragmentTransaction(mSettingsFragment, getString(R.string.fragment_main_user_settings), true, "");


        mDatabaseManager = DatabaseManager.getInstance();
        mUserData = new User();

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //  Toolbar
        mSettingToolbar = findViewById(R.id.toolbar_simple);
        mSettingToolbarTitle = findViewById(R.id.toolbar_simple_title);
        setSupportActionBar(mSettingToolbar);
        /*if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_primary_24dp);
        }
        else
            Log.e(TAG,"Toolbar is null");*/
    }

    @Override
    public void setToolbarTitle(String fragmentTag) {
        mSettingToolbarTitle.setText(fragmentTag);
    }

    @Override
    public void inflateFragment(int fragmentTagId, String message) {

        switch (fragmentTagId)
        {
            case R.string.fragment_main_user_settings:
                doFragmentTransaction(mSettingsFragment, getString(R.string.fragment_main_user_settings), false, "");
                break;
            case R.string.fragment_verify_account:
                doFragmentTransaction(mVerifyUserFragment, getString(R.string.fragment_verify_account), false, "");
                break;
            case R.string.fragment_change_password:
                doFragmentTransaction(mEditPasswordFragment, getString(R.string.fragment_change_password), false, "");
                break;
            case R.string.fragment_delete_account:
                doFragmentTransaction(mDeleteUserFragment, getString(R.string.fragment_delete_account), true, "");
                break;
            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(!currFragment.equals(getString(R.string.fragment_main_user_settings))) {
            doFragmentTransaction(mSettingsFragment, getString(R.string.fragment_main_user_settings), true, "");
        } else {
            this.finish();
        }
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message) {
        Log.d(TAG, "doFragmentTransaction");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        currFragment = tag;
        transaction.replace(R.id.setting_container, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    public void signOutAccount() {
        Log.d(TAG, "signOutAccount");
        LoginManager.getInstance().logOut();
        mAuth.signOut();
        destroyPreviousActivity(SettingsActivity.this, MainActivity.class);
    }


}
