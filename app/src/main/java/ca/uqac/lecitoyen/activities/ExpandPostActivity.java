package ca.uqac.lecitoyen.activities;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.buttons.RepostButton;
import ca.uqac.lecitoyen.buttons.UpvoteButton;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.util.MultimediaView;
import ca.uqac.lecitoyen.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;

public class ExpandPostActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ExpandPostActivity.class.getSimpleName();

    private CircleImageView mProfilImageView;
    private TextView mNameTextView;
    private TextView mUsernameTextView;
    private TextView mMessageTextView;
    private TextView mDateTextView;
    private TextView mIsModifyTextView;
    private MultimediaView mMultimediaView;

    private UpvoteButton mUpvoteButton;
    private RepostButton mRepostButton;

    private Post mPost;

    private String mPostId;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_post);

        mContext = this;

        //  Get bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mPostId = bundle.getString("postid");
            Log.d(TAG, "post id " + mPostId);
        }else
            Log.e(TAG, "bundle is null");

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
        mMultimediaView = findViewById(R.id.expand_post_multimedia);

        //  Buttons
        mUpvoteButton = findViewById(R.id.post_upvote_button);
        mRepostButton = findViewById(R.id.post_repost_button);
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

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {

            FirebaseUser fbUser = auth.getCurrentUser();

            updateUI(fbUser);

        } else
            Log.e(TAG, "FirebaseUser is null");
    }

    private void updateUI(FirebaseUser user) {

        DatabaseManager dbManager = DatabaseManager.getInstance();

        if(mPostId != null) {
            dbManager.getDatabasePost(mPostId).addListenerForSingleValueEvent(initExpandPost(dbManager));
        } else {
            Log.e(TAG, "post is null");
        }

    }

    private ValueEventListener initExpandPost(final DatabaseManager dbManager) {
        showProgressDialog();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPost = dataSnapshot.getValue(Post.class);
                Post post = mPost;
                if(post != null) {

                    User user = post.getUser();

                    StorageReference stProfilPicture = dbManager.getStorageUserProfilPicture(user.getUid());
                    StorageReference stPost = dbManager.getStoragePost(mPostId);

                    if(user.getPid() != null && !user.getPid().isEmpty())                                   //User's profil picture
                        Glide.with(mContext).load(stProfilPicture.child(user.getPid())).into(mProfilImageView);

                    if(user.getName() != null && !user.getName().isEmpty())                                 //User's name
                        mNameTextView.setText(user.getName());

                    if(user.getUsername() != null && !user.getUsername().isEmpty())                         //User's username
                        mUsernameTextView.setText(user.getUsername());

                    if(post.getMessage() != null && !post.getMessage().isEmpty())                                 //Post message
                        mMessageTextView.setText(post.getMessage());

                    if(post.getDate() != 0)                                                                 //Time of publication
                        mDateTextView.setText(Util.setDisplayTime(mContext, post.getDate()));

                    if(post.getHistories().size() > 1)                                                  //Show if post modified
                        mIsModifyTextView.setVisibility(View.VISIBLE);

                    if(post.getImages() != null) {
                        if (!post.getImages().get(0).getImageId().isEmpty()){
                            mMultimediaView.loadImages(stPost
                                    .child(post.getImages().get(0).getImageId()));
                        }
                    }

                    if(post.getAudio() != null) {
                        if (!post.getAudio().isEmpty()) {
                            Log.d(TAG, post.getAudio());
                            mMultimediaView.loadAudio(post.getAudio());
                        }
                    }

                    mUpvoteButton.setButtonCount(post.getUpvoteCount());

                    mRepostButton.setButtonCount(post.getRepostCount());

                }

                hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                hideProgressDialog();
            }
        };
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.expand_post_multimedia:
                if(mPost != null)
                    expandMultimedia(mPost);
                break;
        }

    }

    private void expandMultimedia(Post post) {

        Bundle bundle = new Bundle();
        bundle.putString("postid", post.getPostid());

        if(mMultimediaView.isPhoto()) {

            bundle.putInt("code", Constants.EXPAND_PHOTO);

            Intent intent = new Intent(mContext, ExpandPostMediaActivity.class);
            intent.putExtras(bundle);

            mContext.startActivity(intent);
        }
        if(mMultimediaView.isAudio()) {

            bundle.putInt("code", Constants.EXPAND_AUDIO);

            Intent intent = new Intent(mContext, ExpandPostMediaActivity.class);
            intent.putExtras(bundle);

            mContext.startActivity(intent);
        }
        if(mMultimediaView.isLink()) {

        }

    }
}
