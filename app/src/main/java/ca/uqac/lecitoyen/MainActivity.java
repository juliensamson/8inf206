package ca.uqac.lecitoyen;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ca.uqac.lecitoyen.Auth.LoginFragment;
import ca.uqac.lecitoyen.Auth.SigninFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.User.UserActivity;


public class MainActivity extends BaseActivity implements iHandleFragment {

    final private static String TAG = "MainActivity";

    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();


        //AccessToken accessToken = AccessToken.getCurrentAccessToken();
        //boolean isLoggedIn = accessToken != null && !accessToken.isExpired();


        //  Set default toolbar
        setSupportActionBar(mToolbar);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "ca.uqacp.lecitoyen",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Activity started");

        //  Obtenir l'utilisateur courant, s'il est déjà connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            startActivity(new Intent(this, UserActivity.class));
        }
        else
        {
            initLoginFragment();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }


    @Override
    public void setToolbarTitle(String fragmentTag) {

    }

    @Override
    public void inflateFragment(String fragmentTag, String message) {

        Log.d(TAG, "Inflate " + fragmentTag + " " + message);

        if(fragmentTag.equals(getString(R.string.fragment_login)))
        {
            LoginFragment fragment = new LoginFragment();
            doFragmentTransaction(fragment, fragmentTag, false, message);
        }
        else if (fragmentTag.equals(getString(R.string.fragment_signin)))
        {
            SigninFragment fragment = new SigninFragment();
            doFragmentTransaction(fragment, fragmentTag, false, message);
        }
    }

    //
    // Fragment Transaction
    //

    private void initLoginFragment() {
        Log.d(TAG, "Login fragment initialize");
        LoginFragment fragment = new LoginFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_login), false, "");
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.main_container, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }



    public void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            Intent intent = new Intent(this, UserActivity.class);
            Bundle extras = new Bundle();
            extras.putString("display_button", "login");
            intent.putExtras(extras);
            startActivity(intent);
        }
    }
}
