package ca.uqac.lecitoyen.User.UserSettings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.Interface.iUpdate;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.UserData;

//TODO: Add Verify email button

public class UserSettingsActivity extends BaseActivity implements iUpdate, View.OnClickListener {

    private static String TAG = "UserSettingsActivity";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private UserData mUserData;

    private EditText mNameField;
    private EditText mUserNameField;
    private TextView mEmail;


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

        mDatabaseManager = DatabaseManager.getInstance();
        mUserData = new UserData();

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //  Toolbar
        showToolbar(TAG,"Paramètres");

        //  View
        mNameField = findViewById(R.id.user_setting_realname);
        mUserNameField = findViewById(R.id.user_setting_username);
        mEmail = findViewById(R.id.user_setting_email);

        //  Button
        findViewById(R.id.change_email_button).setOnClickListener(this);
        findViewById(R.id.change_password_button).setOnClickListener(this);
        findViewById(R.id.signout_account_button).setOnClickListener(this);
        findViewById(R.id.delete_account_button).setOnClickListener(this);

        updateUI(mUser);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Started");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "Paused");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Stopped");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.confirm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_confirm:
                showProgressDialog();
                if(!mUserNameField.getText().toString().equals(""))
                    updateDB(mUser);
                Log.w(TAG, "Information saved");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateUI(final FirebaseUser user) {

        //  Initialize database manager
        mUserReference = mDatabaseManager.getReference();

        showProgressDialog();

        mUserReference.child("users").child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //  Add the user to the database if he wasn't added before
                mUserData = dataSnapshot.getValue(UserData.class);
                if(mUserData == null)
                {
                    mUserData = new UserData(mUserId, "", "", user.getEmail(), currentTimeMillis);
                }

                mNameField.setText(mUserData.getRealName());
                mUserNameField.setText(mUserData.getUserName());
                mEmail.setText(mUserData.getEmail());

                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });


    }

    //TODO: Check if USERNAME already exist
    @Override
    public void updateDB(FirebaseUser user) {

        mUserData.setRealName(mNameField.getText().toString());
        mUserData.setUserName(mUserNameField.getText().toString());

        mUserReference.child("users").child(mUserId).setValue(mUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserSettingsActivity.this, "Change saved", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Data inserted ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserSettingsActivity.this, "Couldn't be saved", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Something went wrong inserting data");
                    }
                });
        hideProgressDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.change_email_button:
                startActivity(new Intent(UserSettingsActivity.this, ChangeEmailActivity.class));
                break;
            case R.id.change_password_button:
                startActivity(new Intent(UserSettingsActivity.this, ChangePasswordActivity.class));
                break;
            case R.id.delete_account_button:
                startActivity(new Intent(UserSettingsActivity.this, DeleteAccountActivity.class));
                break;
            case R.id.signout_account_button:
                signOutAccount();
                break;
            default:
                break;
        }
    }

    private void signOutAccount() {
        Log.d(TAG, "signOutAccount");
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        destroyPreviousActivity(UserSettingsActivity.this, MainActivity.class);
    }


}
