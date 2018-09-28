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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.Interface.iUpdate;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.User.UserMainActivity;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;

//TODO: DOnt finish activity if auth failed

public class EmailPasswordActivity extends BaseActivity implements iUpdate, View.OnClickListener {

    final private static String TAG = "EmailPasswordActivity";

    private EditText mEmailField;
    private EditText mPasswordField;

    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);
        Log.d(TAG, "Activity created");

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();
        mDatabaseManager = DatabaseManager.getInstance();

        //  Views
        mEmailField = findViewById(R.id.emailpassword_email_field);
        mPasswordField = findViewById(R.id.emailpassword_password_field);

        //  Buttons
        findViewById(R.id.emailpassword_log_in_button).setOnClickListener(this);
        findViewById(R.id.emailpassword_create_account_button).setOnClickListener(this);

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
                            updateDB(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(EmailPasswordActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                            }
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

    @Override
    public void updateUI(FirebaseUser user) {

        if (user != null)
        {
            startActivity(new Intent(this, UserMainActivity.class));
            this.finish();
        }
        this.finish();
    }

    @Override
    public void updateDB(FirebaseUser user) {
        mUserReference = mDatabaseManager.getReference();
        User userData = new User(user.getUid(), "", "", user.getEmail(), currentTimeMillis);
        mDatabaseManager.writeUserInformation(mUserReference, userData);
    }

}
