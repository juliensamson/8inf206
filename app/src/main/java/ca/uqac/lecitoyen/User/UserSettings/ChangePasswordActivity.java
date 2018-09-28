package ca.uqac.lecitoyen.User.UserSettings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.R;

public class ChangePasswordActivity extends BaseActivity {

    private static String TAG = "ChangePasswordActivity";

    private EditText mActualPasswordField;
    private EditText mNewPasswordField;
    private EditText mVerifyNewPasswordField;

    //  Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        //  Get user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //  View
        createToolbar("Mot de passe", true);
        mActualPasswordField = findViewById(R.id.change_password_actual_password);
        mNewPasswordField= findViewById(R.id.change_password_new_password);
        mVerifyNewPasswordField = findViewById(R.id.change_password_verify_new_password);
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
                if(!mActualPasswordField.getText().toString().equals("")
                        && !mNewPasswordField.getText().toString().equals("")
                        && !mVerifyNewPasswordField.getText().toString().equals(""))
                    reauthenticateUser();
                else
                    Toast.makeText(ChangePasswordActivity.this, "Un des champs est vide", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reauthenticateUser() {
        Log.d(TAG, "reauthenticateUser");
        //  Check credential
        AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), mActualPasswordField.getText().toString());

        showProgressDialog();
        mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                hideProgressDialog();

                if(task.isSuccessful())
                {
                    updatePassword();
                }
                else
                {
                    Toast.makeText(ChangePasswordActivity.this, "Mot de passe actuel erroné", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePassword() {
        Log.d(TAG, "updatePassword");
        if (mNewPasswordField.getText().toString().equals(mVerifyNewPasswordField.getText().toString())) {

            showProgressDialog();
            mUser.updatePassword(mNewPasswordField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    hideProgressDialog();

                    if (task.isSuccessful())
                    {
                        Log.w(TAG, "updatePassword succeed");
                        Toast.makeText(ChangePasswordActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                        finish();
                       }
                    else
                    {
                        Log.e(TAG, "updatePassword failed");
                        Toast.makeText(ChangePasswordActivity.this, "Mot de passe est trop court", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Log.e(TAG, "Passwords does not correspond");
            Toast.makeText(ChangePasswordActivity.this, "Password are different", Toast.LENGTH_SHORT).show();
        }
    }

}
