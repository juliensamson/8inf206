package ca.uqac.lecitoyen.userUI.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapter.ProfilAdapter;
import ca.uqac.lecitoyen.adapter.PublicationAdapter;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.userUI.settings.UserSettingsActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ProfilFragment";
    private UserMainActivity userMainActivity;
    private iHandleFragment mHandleFragment;

    //  Views
    private NestedScrollView mNestedScrollView;
    private CircleImageView vProfilPicture;
    private TextView vProfilName;
    private TextView vProfilUsername;
    private TextView vProfilBiography;
    private TextView vProfilPublicationCount;
    private TextView vProfilFollowingCount;
    private TextView vProfilFollowersCount;
    private RecyclerView vProfilRecyclerView;

    private RecyclerView.Adapter vAdapter;
    private RecyclerView.LayoutManager vLayoutManager;

    //  Buttons
    TextView mEditProfile;

    //  Data structure
    private String pid;
    private User mUserData;
    private UserStorage mUserStorage;
    private ArrayList<Post> listUserPost = new ArrayList<>();
    private ArrayList<User> listUserData = new ArrayList<>();
    private ArrayList<UserStorage> listUserProfilPicture = new ArrayList<>();


    //  Firebase
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseManager dbManager;
    private DatabaseReference dbUserData;
    private DatabaseReference dbUserProfilPicture;
    private DatabaseReference dbUserPost;
    private StorageReference  stUserProfilPicture;

    public ProfilFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userMainActivity = (UserMainActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.fbAuth = userMainActivity.getUserAuth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        //  Toolbar
        setFragmentToolbar(view, userMainActivity, R.id.toolbar_profil, getTag(), true);

        //  View
        mNestedScrollView = view.findViewById(R.id.profil_nested_scroll_view);
        //  Put the view to the top
        mNestedScrollView.getParent().requestChildFocus(mNestedScrollView, mNestedScrollView);

        vProfilPicture = view.findViewById(R.id.profil_picture);
        vProfilName = view.findViewById(R.id.profil_name);
        vProfilUsername = view.findViewById(R.id.profil_username);
        vProfilBiography = view.findViewById(R.id.profil_biography);
        vProfilPublicationCount = view.findViewById(R.id.profil_publication_count);
        vProfilFollowingCount = view.findViewById(R.id.profil_following_count);
        vProfilFollowersCount = view.findViewById(R.id.profil_followers_count);
        vProfilRecyclerView = view.findViewById(R.id.profil_publication_recycler_view);
        //  Make the scroll smooth
        vProfilRecyclerView.setNestedScrollingEnabled(false);



        //  Button
        view.findViewById(R.id.button_edit_profile).setOnClickListener(this);

        //  Set recycler view
        vLayoutManager = new LinearLayoutManager(userMainActivity);
        vProfilRecyclerView.setLayoutManager(vLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(fbAuth != null) {

            fbUser = fbAuth.getCurrentUser();

            if(fbUser != null) {

                String uid = fbUser.getUid();

                //  Get database & storage reference
                dbUserData = dbManager.getDatabaseUser(uid);
                dbUserProfilPicture = dbManager.getDatabaseUserProfilPicture(uid);
                dbUserPost = dbManager.getDatabaseUserPost(uid);
                stUserProfilPicture = dbManager.getStorageUserProfilPicture(uid);

                updateUI();
            }
        } else {
            Log.e(TAG, "auth is null");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (iHandleFragment) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG, "item selected");
        switch (item.getItemId())
        {
            case R.id.menu_setting:
                Log.w(TAG, "menu_setting clicked");
                userMainActivity.startActivityWithBundle(UserSettingsActivity.class, "userid", fbUser.getUid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_edit_profile:
                Log.d(TAG, "edit_post clicked");
                startActivity(new Intent(userMainActivity.getApplicationContext(), EditProfileActivity.class ));
                userMainActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                //mHandleFragment.inflateFragment(R.string.fragment_edit_profil, "");
                break;
        }
    }

    /*

            Handle Firebase database, storage, and UI

     */

    private void updateUI() {
        //  Load database user data & update UI
        dbUserData.addListenerForSingleValueEvent(loadUserData());
        //  Load database user post & update UI
        dbUserPost.addListenerForSingleValueEvent(loadUserPost());
    }

    private ValueEventListener loadUserData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "loadUserData");
                mUserData = dataSnapshot.getValue(User.class);

                if(mUserData != null)
                {
                    if (mUserData.getName() != null && !mUserData.getName().isEmpty())
                        vProfilName.setText(mUserData.getName());
                    if (mUserData.getUsername() != null && !mUserData.getUsername().isEmpty())
                        vProfilUsername.setText(mUserData.getUsername());
                    if (mUserData.getBiography() != null && !mUserData.getBiography().isEmpty())
                        vProfilBiography.setText(mUserData.getBiography());
                    if(mUserData.getPid() != null)
                        Glide.with(userMainActivity).load(stUserProfilPicture.child(mUserData.getPid())).into(vProfilPicture);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    private ValueEventListener loadUserPost() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "loadUserPost");
                listUserPost.clear();

                for(DataSnapshot userPost: dataSnapshot.getChildren()) {
                    listUserPost.add(userPost.getValue(Post.class));
                }

                if(listUserPost != null) {
                    vProfilPublicationCount.setText((Integer.toString(listUserPost.size())));
                    vAdapter = new PublicationAdapter(userMainActivity, listUserPost);
                    vProfilRecyclerView.setAdapter(vAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

}
