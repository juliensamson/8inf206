package ca.uqac.lecitoyen.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;

public class UserSettingsActivity extends BaseActivity implements View.OnClickListener {

    private static String TAG = "UserSettingsActivity";

    //Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG, "Created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        //  Initialize auth
        mAuth = FirebaseAuth.getInstance();

        //  Toolbar
        showToolbar(TAG,"Paramètres");

        //  Button
        findViewById(R.id.signout_account_button).setOnClickListener(this);
        findViewById(R.id.delete_account_button).setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.w(TAG, "Started");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.w(TAG, "OnPrepareMenu");
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Log.w(TAG, "OnClick");

        switch (id) {
            case R.id.signout_account_button:
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(UserSettingsActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.delete_account_button:
                deleteAccount();
                break;
            default:
                break;
        }

    }

    //  Delete Account

    private void deleteAccount() {
        Log.d(TAG, "ingreso a deleteAccount");
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG,"OK! Works fine!");
                    startActivity(new Intent(UserSettingsActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.w(TAG,"Something is wrong!");
                }
            }
        });
    }
}
