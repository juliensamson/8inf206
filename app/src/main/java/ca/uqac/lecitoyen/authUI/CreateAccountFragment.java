package ca.uqac.lecitoyen.authUI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.Interface.iUpdate;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;

public class CreateAccountFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "CreateAccountFragment";

    MainActivity activity;

    private iHandleFragment mHandleFragment;

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mNameField;
    private TextInputEditText mUserNameField;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private TextInputEditText mVerifyPasswordField;

    private FirebaseAuth mAuth;
    private FirebaseUser mNewUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_close_white_24dp,true, true);

        //  Views
        mNameField = view.findViewById(R.id.create_account_first_name);
        mUserNameField = view.findViewById(R.id.create_account_frag_user_name);
        mEmailField = view.findViewById(R.id.create_account_frag_email);
        mPasswordField = view.findViewById(R.id.create_account_frag_password);
        mVerifyPasswordField = view.findViewById(R.id.create_account_frag_password_again);

        //  Buttons
        view.findViewById(R.id.create_account_frag_button).setOnClickListener(this);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mHandleFragment.inflateFragment(R.string.fragment_main_auth,"");
                break;
            default:
                Log.e(TAG, "This onClick doesn't exist");
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (iHandleFragment) getActivity();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case android.R.id.home:
                mHandleFragment.inflateFragment(R.string.fragment_main_auth,"");
                break;
            case R.id.create_account_frag_button:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            default:
                Log.e(TAG, "This onClick doesn't exist");
                break;

        }
    }

    private void updateUI() {
        if (mNewUser != null) {
            startActivity(new Intent(getContext(), UserMainActivity.class));
        } else {
            Log.e(TAG, "No user connected");
        }
    }

    private void updateDB() {
        DatabaseManager db = DatabaseManager.getInstance();
        DatabaseReference ref = db.getReference();

        if(mNewUser != null) {
            Log.e(TAG, "uid: " + mNewUser.getUid());
            User userData = new User(
                    mNewUser.getUid(),
                    mNameField.getText().toString(),
                    mUserNameField.getText().toString(),
                    mEmailField.getText().toString(),
                    "",
                    getCurrentTime(),
                    "email"
            );
            userData.setVerify(false);
            db.writeUserInformation(ref, userData);
        } else {
            Log.e(TAG, "user null?");
        }
    }

    //  TODO: somehow this create an post?
    private void createAccount(String email, String password) {

        if (!validateForm()) {
            return;
        }

        Log.d(TAG, "createAccount:" + email);

        if (password.equals(mVerifyPasswordField.getText().toString()))
        {
            activity.showProgressDialog();

            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    mNewUser = authResult.getUser();
                    updateDB();
                    updateUI();
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

        String name = mNameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameField.setError("Required.");
            valid = false;
        } else {
            mNameField.setError(null);
        }

        String userName = mUserNameField.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            mUserNameField.setError("Required.");
            valid = false;
        } else {
            mUserNameField.setError(null);
        }

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
        String verifyPassword = mVerifyPasswordField.getText().toString();
        if (TextUtils.isEmpty(verifyPassword)) {
            mVerifyPasswordField.setError("Required.");
            valid = false;
        } else {
            mVerifyPasswordField.setError(null);
        }

        return valid;
    }
}
