package ca.uqac.lecitoyen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.gms.common.util.VisibleForTesting;

import ca.uqac.lecitoyen.Auth.LoginFragment;
import ca.uqac.lecitoyen.Auth.SigninFragment;


public class MainActivity extends AppCompatActivity {

    private static String LOG = "MainActivity";

    private SectionStatePagerAdapter mSectionStatePagerAdapter;
    private ViewPager mViewPager;

    private Button mLoginEmail;
    private Button mLoginAnonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

    }

    private void openLoginActivity(String signInActivity) {

        Intent signInAuth;

        switch (signInActivity) {
            case "email":
                break;
            case "anonymous":
                //signInAuth = new Intent(this, AnonymousAuthActivity.class);
                //startActivity(signInAuth);
                break;
            default:
                break;

        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.d("SetupViewPager", "START");
        SectionStatePagerAdapter adapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "LoginFragment");
        adapter.addFragment(new SigninFragment(), "SigninFragment");
        viewPager.setAdapter(adapter);
        Log.d("SetupViewPager", "END");
    }

    public void setViewPager(int fragmentNumber) {
        Log.d("setViewPager", "...");
        mViewPager.setCurrentItem(fragmentNumber);
    }

}
