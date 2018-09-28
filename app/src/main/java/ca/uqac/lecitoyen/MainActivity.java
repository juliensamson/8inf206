package ca.uqac.lecitoyen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.Auth.EmailPasswordActivity;
import ca.uqac.lecitoyen.Auth.LoginAccountFragment;
import ca.uqac.lecitoyen.Auth.CreateAccountFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.User.UserMainActivity;
import ca.uqac.lecitoyen.User.UserSettings.ChangeEmailActivity;


public class MainActivity extends BaseActivity implements iHandleFragment {

    final private static String TAG = "MainActivity";

    private Toolbar mToolbar;
    private TextView mToolbarTitle;

    private FirebaseAuth mAuth;

    public FirebaseAuth getAuth() {
        return this.mAuth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

        mToolbar = findViewById(R.id.main_toolbar);
        mToolbarTitle = findViewById(R.id.toolbar_title);


        setSupportActionBar(mToolbar);
        mToolbarTitle.setText("Bienvenue");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Activity started");

        //  Obtenir l'utilisateur courant, s'il est déjà connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
        {
            startActivity(new Intent(this, UserMainActivity.class));
        }
        else
        {
            initLoginFragment();
        }

    }

    @Override
    public void setToolbarTitle(String fragmentTag) {
        setSupportActionBar(mToolbar);
        mToolbarTitle.setText(fragmentTag);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void inflateFragment(String fragmentTag, String message) {

        Log.d(TAG, "Inflate " + fragmentTag + " " + message);

        if(fragmentTag.equals(getString(R.string.fragment_login_account)))
        {
            LoginAccountFragment fragment = new LoginAccountFragment();
            doFragmentTransaction(fragment, fragmentTag, false, message);
        }
        else if (fragmentTag.equals(getString(R.string.fragment_create_account)))
        {
            CreateAccountFragment fragment = new CreateAccountFragment();
            doFragmentTransaction(fragment, fragmentTag, false, message);
        }
    }

    //
    // Fragment Transaction
    //

    private void initLoginFragment() {
        Log.d(TAG, "Login fragment initialize");
        LoginAccountFragment fragment = new LoginAccountFragment();
        doFragmentTransaction(fragment, getString(R.string.fragment_login_account), false, "");
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
            Intent intent = new Intent(this, UserMainActivity.class);
            Bundle extras = new Bundle();
            extras.putString("display_button", "login");
            intent.putExtras(extras);
            startActivity(intent);
        }
    }
}
