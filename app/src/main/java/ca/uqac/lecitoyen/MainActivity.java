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
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.Auth.EmailPasswordActivity;
import ca.uqac.lecitoyen.Auth.LoginFragment;
import ca.uqac.lecitoyen.Auth.SigninFragment;


public class MainActivity extends AppCompatActivity {

    final private static String TAG = "MainActivity";

    private ViewPager mViewPager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionStatePagerAdapter sectionStatePagerAdapter
                = new SectionStatePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        createViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Activity started");
    }

    private void createViewPager(ViewPager viewPager) {
        Log.d(TAG, "createViewPager");
        viewPager.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        SectionStatePagerAdapter adapter = new SectionStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "LogInFragment");
        adapter.addFragment(new SigninFragment(), "SignInFragment");
        viewPager.setAdapter(adapter);
    }

    public void setupViewPager(int fragmentNumber) {
        Log.d(TAG, "setupViewPager");
        mViewPager.setCurrentItem(fragmentNumber);
    }

}
