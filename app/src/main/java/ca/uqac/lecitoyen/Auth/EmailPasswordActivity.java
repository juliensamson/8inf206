package ca.uqac.lecitoyen.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;

/**
 * Created by jul_samson on 18-09-02.
 */

public class EmailPasswordActivity extends MainActivity implements View.OnClickListener {

    final private static String TAG = "EmailPasswordActivity";

    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailpassword);
        Log.d(TAG, "Activity created");

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

        //  Views
        mStatusTextView = findViewById(R.id.emailpassword_status);
        mDetailTextView = findViewById(R.id.emailpassword_detail);
        mEmailField = findViewById(R.id.emailpassword_email_field);
        mPasswordField = findViewById(R.id.emailpassword_password_field);

        //  Buttons
        findViewById(R.id.emailpassword_log_in_button).setOnClickListener(this);
        findViewById(R.id.emailpassword_sign_out_button).setOnClickListener(this);
        findViewById(R.id.emailpassword_create_account_button).setOnClickListener(this);
        findViewById(R.id.emailpassword_verify_email_button).setOnClickListener(this);

        layoutManagement();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Activity started");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        if (!validateForm()) {
            return;
        }

        //showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.emailpassword_verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.emailpassword_verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(EmailPasswordActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }


    private void signInUser(String email, String password) {
        Log.d(TAG, "signInUser: " + email);
        //findViewById(R.id.button_create_account_email).setVisibility(View.GONE);
        if(!validateForm()) {
            return;
        }

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
            }
        });
    }

    private void signOutUser() {
        mAuth.signOut();
        updateUI(null);
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

    private void updateUI(FirebaseUser user) {

        if(user != null) {
            mStatusTextView.setText("Connected " + user.getEmail());
            mDetailTextView.setText("User " + user.getUid());

            findViewById(R.id.emailpassword_fields_layout).setVisibility(View.GONE);
            findViewById(R.id.emailpassword_log_in_layout).setVisibility(View.GONE);
            findViewById(R.id.emailpassword_sign_out_layout).setVisibility(View.VISIBLE);

            findViewById(R.id.emailpassword_verify_email_button).setEnabled(!user.isEmailVerified());

        } else {
            mStatusTextView.setText("Signout");
            mDetailTextView.setText(null);

            findViewById(R.id.emailpassword_fields_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.emailpassword_log_in_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.emailpassword_sign_out_layout).setVisibility(View.GONE);
        }

    }

    private void layoutManagement() {

        Log.d(TAG, "layoutManagement");

        Intent previousIntent = getIntent();
        if(previousIntent != null) {
            String previousFragment = previousIntent.getStringExtra("display_button");
            switch (previousFragment){
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

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.emailpassword_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (id == R.id.emailpassword_log_in_button) {
            signInUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (id == R.id.emailpassword_sign_out_button) {
            signOutUser();
        } else if (id == R.id.emailpassword_verify_email_button) {
            sendEmailVerification();
        }
    }
}
