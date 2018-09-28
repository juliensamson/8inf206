package ca.uqac.lecitoyen.Auth;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;

public class LoginAccountFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginAccountFragment";

    private iHandleFragment mHandleFragment;

    private FrameLayout mMainLayout;
    private TextInputLayout mTextInputLayout;
    private TextInputEditText mEmailField;
    private TextInputEditText mPasswordField;
    private Button mFacebookButton;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

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

        //  View
        mMainLayout = view.findViewById(R.id.main_layout);
        mTextInputLayout = view.findViewById(R.id.login_account_frag_text_input_layout);
        mEmailField = view.findViewById(R.id.login_account_frag_text_input_email);
        mPasswordField = view.findViewById(R.id.login_account_frag_text_input_password);
        mFacebookButton = view.findViewById(R.id.login_account_frag_facebook_button);

        //  Buttons
        view.findViewById(R.id.login_account_frag_email_button).setOnClickListener(this);
        view.findViewById(R.id.login_account_frag_facebook_button).setOnClickListener(this);
        view.findViewById(R.id.login_account_frag_password_forgotten).setOnClickListener(this);
        view.findViewById(R.id.login_account_frag_create_account_button).setOnClickListener(this);
        view.findViewById(R.id.main_layout).setOnClickListener(this);

        return view;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " " + resultCode);
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            // TODO: Check connexion failure
            case R.id.login_account_frag_email_button:
                if(mFacebookButton.getVisibility() == View.VISIBLE) {
                    mFacebookButton.setVisibility(View.GONE);
                    mTextInputLayout.setVisibility(View.VISIBLE);
                } else {
                    mParentActivity.createToolbar("Se connecter", true);
                    // TODO: Not supported on lower version (like 19). Can't find vector
                    mEmailField.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
                    mPasswordField.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
                    signInUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
                }
                //signInWithEmail();
                break;
            case R.id.login_account_frag_facebook_button:
                initFacebookLogin();
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.login_account_frag_password_forgotten:
                forgottenPassword();
                break;
            case R.id.login_account_frag_create_account_button:
                mHandleFragment.inflateFragment(getString(R.string.fragment_create_account),"");
                break;
            case R.id.main_layout:
                view.findViewById(R.id.login_account_frag_facebook_button).setVisibility(View.VISIBLE);
                mTextInputLayout.setVisibility(View.GONE);
            default:
                break;
        }
    }

    //  EMAIL CONNECTION

    private void signIn(View view) {
        // TODO: Not supported on lower version (like 19). Can't find vector
        mEmailField.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
        mPasswordField.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);
        signInUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
    }

    private void signInWithEmail() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(getContext());

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View alertLoginAccountView = factory.inflate(R.layout.alert_dialog_sign_in, null);

        final EditText emailField = alertLoginAccountView.findViewById(R.id.alert_dialog_email);
        final EditText passwordField = alertLoginAccountView.findViewById(R.id.alert_dialog_password);

        // TODO: Not supported on lower version (like 19). Can't find vector
        emailField.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);
        passwordField.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock_black_24dp, 0, 0, 0);

        builder.setTitle("Se connecter")
                .setView(alertLoginAccountView)
                .setPositiveButton("SE CONNECTER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        signInUser(emailField.getText().toString(), passwordField.getText().toString());
                    }
                })
                .setNeutralButton("ANNULER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_input_black_24dp)
                .show();
    }

    private void signInUser(String email, String password) {
        Log.d(TAG, "signInUser: " + email);

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "No email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "No password", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Authentication failed", Toast.LENGTH_LONG).show();
                    mParentActivity.updateUI(null);
                }

                mParentActivity.hideProgressDialog();
            }
        });
    }

    //  FACEBOOK CONNECTION

    private void initFacebookLogin() {

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() { }

                    @Override
                    public void onError(FacebookException exception) { }
                });
    }

    public void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mParentActivity.showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mParentActivity.updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mParentActivity.updateUI(null);
                        }
                        mParentActivity.hideProgressDialog();
                    }
                });
    }


    //  FORGOTTEN PASSWORD

    private void forgottenPassword() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(getContext());

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View alertLoginAccountView = factory.inflate(R.layout.alert_dialog_sign_in, null);

        final EditText emailField = alertLoginAccountView.findViewById(R.id.alert_dialog_email);
        final EditText passwordField = alertLoginAccountView.findViewById(R.id.alert_dialog_password);
        emailField.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_email_black_24dp, 0, 0, 0);

        passwordField.setVisibility(View.GONE);

        builder.setTitle("Mot de passe oublié")
                .setView(alertLoginAccountView)
                .setPositiveButton("ENVOYER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        resetEmailPassword(emailField.getText().toString());
                    }
                })
                .setNeutralButton("ANNULER", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_input_black_24dp)
                .show();
    }

    private void resetEmailPassword(String email) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
            return;
        }

        mParentActivity.showProgressDialog();

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        Log.w(TAG, "Email sent");
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        mParentActivity.hideProgressDialog();
                    }
                });
    }
}
