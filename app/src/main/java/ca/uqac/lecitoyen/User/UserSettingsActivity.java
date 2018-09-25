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
import android.widget.Toast;


import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.UserData;

public class UserSettingsActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "UserSettingsActivity";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private UserData mUserData;

    private EditText mNameField;
    private EditText mUserNameField;
    private EditText mEmailField;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
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


        //  Button
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
                if(!mEmailField.getText().toString().equals(""))
                    updateBD(mUser);
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
    public void onClick(View view) {
        int id = view.getId();
        Log.w(TAG, "OnClick");

        switch (id) {
            case R.id.signout_account_button:
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(UserSettingsActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.delete_account_button:
                deleteAccount();
                break;
            default:
                break;
        }

    }

    //
    //  Remplir les champs avec les données disponible
    //

    private void updateUI(final FirebaseUser user) {

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
                    mUserData = new UserData(mUserId, "", "", user.getEmail());
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

    //
    //  Write data to database
    //

    private void updateBD(FirebaseUser user) {

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

    }


    //  Delete Account

    private void deleteAccount() {
        Log.d(TAG, "ingreso a deleteAccount");
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG,"OK! Works fine!");
                    startActivity(new Intent(UserSettingsActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.w(TAG,"Something is wrong!");
                }
            }
        });
    }
}
