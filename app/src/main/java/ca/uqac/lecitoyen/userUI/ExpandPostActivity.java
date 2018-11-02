package ca.uqac.lecitoyen.userUI;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.userUI.settings.UserSettingsFragment;
import ca.uqac.lecitoyen.utility.AudioPlayer;
import nl.changer.audiowife.AudioWife;

public class ExpandPostActivity extends BaseActivity {

    private static final String TAG = "ExpandPostActivity";

    private ImageView mPictureView;
    private FrameLayout mPlayerView;

    private DatabaseManager dbManager;
    private DatabaseReference dbPost;
    private StorageReference stPost;
    private DatabaseReference dbUsersData;
    private DatabaseReference dbUserProfilPicture;
    private DatabaseReference dbPosts;

    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;

    String mPostId;
    int codePrev;

    AudioPlayer mAudioplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_post);

        //  Get bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            mPostId = bundle.getString("SwipePostAdapter");
            codePrev = bundle.getInt("SwipePostAdapter");
            Log.d(TAG, "post id ");
        }else
            Log.e(TAG, "bundle is null");

        dbManager = DatabaseManager.getInstance();

        mPictureView = findViewById(R.id.expand_post_picture);
        mPlayerView  = findViewById(R.id.expand_post_mediaplayer);


        Toolbar toolbar = findViewById(R.id.expand_post_toolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } catch (NullPointerException npe) {

            Log.e(TAG, npe.getMessage());

        }
        //mAudioplayer = new AudioPlayer(this);
        //mAudioplayer = findViewById(R.id.expand_post_audioplayer);

        //mAudioplayer.create(this).load();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mPostId != null) {

            dbPost = dbManager.getDatabasePost(mPostId);
            stPost = dbManager.getStoragePost(mPostId);
            updateUI();

        } else {

            Log.e(TAG, "Post id is null");

        }

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

    private void updateUI() {
        dbPost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Post post = dataSnapshot.getValue(Post.class);

                if(post != null) {

                    //  Load image if there is image

                    switch (codePrev)
                    {
                        case 101:
                            setAudioPlayer(post);
                            break;
                        case 100:
                            Glide.with(getBaseContext())
                                    .load(stPost.child(post.getImages().get(0).getImageId()))
                                    .into(mPictureView);
                            break;
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setAudioPlayer(Post post) {
        if (post.getAudio() != null && !post.getAudio().isEmpty()) {

            // Load audio file
            stPost.child(post.getAudio()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    AudioWife.getInstance()
                            .init(getBaseContext(), uri)
                            .useDefaultUi(mPlayerView, getLayoutInflater());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            });

        }
    }
}
