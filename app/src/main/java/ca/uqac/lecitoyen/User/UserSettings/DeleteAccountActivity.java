package ca.uqac.lecitoyen.User.UserSettings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;

public class DeleteAccountActivity extends BaseActivity implements View.OnClickListener{

    private static String TAG = "DeleteAccountActivity";

    private EditText mPasswordField;
    private Button mConfirmDeleteAccount;

    DatabaseManager mDatabaseManager;

    //  Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mDatabaseManager = DatabaseManager.getInstance();

        //  Get user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();

        //  View
        createToolbar("Supprimer compte", true);
        mPasswordField = findViewById(R.id.delete_account_actual_password);
        findViewById(R.id.delete_account_confirm_button).setOnClickListener(this);

        //  Enable button DeleteAccount if password is correct
        //  Cannot do has each the text is changed, the app try to auth and after too many try it blocked request

        /*mPasswordField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d(TAG, "beforeTextChanged " + charSequence.toString());
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d(TAG, "onTextChanged " + charSequence.toString());
                    try {
                        wait(1000);
                        Log.e(TAG, "ok");
                        reauthenticateUser();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Log.d(TAG, "afterTextChanged " + editable.toString());
                }
            });
*/
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
                reauthenticateUser();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reauthenticateUser() {
        Log.d(TAG, "reauthenticateUser");

        if (!mPasswordField.getText().toString().isEmpty())
        {
            //  Check credential
            AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), mPasswordField.getText().toString());

            mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        findViewById(R.id.delete_account_confirm_button).setEnabled(true);
                        findViewById(R.id.delete_account_confirm_button).setBackgroundColor(getResources().getColor(R.color.i_secondary_700));
                    } else {
                        findViewById(R.id.delete_account_confirm_button).setEnabled(false);
                        findViewById(R.id.delete_account_confirm_button).setBackgroundColor(getResources().getColor(R.color.black_100));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            });
        }
    }

    private void deleteAccount() {
        Log.d(TAG, "deleteAccount");

        mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.w(TAG,"Account deleted");
                    // Delete account but also delete data in reference to the account
                    updateDB();
                    //Close all previous activity
                    destroyPreviousActivity(DeleteAccountActivity.this, MainActivity.class);
                } else {
                    Log.e(TAG,"Cannot delete account!");
                }
            }
        });
    }

    private void updateDB() {
        DatabaseReference userReference = mDatabaseManager.getReference();
        userReference.child("users").child(mUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(DeleteAccountActivity.this, "Données supprimer", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Data deleted  ");
                }
                else
                {
                    Log.e(TAG, "Couldn't remove firebase user database ");
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.delete_account_confirm_button:
                showProgressDialog();
                deleteAccount();
                hideProgressDialog();
                break;
            default:
                break;
        }
    }
}
