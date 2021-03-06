package ca.uqac.lecitoyen.activities;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.fragments.UserEditProfileFragment;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.fragments.UserEditEmailFragment;

public class EditProfilActivity extends BaseActivity implements iHandleFragment {

    private static final String TAG = EditProfilActivity.class.getSimpleName();

    private EditText mNameField;
    private EditText mUserNameField;
    private TextView mEmail;

    private Toolbar mToolbar;
    private TextView mToolbarTitle;
    private int containerId = R.id.edit_profil_container;
    private String currentFragment;

    private User mUserData;

    //Firebase
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private AuthCredential mCredential;
    private String mUid;

    private User mUserAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mUserAuth = bundle.getParcelable("user-auth");
            Log.d(TAG, "user id " + mUserAuth.getName() );
        }else
            Log.e(TAG, "bundle is null");

        //  SetToolbar


        mToolbar = findViewById(R.id.edit_profil_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);

        //  Initiate first fragement
        UserEditProfileFragment fragment = UserEditProfileFragment.newInstance(mUserAuth);
        doFragmentTransaction(containerId, fragment, getString(R.string.fragment_edit_profil), false, "");

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
            case R.string.fragment_edit_profil:
                fragment = UserEditProfileFragment.newInstance(mUserAuth);
                doFragmentTransaction(containerId, fragment, getString(R.string.fragment_edit_profil), false, "");
                break;
            case R.string.fragment_edit_profil_email:
                fragment = new UserEditEmailFragment();
                doFragmentTransaction(containerId, fragment, getString(R.string.fragment_edit_profil_email), false, "");
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(!currentFragment.equals(getString(R.string.fragment_edit_profil))) {
            Fragment fragment = new UserEditProfileFragment();
            doFragmentTransaction(containerId, fragment, getString(R.string.fragment_edit_profil), true, "");
        } else {
            this.finish();
        }
    }

    private ValueEventListener loadUserData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserData = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public User getUserData() { return this.mUserData; }

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
