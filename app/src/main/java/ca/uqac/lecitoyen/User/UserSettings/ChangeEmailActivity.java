package ca.uqac.lecitoyen.User.UserSettings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.Interface.iUpdate;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.UserData;

public class ChangeEmailActivity extends BaseActivity implements iUpdate {

    private static String TAG = "ChangeEmailActivity";

    private EditText mActualPasswordField;
    private EditText mNewEmailField;
    private EditText mVerifyNewEmailField;

    //  Firebase Database
    private DatabaseManager mDatabaseManager;

    //  Firebase Authentification
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        Log.d(TAG, "Created");

        //  Get user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();

        //  Initiate database
        mDatabaseManager = DatabaseManager.getInstance();

        //  View
        showToolbar(TAG,"Email");
        mActualPasswordField = findViewById(R.id.change_email_actual_password);
        mNewEmailField= findViewById(R.id.change_email_new_email);
        mVerifyNewEmailField = findViewById(R.id.change_email_verify_new_email);
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
                        && !mNewEmailField.getText().toString().equals("")
                        && !mVerifyNewEmailField.getText().toString().equals(""))
                    reauthenticateUser();
                else
                    Toast.makeText(ChangeEmailActivity.this, "Un des champs est vide", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
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
                    updateEmail();
                }
                else
                {
                    Toast.makeText(ChangeEmailActivity.this, "Mot de passe actuel erroné", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //TODO: Check if the email is valid
    private void updateEmail() {
        Log.d(TAG, "updateEmail");

        if(isValidEmail(mNewEmailField.getText().toString()))
        {
            if (mNewEmailField.getText().toString().equals(mVerifyNewEmailField.getText().toString()))
            {
                showProgressDialog();
                mUser.updateEmail(mNewEmailField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();
                        if (task.isSuccessful())
                        {
                            Log.w(TAG, "updateEmail succeed");
                            updateDB(mUser);
                            Toast.makeText(ChangeEmailActivity.this, "Email changed", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else
                        {
                            Log.e(TAG, "updateEmail failed");
                            Toast.makeText(ChangeEmailActivity.this, "Email not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Log.e(TAG, "Email does not correspond");
                Toast.makeText(ChangeEmailActivity.this, "Email are different", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ChangeEmailActivity.this, "The email is not valid", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void updateUI(FirebaseUser user) {

    }

    @Override
    public void updateDB(FirebaseUser user) {
        Log.d(TAG, "updateDB");
        DatabaseReference userReference = mDatabaseManager.getReference();

        userReference.child("users").child(mUserId).child("email").setValue(mNewEmailField.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChangeEmailActivity.this, "Change saved", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Data inserted ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChangeEmailActivity.this, "Couldn't be saved", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Something went wrong inserting data");
                    }
                });
        hideProgressDialog();
    }
}
