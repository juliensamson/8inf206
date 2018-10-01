package ca.uqac.lecitoyen;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.Auth.ForgotAccountFragment;
import ca.uqac.lecitoyen.Auth.LoginAccountFragment;
import ca.uqac.lecitoyen.Auth.MainAuthFragment;
import ca.uqac.lecitoyen.Auth.CreateAccountFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.User.UserMainActivity;


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

        mToolbarTitle.setText(getString(R.string.fragment_main_auth));
        setSupportActionBar(mToolbar);

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
            MainAuthFragment fragment = new MainAuthFragment();
            doFragmentTransaction(fragment, getString(R.string.fragment_main_auth), false, "");
        }

    }


    @Override
    public void setToolbarTitle(String fragmentTag) {
        mToolbarTitle.setText(fragmentTag);
    }

    @Override
    public void inflateFragment(int fragmentTagId, String message) {

        Fragment fragment;

        switch (fragmentTagId)
        {
            case R.string.fragment_main_auth:
                fragment = new MainAuthFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_main_auth), false, "");
                break;
            case R.string.fragment_login_account:
                fragment = new LoginAccountFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_login_account), true, "");
                break;
            case R.string.fragment_create_account:
                fragment = new CreateAccountFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_create_account), true, "");
                break;
            case R.string.fragment_forgot_account:
                fragment = new ForgotAccountFragment();
                doFragmentTransaction(fragment, getString(R.string.fragment_forgot_account), true, "");
                break;
            default:
                break;
        }
    }

    //
    // Fragment Transaction
    //

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
