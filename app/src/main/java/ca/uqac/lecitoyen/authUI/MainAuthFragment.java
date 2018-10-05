package ca.uqac.lecitoyen.authUI;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;

public class MainAuthFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MainAuthFragment";

    private iHandleFragment mHandleFragment;

    LoginButton mFacebookButton;
    Bundle bFacebookData;

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    MainActivity mParentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mParentActivity = (MainActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_auth, container, false);
        Log.d(TAG, "onCreateView");

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(mParentActivity, R.drawable.ic_arrow_back_white_24dp, false, false);

        //View
        mFacebookButton = view.findViewById(R.id.main_auth_facebook_button);

        //  Buttons
        view.findViewById(R.id.main_auth_frag_email_button).setOnClickListener(this);
        view.findViewById(R.id.main_auth_facebook_button).setOnClickListener(this);
        view.findViewById(R.id.main_auth_frag_password_forgotten).setOnClickListener(this);
        view.findViewById(R.id.main_auth_frag_create_account_button).setOnClickListener(this);

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
            case R.id.main_auth_frag_email_button:
                mHandleFragment.inflateFragment(R.string.fragment_login_account,"");
                break;
            case R.id.main_auth_facebook_button:
                initFacebookLogin();
                //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends"));
                break;
            case R.id.main_auth_frag_password_forgotten:
                mHandleFragment.inflateFragment(R.string.fragment_forgot_account,"");
                break;
            case R.id.main_auth_frag_create_account_button:
                mHandleFragment.inflateFragment(R.string.fragment_create_account,"");
                break;
            default:
                break;
        }
    }

    //  FACEBOOK CONNECTION

    //  TODO: Bug lors de la connection. Enlève le username. Et parfois ajoute données à firebase
    private void initFacebookLogin() {

        mCallbackManager = CallbackManager.Factory.create();
        mFacebookButton.setReadPermissions("email", "public_profile");
        mFacebookButton.setFragment(this);
        mFacebookButton.registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getContext(), exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleFacebookAccessToken(LoginResult loginResult) {

        String accessToken = loginResult.getAccessToken().getToken();
        Log.i("accessToken", accessToken);
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.i("LoginActivity", response.toString());
                // Get facebook data from login
                bFacebookData = getFacebookData(object);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken);

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
                            updateDB(user, bFacebookData);
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

    @Nullable
    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        }
        catch(JSONException e) {
            Log.d(TAG,"Error parsing JSON");
        }
        return null;
    }

    //TODO: setLocation
    public void updateDB(FirebaseUser user, Bundle bundle) {
        DatabaseManager db = DatabaseManager.getInstance();
        DatabaseReference ref = db.getReference();

        // TODO: hange where you the info
        User userData = new User(
                user.getUid(),
                bFacebookData.getString("first_name") + " " +
                        bFacebookData.getString("last_name"),
                "",
                user.getEmail(),
                "",
                getCurrentTime(),
                user.getProviderId()
        );
        userData.setVerify(true);

        db.writeUserInformation(ref, userData);
    }

}
