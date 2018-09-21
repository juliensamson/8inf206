package ca.uqac.lecitoyen.Auth;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "ResetPasswordActivity";

    private EditText mEmailField;
    private Toolbar mDefaultToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //  View
        mEmailField = findViewById(R.id.reset_password_email);
        mDefaultToolbar = findViewById(R.id.toolbar_default_reset);
        setSupportActionBar(mDefaultToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //  Button
        findViewById(R.id.reset_password_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.reset_password_button:
                resetEmailPassword();
                break;
            default:
                break;
        }
    }

    //
    //  Reset password
    //

    private void resetEmailPassword() {

        String email = mEmailField.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.w(TAG, "Email sent");
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else
                        {
                            Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        hideProgressDialog();
                    }
                });
    }
}
