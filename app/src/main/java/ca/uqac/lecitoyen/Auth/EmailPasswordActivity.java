package ca.uqac.lecitoyen.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.User.UserActivity;
import ca.uqac.lecitoyen.database.AbstractDatabaseManager;
import ca.uqac.lecitoyen.database.UserData;

public class EmailPasswordActivity extends BaseActivity implements View.OnClickListener {

    final private static String TAG = "EmailPasswordActivity";



    private EditText mEmailField;
    private EditText mPasswordField;

    //Firebase
    private FirebaseAuth mAuth;
    private AbstractDatabaseManager mDatabaseManager;
    private UserData mUserData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);
        Log.d(TAG, "Activity created");

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();
        mDatabaseManager = new AbstractDatabaseManager();
        mUserData = new UserData();

        //  Views
        mEmailField = findViewById(R.id.emailpassword_email_field);
        mPasswordField = findViewById(R.id.emailpassword_password_field);

        //  Buttons
        findViewById(R.id.emailpassword_log_in_button).setOnClickListener(this);
        findViewById(R.id.emailpassword_create_account_button).setOnClickListener(this);

        layoutManagement();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.emailpassword_create_account_button)
        {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
        else if (id == R.id.emailpassword_log_in_button)
        {
            signInUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToDatabase(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void signInUser(String email, String password) {
        Log.d(TAG, "signInUser: " + email);

        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(EmailPasswordActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                    updateUI(null);
                }

                hideProgressDialog();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void addUserToDatabase(FirebaseUser user) {
        DatabaseReference ref = mDatabaseManager.getRef();
        mUserData.setUserID(user.getUid());
        mDatabaseManager.writeData(ref, mUserData);
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            startActivity(new Intent(this, UserActivity.class));
            this.finish();
        }
    }

    private void layoutManagement() {

        Log.d(TAG, "layoutManagement");

        Intent previousIntent = getIntent();
        if (previousIntent != null) {
            String previousFragment = previousIntent.getStringExtra("display_button");
            switch (previousFragment) {
                case "login":
                    findViewById(R.id.emailpassword_create_account_button).setVisibility(View.GONE);
                    break;
                case "create_account":
                    findViewById(R.id.emailpassword_log_in_button).setVisibility(View.GONE);
                    break;
                default:
                    Log.e(TAG, "There was some error");
                    break;
            }
        }
    }
}
