package ca.uqac.lecitoyen.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ca.uqac.lecitoyen.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ExpandPostActivity extends BaseActivity {

    private CircleImageView mProfilImageView;
    private TextView mNameTextView;
    private TextView mUsernameTextView;
    private TextView mMessageTextView;
    private TextView mDateTextView;
    private TextView mIsModifyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_post);

        //  Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_simple);
        TextView tbTitle = findViewById(R.id.toolbar_simple_title);
        tbTitle.setText("Publication");
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_primary_24dp);
        }

        //  Views
        mProfilImageView = findViewById(R.id.expand_post_profil_picture);
        mNameTextView = findViewById(R.id.expand_post_name);
        mUsernameTextView = findViewById(R.id.expand_post_username);
        mMessageTextView = findViewById(R.id.expand_post_message);
        mDateTextView = findViewById(R.id.expand_post_publish_time);
        mIsModifyTextView = findViewById(R.id.expand_post_is_modify);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {

    }
}
