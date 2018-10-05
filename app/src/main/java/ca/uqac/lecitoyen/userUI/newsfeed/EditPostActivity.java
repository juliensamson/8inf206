package ca.uqac.lecitoyen.userUI.newsfeed;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostModification;
import ca.uqac.lecitoyen.database.User;

public class EditPostActivity extends BaseActivity {

    private static final String TAG = "EditPostActivity" ;

    private Post mCurrentPost;
    private String mPostid;

    private EditText mMessage;
    private Button mDeletePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        //  Toolbar
        createToolbar("Edit Post", true);

        //  View
        mMessage = findViewById(R.id.edit_post);

        //  Get intent
        Intent intent = getIntent();
        if(intent != null) {
            mPostid = intent.getStringExtra("postid");
            DatabaseManager.getInstance().getReference()
                    .child("posts")
                    .addListenerForSingleValueEvent(getPostData());
        }
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
                    updateDB();
                else
                    Toast.makeText(EditPostActivity.this, "Un des champs est vide", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ValueEventListener getPostData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    mCurrentPost = postSnapshot.getValue(Post.class);

                    String currentPostId = postSnapshot.getValue(Post.class).getPostid();
                    if(currentPostId.equals(mPostid)) {
                        Log.w(TAG, "Post id: " + currentPostId + " " + mPostid);
                        mMessage.setText(mCurrentPost.getPost());
                        break;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }


    // TODO: Si le message n'est pas changé, assuré qu'il ne fasse pas de mise à jour (éviter de dupliqué donné)
    private void updateDB() {

        final DatabaseReference ref = DatabaseManager.getInstance().getReference();


        showProgressDialog();
        ref.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren())
                {
                    mCurrentPost.setPost(mMessage.getText().toString());

                    List modifications = mCurrentPost.getModifications();

                    PostModification postModification = new PostModification(
                            modifications.size(),
                            mMessage.getText().toString(),
                            System.currentTimeMillis()
                    );
                    modifications.add(postModification);
                    mCurrentPost.setModifications(modifications);

                    DatabaseManager.getInstance().getReference()
                            .child("posts")
                            .child(mCurrentPost.getPostid())
                            .setValue(mCurrentPost);
                    DatabaseManager.getInstance().getReference()
                            .child("user-post")
                            .child(mCurrentPost.getUid())
                            .child(mCurrentPost.getPostid())
                            .setValue(mCurrentPost);
                    Toast.makeText(getApplicationContext(), "Data modified", Toast.LENGTH_SHORT).show();
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
