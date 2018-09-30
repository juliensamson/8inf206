package ca.uqac.lecitoyen.Auth;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;

/**
 * A simple {@link Fragment} subclass.
 */

//TODO: Check if the user is already connected on a device, because it bug everything so far
public class LoginAccountFragment extends BaseFragment implements View.OnClickListener {


    private static final String TAG = "LoginAccountFragment";

    private iHandleFragment mHandleFragment;

    private TextInputLayout mTextInputLayout;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;

    private FirebaseAuth mAuth;

    MainActivity mParentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mParentActivity = (MainActivity) getActivity();
        mHandleFragment.setToolbarTitle(getTag());

        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_account, container, false);
        Log.d(TAG, "onCreateView");

        setFragmentToolbar(mParentActivity, R.id.main_toolbar, R.drawable.ic_arrow_back_white_24dp, true, true);

        //  View
        mTextInputLayout = view.findViewById(R.id.login_account_frag_text_input_layout);
        mEmailField = view.findViewById(R.id.login_account_frag_text_input_email);
        mPasswordField = view.findViewById(R.id.login_account_frag_text_input_password);

        //  Buttons
        view.findViewById(R.id.login_account_frag_email_button).setOnClickListener(this);
        view.findViewById(R.id.login_account_frag_password_forgotten).setOnClickListener(this);

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
                mHandleFragment.inflateFragment(R.string.fragment_main_auth,"");
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
        Log.d(TAG, "onStart");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mParentActivity.updateUI(currentUser);
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
            // TODO: Check connexion failure
            case R.id.login_account_frag_email_button:
                signInUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.login_account_frag_password_forgotten:
                mHandleFragment.inflateFragment(R.string.fragment_forgot_account,"");
                break;
            default:
                break;
        }
    }

    private void signInUser(String email, String password) {
        Log.d(TAG, "signInUser: " + email);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), R.string.toast_email_field_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), R.string.toast_password_field_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        mParentActivity.showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    mParentActivity.updateUI(user);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(getContext(), R.string.toast_email_auth_fail, Toast.LENGTH_LONG).show();
                    mParentActivity.updateUI(null);
                }
                mParentActivity.hideProgressDialog();
            }
        });
    }

}
