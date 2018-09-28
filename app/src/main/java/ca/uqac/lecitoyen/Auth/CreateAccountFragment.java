package ca.uqac.lecitoyen.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.Interface.iUpdate;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.User.UserMainActivity;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;

public class CreateAccountFragment extends Fragment implements iUpdate, View.OnClickListener {

    private static final String TAG = "CreateAccountFragment";

    MainActivity mParentActivity;

    private iHandleFragment mHandleFragment;

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mFirstNameField;
    private TextInputEditText mLastNameField;
    private TextInputEditText mUserNameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private TextInputEditText mVerifyPasswordField;

    private TextView mLoginFragment;


    private FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = (MainActivity) getActivity();
        mHandleFragment.setToolbarTitle(getTag());

        auth = mParentActivity.getAuth();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        try {
            Toolbar toolbar = getActivity().findViewById(R.id.main_toolbar);
            mParentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setHasOptionsMenu(true);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }

        //  Views
        mFirstNameField = view.findViewById(R.id.create_account_first_name);
        mLastNameField = view.findViewById(R.id.create_account_last_name);
        mUserNameField = view.findViewById(R.id.create_account_frag_user_name);
        mEmailField = view.findViewById(R.id.create_account_frag_email);
        mPasswordField = view.findViewById(R.id.create_account_frag_password);
        mVerifyPasswordField = view.findViewById(R.id.create_account_frag_password_again);

        //  Buttons
        view.findViewById(R.id.create_account_frag_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mHandleFragment.inflateFragment(getString(R.string.fragment_login_account),"");
                break;
            default:
                Log.e(TAG, "This onClick doesn't exist");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainActivity) getActivity();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case android.R.id.home:
                //change frag
                break;
            case R.id.create_account_frag_button:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            default:
                Log.e(TAG, "This onClick doesn't exist");
                break;

        }
    }

    @Override
    public void updateUI(FirebaseUser user) {
        if (user != null)
        {
            startActivity(new Intent(getContext(), UserMainActivity.class));
        }
    }

    @Override
    public void updateDB(FirebaseUser user) {
        DatabaseManager db = DatabaseManager.getInstance();
        DatabaseReference ref = db.getReference();

        User userData = new User(
                user.getUid(),
                mFirstNameField.getText().toString() + " " + mLastNameField.getText().toString(),
                mUserNameField.getText().toString(),
                user.getEmail(),
                System.currentTimeMillis()
        );

        db.writeUserInformation(ref, userData);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        if (!validateForm()) {
            return;
        }

        if (password.equals(mVerifyPasswordField.getText().toString()))
        {
            mParentActivity.showProgressDialog();

            // [START create_user_with_email]
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(mParentActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                updateDB(user);
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(getContext(), "User with this email already exist.", Toast.LENGTH_SHORT).show();
                                }
                                updateUI(null);
                            }

                            // [START_EXCLUDE]
                            mParentActivity.hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getCause().getMessage());
                }
            });
            // [END create_user_with_email]
        }
        else {
            Log.e(TAG, "Password different");
            Toast.makeText(getContext(), getString(R.string.toast_password_different), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String firstName = mFirstNameField.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameField.setError("Required.");
            valid = false;
        } else {
            mFirstNameField.setError(null);
        }

        String lastName = mLastNameField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mLastNameField.setError("Required.");
            valid = false;
        } else {
            mLastNameField.setError(null);
        }
        String userName = mUserNameField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mUserNameField.setError("Required.");
            valid = false;
        } else {
            mUserNameField.setError(null);
        }

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        String verifyPassword = mVerifyPasswordField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            mVerifyPasswordField.setError("Required.");
            valid = false;
        } else {
            mVerifyPasswordField.setError(null);
        }

        return valid;
    }
}
