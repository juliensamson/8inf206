package ca.uqac.lecitoyen.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

public class UserSettingsActivity extends BaseActivity implements iUpdate, View.OnClickListener {

    private static String TAG = "UserSettingsActivity";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private UserData mUserData;

    private EditText mNameField;
    private EditText mUserNameField;
    private EditText mEmailField;
    private EditText mLocationField;

    private LinearLayout mVerifiyUserLayout;
    private EditText mVerifyPasswordField;
    private LinearLayout mPasswordFieldLayout;
    private EditText mPasswordField;
    private EditText mPasswordFieldVerify;



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
        mEmailField = findViewById(R.id.user_setting_email);

        mVerifiyUserLayout = findViewById(R.id.user_setting_user_verification_layout);
        mVerifyPasswordField = findViewById(R.id.user_setting_verify_old_password);
        mPasswordFieldLayout = findViewById(R.id.user_setting_password_layout);
        mPasswordField= findViewById(R.id.user_setting_password);
        mPasswordFieldVerify = findViewById(R.id.user_setting_password_verify);

        //  Button
        findViewById(R.id.change_password_button).setOnClickListener(this);
        findViewById(R.id.confirm_old_password_button).setOnClickListener(this);
        findViewById(R.id.confirm_password_button).setOnClickListener(this);
        findViewById(R.id.signout_account_button).setOnClickListener(this);
        findViewById(R.id.delete_account_button).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(mUser);
        Log.w(TAG, "Started");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.confirm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareMenu");
        //menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_confirm:
                showProgressDialog();
                if(!mEmailField.getText().toString().equals(""))
                    updateDB(mUser);
                Log.w(TAG, "Information saved");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
                mEmailField.setText(mUserData.getEmail());

                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });


    }

    //TODO: Make sure if you change EMAIL, change also auth email
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
    //
    //  Button onClick
    //

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.change_password_button:
                if(mVerifiyUserLayout.getVisibility() ==  View.GONE && mPasswordFieldLayout.getVisibility() == View.GONE)
                {
                    mVerifiyUserLayout.setVisibility(View.VISIBLE);
                    mVerifyPasswordField.setText("");
                }
                else {
                    mVerifiyUserLayout.setVisibility(View.GONE);
                    mPasswordFieldLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.confirm_old_password_button:
                confirmOldPasswordAccount();
                break;
            case R.id.confirm_password_button:
                changePasswordAccount();
                break;
            case R.id.signout_account_button:
                signOutAccount();
                break;
            case R.id.delete_account_button:
                deleteAccount();
                break;
            default:
                break;
        }
    }

    private void confirmOldPasswordAccount() {

        try {
             mCredential = EmailAuthProvider.getCredential(
                    mUser.getEmail(),
                    mVerifyPasswordField.getText().toString()
            );

             showProgressDialog();

             mUser.reauthenticate(mCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task)
                 {
                     hideProgressDialog();
                     if(!task.isSuccessful())
                     {
                         Log.e(TAG, "reauthenticate failed");
                         Toast.makeText(UserSettingsActivity.this, "Mot de passe erroné", Toast.LENGTH_SHORT).show();
                     }
                     else
                     {
                         Log.w(TAG, "reauthenticate succeed");
                         Toast.makeText(UserSettingsActivity.this, "Mot de passe vérifié", Toast.LENGTH_SHORT).show();
                         mVerifiyUserLayout.setVisibility(View.GONE);
                         mPasswordFieldLayout.setVisibility(View.VISIBLE);
                     }
                 }
             });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void changePasswordAccount() {
        Log.d(TAG, "changePasswordAccount");

        if (mPasswordField.getText().toString().equals(mPasswordFieldVerify.getText().toString()))
        {
            showProgressDialog();

            mUser.updatePassword(mPasswordField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    hideProgressDialog();
                    if(!task.isSuccessful())
                    {
                        Log.e(TAG, "updatePassword failed");
                        Toast.makeText(UserSettingsActivity.this, "Mot de passe est trop court", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Log.w(TAG, "updatePassword succeed");
                        Toast.makeText(UserSettingsActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                        mPasswordFieldLayout.setVisibility(View.GONE);
                    }
                }
            });
        }
        else
        {
            Log.e(TAG, "Passwords does not correspond");
            Toast.makeText(UserSettingsActivity.this, "Password are different", Toast.LENGTH_SHORT).show();
        }
    }

    private void signOutAccount() {
        Log.d(TAG, "signOutAccount");
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        destroyPreviousActivity(UserSettingsActivity.this, MainActivity.class);
    }


    //  TODO: ReAuthenticate the user. (Confirm old password)
    private void deleteAccount() {
        Log.d(TAG, "deleteAccount");

        mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Delete account but also delete data in reference to the account
                    removeDataFromFirebase();
                    Log.w(TAG,"OK! Works fine!");

                    //Close all previous activity
                    destroyPreviousActivity(UserSettingsActivity.this, MainActivity.class);
                } else {
                    Log.e(TAG,"Cannot delete account!");
                }
            }
        });
    }

    private void removeDataFromFirebase() {
        mUserReference.child("users").child(mUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(UserSettingsActivity.this, "Données supprimer", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Data deleted  ");
                }
                else
                {
                    Log.e(TAG, "Couldn't remove firebase user database ");
                }

            }
        });
    }
}
