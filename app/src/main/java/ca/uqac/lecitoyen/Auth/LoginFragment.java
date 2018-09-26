package ca.uqac.lecitoyen.Auth;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";

    private iHandleFragment mHandleFragment;

    private Context mContext;

    private Button mEmailLogin;
    private TextView mSigninFragment;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    MainActivity mParentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = (MainActivity) getActivity();
        mHandleFragment.setToolbarTitle(getTag());

        mAuth = FirebaseAuth.getInstance();
        initFacebookLogin();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_layout, container, false);

        //  Buttons
        view.findViewById(R.id.button_login_email).setOnClickListener(this);
        view.findViewById(R.id.button_facebook_login).setOnClickListener(this);
        view.findViewById(R.id.go_to_signin_fragment).setOnClickListener(this);
        view.findViewById(R.id.forgot_password).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mParentActivity.updateUI(currentUser);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainActivity) getActivity();
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
        int id = view.getId();

        if (id == R.id.button_login_email)
        {
            Intent intent = new Intent(getContext(), EmailPasswordActivity.class);
            Bundle extras = new Bundle();
            extras.putString("display_button", "login");
            intent.putExtras(extras);
            startActivity(intent);
        }
        else if (id == R.id.button_facebook_login)
        {
            Log.e("facebook login","You have clicked");
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
        }
        else if (id == R.id.go_to_signin_fragment)
        {
            mHandleFragment.inflateFragment(getString(R.string.fragment_signin), "");
        }
        else if (id == R.id.forgot_password)
        {
            startActivity(new Intent(getContext(), ResetPasswordActivity.class));
        }
    }

    //
    //  Handle connection to facebook
    //

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

    //
    //  Handle connection to firebase
    //

    public void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
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

                    }
                });
    }
}
