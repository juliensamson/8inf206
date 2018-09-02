package ca.uqac.lecitoyen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.gms.common.util.VisibleForTesting;
import com.google.firebase.auth.FirebaseAuth;

import ca.uqac.lecitoyen.Auth.EmailPasswordActivity;
import ca.uqac.lecitoyen.Auth.LoginFragment;
import ca.uqac.lecitoyen.Auth.SigninFragment;


public class MainActivity extends AppCompatActivity {

    final private static String TAG = "MainActivity";

    private SectionStatePagerAdapter mSectionStatePagerAdapter;
    private ViewPager mViewPager;
    private Context mContext;

    private Button mLoginEmail;
    private Button mLoginAnonymous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        mSectionStatePagerAdapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.d("SetupViewPager", "START");
        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

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
