package ca.uqac.lecitoyen.User;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;


import ca.uqac.lecitoyen.R;

public class UserSettingsActivity extends UserActivity implements View.OnClickListener {

    private Toolbar mSettingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mSettingsToolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(mSettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.sign_out_button).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.sign_out_button) {
            signOutUser();
            this.finish();
        }
    }
}
