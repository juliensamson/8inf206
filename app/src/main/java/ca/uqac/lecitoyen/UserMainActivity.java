package ca.uqac.lecitoyen;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.Auth.EmailPasswordActivity;

public class UserMainActivity extends AppCompatActivity implements View.OnClickListener {

    final private static String TAG = "UserMainActivity";

    private Toolbar mUserToolbar;

    private TextView mStatusTextView;
    private TextView mDetailTextView;

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        Log.d(TAG, "Activity created");

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

        //  Views
        mUserToolbar = findViewById(R.id.toolbar_user);
        setSupportActionBar(mUserToolbar);
        mStatusTextView = findViewById(R.id.emailpassword_status);
        mDetailTextView = findViewById(R.id.emailpassword_detail);

        //  Buttons
        findViewById(R.id.emailpassword_sign_out_button).setOnClickListener(this);
        findViewById(R.id.emailpassword_verify_email_button).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.emailpassword_sign_out_button) {
            signOutUser();
        } else if (id == R.id.emailpassword_verify_email_button) {
            sendEmailVerification();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            mStatusTextView.setText("Connected " + user.getEmail());
            mDetailTextView.setText("User " + user.getUid());
            findViewById(R.id.emailpassword_verify_email_button).setEnabled(!user.isEmailVerified());
        } else {
            this.finish();
        }

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
                            Toast.makeText(UserMainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(UserMainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void signOutUser() {
        mAuth.signOut();
        updateUI(null);
    }
}
