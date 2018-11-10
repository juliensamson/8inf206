package ca.uqac.lecitoyen.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.widget.TextView;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.fragments.userUI.CityfeedFragment;
import ca.uqac.lecitoyen.fragments.userUI.MessageFragment;
import ca.uqac.lecitoyen.fragments.userUI.ForumFragment;
import ca.uqac.lecitoyen.fragments.userUI.UserProfileFragment;
import ca.uqac.lecitoyen.models.UserStorage;
import ca.uqac.lecitoyen.fragments.userUI.ProfilFragment;
import ca.uqac.lecitoyen.fragments.userUI.SearchFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.BottomNavigationFixedItemView;

//TODO: Make the RecyclerView load automatically after making a post

public class MainUserActivity extends BaseActivity implements
        iHandleFragment,
        ForumFragment.OnFragmentInteractionListener,
        UserProfileFragment.OnFragmentInteractionListener
{

    private final static String TAG = MainUserActivity.class.getSimpleName();

    public final static int AUTH_USER = 10;
    public final static int SELECT_USER = 20;

    private FragmentManager mFragmentManager;

    //private Handler mDelayedTransactionHandler = new Handler();
    //private Runnable mRunnable = this;

    private iHandleFragment mHandleFragment;
    private ForumFragment forumFragment;
    private SearchFragment searchFragment;
    private CityfeedFragment cityfeedFragment;
    private MessageFragment messageFragment;
    private UserProfileFragment userProfileFragment;

    private DatabaseManager dbManager;


    private User mUserAuth;

    public UserStorage mUserStorage;
    private ArrayList<User> mUsersList = new ArrayList<>();
    private ArrayList<Post> mPostsList = new ArrayList<>();
    private RecyclerSwipeAdapter mForumAdapter;

    private Toolbar mUserToolbar;
    private TextView mUserToolbarTitle;

    private String currentFragmentTag;
    private boolean isFragmentInitialize = false;

    private Context mContext;
    private MainUserActivity mUserActivity;

    //Firebase
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;

    private BottomNavigationFixedItemView bottomNavigationFixedItemView;
    private BottomNavigation mBottomNavigation;
    private BottomNavigation.OnMenuItemSelectionListener mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        setContentView(R.layout.activity_user);

        this.mContext = this;
        this.mUserActivity = this;

        //  Database
        dbManager = DatabaseManager.getInstance();
        mFragmentManager = getSupportFragmentManager();
        fbAuth = FirebaseAuth.getInstance();

        //  Initialize auth

        //  Initialize fragment
        searchFragment = new SearchFragment();
        cityfeedFragment = new CityfeedFragment();
        messageFragment = new MessageFragment();
        //doFragmentTransaction(forumFragment, getString(R.string.fragment_forum), false, "");

        //  Bottom navigation
        mBottomNavigation = findViewById(R.id.navigation);
        mBottomNavigation.setDefaultSelectedIndex(0);
        mBottomNavigation.setDefaultTypeface(Typeface.DEFAULT_BOLD);
        mBottomNavigation.setOnMenuItemClickListener(loadBottonNavigation());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(fbAuth != null) {
            fbUser = fbAuth.getCurrentUser();
            loadFirebaseValueListener();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //moveTaskToBack(true);
    }

    private void updateUI() {

        loadInitialFragment();

    }

    private void loadFirebaseValueListener() {
        fbAuth = FirebaseAuth.getInstance();
        fbUser = fbAuth.getCurrentUser();

        Log.d(TAG, fbUser.getUid());

        dbManager.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /*      Get Userdata      */

                mUserAuth = dataSnapshot
                        .child("users")
                        .child(fbUser.getUid())
                        .getValue(User.class);

                /*      Get User Profile Image     */

                //loadUriProfileImage();

                /*      Get the list of users      */

                loadUsersList(dataSnapshot);

                /*      Get the list of posts      */

                loadPostsList(dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadFileProfileImage() {
        StorageReference stProfileImage = dbManager.getStorageUserProfilPicture(
                mUserAuth.getUid(),
                mUserAuth.getPid());

        try {

            File localFile = File.createTempFile("user-profile-image", "jpg");

            stProfileImage.getFile(localFile).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getTotalByteCount();
                }
            }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            });
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }


        /*stProfileImage.get.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                task
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                uri.
            }
        });*/
    }

    private void loadUsersList(DataSnapshot dataSnapshot) {
        Query dbPosts = dataSnapshot.getRef().child("users");
        dbPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mUsersList.add(postSnapshot.getValue(User.class));
                }
                searchFragment = SearchFragment.newInstance(mUserAuth, mUsersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void loadPostsList(DataSnapshot dataSnapshot) {
        Query dbPosts = dataSnapshot.getRef().child("posts").orderByChild("dateInverse");
        dbPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mPostsList.add(postSnapshot.getValue(Post.class));
                }

                mForumAdapter = new SwipePostAdapter(mUserActivity, mUserAuth, mPostsList);

                updateUI();

                hideProgressDialog();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });
    }

    private void loadInitialFragment() {
        forumFragment = ForumFragment.newInstance(mUserAuth, mPostsList);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.user_container, forumFragment);
        transaction.commit();
    }


    public void doUserProfileTransaction(Fragment fragment, int userType) {

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        switch (userType) {
            case AUTH_USER:
                transaction.setCustomAnimations(
                        R.anim.enter_from_top,
                        R.anim.exit_from_top,
                        R.anim.enter_from_bottom,
                        R.anim.exit_from_bottom);
                break;
            case SELECT_USER:
                transaction.setCustomAnimations(
                        R.anim.enter_from_left,
                        R.anim.exit_from_left,
                        R.anim.enter_from_right,
                        R.anim.exit_from_right);
                break;
        }
        transaction.addToBackStack(null);
        transaction.replace(R.id.user_container, fragment, fragment.getTag());
        transaction.commit();
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack, String message) {
        Log.d(TAG, "doFragmentTransaction");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_from_right);

        transaction.replace(R.id.user_container, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();

        currentFragmentTag = tag;
    }

    @Override
    public void setToolbarTitle(String fragmentTag) {
        mUserToolbarTitle.setText(fragmentTag);
    }

    @Override
    public void inflateFragment(int fragmentTagId, String message) {

        switch (fragmentTagId)
        {
            case R.string.fragment_forum:
                doFragmentTransaction(forumFragment, getString(R.string.fragment_forum), false, "");
                //doFragmentTransaction(forumFragment, getString(R.string.fragment_forum));
                break;
            case R.string.fragment_search:
                doFragmentTransaction(searchFragment, getString(R.string.fragment_search), false, "");
                //doFragmentTransaction(searchFragment, getString(R.string.fragment_search));
                break;
            case R.string.fragment_cityfeed:
                doFragmentTransaction(cityfeedFragment, getString(R.string.fragment_cityfeed), false, "");
                //doFragmentTransaction(cityfeedFragment, getString(R.string.fragment_cityfeed));
                break;
            case R.string.fragment_messages:
                doFragmentTransaction(messageFragment, getString(R.string.fragment_messages), false, "");
                //doFragmentTransaction(messageFragment, getString(R.string.fragment_messages));
                break;
            default:
                break;
        }
    }

    private BottomNavigation.OnMenuItemSelectionListener loadBottonNavigation() {
        return new BottomNavigation.OnMenuItemSelectionListener() {

            @Override
            public void onMenuItemSelect(int navId, int listId, boolean b) {
                Log.d(TAG, "OnNavigationItem");
                switch (navId) {
                    case R.id.navigation_newsfeed:
                        inflateFragment(R.string.fragment_forum, "");
                        break;
                    case R.id.navigation_search:
                        inflateFragment(R.string.fragment_search, "");
                        break;
                    case R.id.navigation_cityfeed:
                        inflateFragment(R.string.fragment_cityfeed, "");
                        break;
                    case R.id.navigation_messages:
                        inflateFragment(R.string.fragment_messages, "");
                        break;
                }
            }

            @Override
            public void onMenuItemReselect(int navId, int listId, boolean b) {
                //  Update feed
                //  Or goe to the top
            }

        };
    }

    public void setBottomNavigationItem(int pos) {
        mBottomNavigation.setSelectedIndex(pos, true);
    }

    //  TODO: - Make Key,Value a list, map, etc. in order to add more "extras" to the Bundle
    //        - Allow to sent the class User to get the info directly. and not call mAuth on setting. (make it faster)
    public void startActivityWithBundle(Class activity, String key, String value) {

        //  Create Bundle sent to the next activity
        Bundle extras = new Bundle();
        extras.putString(key, value);

        //  Add extras to the intent and start
        Intent intent = new Intent(this, activity);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     *
     *      Getter
     *
     */

    public ArrayList<Post> getPostsList() {
        return this.mPostsList;
    }

    public RecyclerSwipeAdapter getForumAdapter() {
        return this.mForumAdapter;
    }

    public FirebaseAuth getUserAuth() {
        return this.fbAuth;
    }

    public UserStorage getUserStorage() {
        return this.mUserStorage;
    }

    @Override
    public void onFragmentInteraction(String something) {
        onBackPressed();
    }
}
