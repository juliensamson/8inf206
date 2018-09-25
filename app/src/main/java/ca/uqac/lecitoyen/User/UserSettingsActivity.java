package ca.uqac.lecitoyen.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.AbstractDatabaseManager;
import ca.uqac.lecitoyen.database.UserData;

public class UserSettingsActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "UserSettingsActivity";

    private AbstractDatabaseManager mDatabaseManager;
    private UserData mUserData;

    private EditText mNameField;
    private EditText mUserNameField;
    private EditText mEmailField;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        //  Initialize database manager
        mDatabaseManager = new AbstractDatabaseManager();
        mUserData = new UserData();

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        setFieldWithData(currentUser);
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
                addInformation();
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

    private void setFieldWithData(FirebaseUser user) {
        mEmailField.setText(user.getEmail());
    }

    //
    //  Write data to database
    //

    private void addInformation() {
        DatabaseReference ref = mDatabaseManager.getRef();
        mUserData.setRealName(mNameField.getText().toString());
        mUserData.setUserName(mUserNameField.getText().toString());
        mDatabaseManager.writeData(ref, mUserData);
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
