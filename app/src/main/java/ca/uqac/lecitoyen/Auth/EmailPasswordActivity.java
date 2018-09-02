package ca.uqac.lecitoyen.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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

import ca.uqac.lecitoyen.R;

/**
 * Created by jul_samson on 18-09-02.
 */

public class EmailPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    final private static String TAG = "EmailPasswordActivity";

    private TextView mStatusTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLogin;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        Log.d(TAG, "Activity created");

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

        //  Views
        mStatusTextView = findViewById(R.id.status);
        mEmailField = findViewById(R.id.edit_text_login_email);
        mPasswordField = findViewById(R.id.edit_text_login_password);
        mLogin = findViewById(R.id.button_email_password_login);

        // Buttons
        findViewById(R.id.button_email_password_login).setOnClickListener(this);
        findViewById(R.id.button_email_password_signout).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Activity started");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signInUser(String email, String password) {
        Log.d(TAG, "signInUser: " + email);

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
            mStatusTextView.setText("Connected" + user.getEmail());

            findViewById(R.id.button_email_password_login).setVisibility(View.GONE);
            findViewById(R.id.edit_text_login_email).setVisibility(View.GONE);
            findViewById(R.id.edit_text_login_password).setVisibility(View.GONE);
            findViewById(R.id.button_email_password_signout).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText("Signout");
            findViewById(R.id.button_email_password_login).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_text_login_email).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_text_login_password).setVisibility(View.VISIBLE);
            findViewById(R.id.button_email_password_signout).setVisibility(View.GONE);
        }

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        //if (i == R.id.email_create_account_button) {
        //    createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        //} else
        if (i == R.id.button_email_password_login) {
            signInUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.button_email_password_signout) {
            signOutUser();
        }
        //else if (i == R.id.verify_email_button) {
        //    sendEmailVerification();
        //}
    }
}
