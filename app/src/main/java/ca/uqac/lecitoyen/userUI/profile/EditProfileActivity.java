package ca.uqac.lecitoyen.userUI.profile;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.userUI.settings.VerifyAccountFragment;

public class EditProfileActivity extends BaseActivity implements iHandleFragment {

    private EditText mNameField;
    private EditText mUserNameField;
    private TextView mEmail;

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private int containerId = R.id.edit_profil_container;
    private String currentFragment;

    //Firebase
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private AuthCredential mCredential;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //  SetToolbar
        mToolbar = findViewById(R.id.edit_profil_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);

        //  Initiate first fragement
        EditProfileFragment fragment = new EditProfileFragment();
        doFragmentTransaction(containerId, fragment, getString(R.string.fragment_edit_profile), false, "");

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

        //  Initialize main database reference
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        //  Initialize main storage reference
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(getUserAuth() != null)
            mUid = mAuth.getCurrentUser().getUid();
    }

    @Override
    public void setToolbarTitle(String fragmentTag) {
        mToolbarTitle.setText(fragmentTag);
    }

    @Override
    public void inflateFragment(int fragmentTagId, String message) {

        Fragment fragment;

        currentFragment = getString(fragmentTagId);

        switch (fragmentTagId)
        {
            case R.string.fragment_edit_profile:
                fragment = new EditProfileFragment();
                doFragmentTransaction(containerId, fragment, getString(R.string.fragment_edit_profile), false, "");
                break;
            case R.string.fragment_change_email:
                fragment = new ChangeEmailFragment();
                doFragmentTransaction(containerId, fragment, getString(R.string.fragment_change_email), false, "");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(!currentFragment.equals(getString(R.string.fragment_edit_profile))) {
            Fragment fragment = new EditProfileFragment();
            doFragmentTransaction(containerId, fragment, getString(R.string.fragment_edit_profile), true, "");
        } else {
            this.finish();
        }
    }

    public FirebaseAuth getUserAuth() {
        return this.mAuth;
    }

    public String getUid() {
        return this.mUid;
    }

    public DatabaseReference getDatabaseRef() {
        return this.mDatabaseRef;
    }

    public StorageReference getStorageRef() {
        return this.mStorageRef;
    }
}
