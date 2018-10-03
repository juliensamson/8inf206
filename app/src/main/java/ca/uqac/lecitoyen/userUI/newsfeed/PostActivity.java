package ca.uqac.lecitoyen.userUI.newsfeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;

public class PostActivity extends BaseActivity {

    private static String TAG = "PostActivity";

    private EditText mMessage;

    //  Firebase Database
    private DatabaseManager mDatabaseManager;

    private User userData;

    //  Firebase Authentification
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Log.d(TAG, "Created");

        //  Get user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();

        //  Initiate database
        mDatabaseManager = DatabaseManager.getInstance();



        //  View
        createToolbar("Nouveau Post", true);
        mMessage = findViewById(R.id.post_message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionMenu");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.confirm_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_confirm:
                if(!mMessage.getText().toString().isEmpty())
                    update();
                    //updateDB();
                else
                    Toast.makeText(PostActivity.this, "Un des champs est vide", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void update() {

        final DatabaseReference ref = mDatabaseManager.getReference();

        Log.d(TAG, "updateDB " + mUserId);

        showProgressDialog();
        ref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren())
                {
                    userData = dataSnapshot.child(mUserId).getValue(User.class);

                    Post post = new Post(
                            mUser.getUid(),
                            mMessage.getText().toString(),
                            System.currentTimeMillis());
                    mDatabaseManager.writePost(ref, post);
                    Toast.makeText(getApplicationContext(), "Data inserted", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
        hideProgressDialog();
        this.finish();
    }

}
