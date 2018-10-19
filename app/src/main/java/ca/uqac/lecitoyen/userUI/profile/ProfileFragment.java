package ca.uqac.lecitoyen.userUI.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import ca.uqac.lecitoyen.adapter.HomeAdapter;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.userUI.settings.UserSettingsActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ProfilFragment";
    private UserMainActivity activity;
    private iHandleFragment mHandleFragment;

    //  Views
    private CircleImageView vProfilPicture;
    private TextView vProfilName;
    private TextView vProfilUsername;
    private TextView vProfilBiography;
    private TextView vProfilPublicationCount;
    private TextView vProfilFollowingCount;
    private TextView vProfilFollowersCount;
    private RecyclerView vPublicationRecyclerView;

    private RecyclerView.Adapter vAdapter;
    private RecyclerView.LayoutManager vLayoutManager;

    //  Buttons
    TextView mEditProfile;

    //  Data structure
    private User mUserData;
    private ArrayList<Post> listUserPost = new ArrayList<>();
    private ArrayList<User> listUserData = new ArrayList<>();
    private ArrayList<UserStorage> listUserProfilPicture = new ArrayList<>();


    //  Firebase
    private FirebaseUser fbUser;
    private DatabaseManager dbManager;
    private DatabaseReference dbUserData;
    private DatabaseReference dbUserProfilPicture;
    private DatabaseReference dbUserPost;
    private StorageReference  stUserProfilPicture;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (UserMainActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());

        //  View
        vProfilPicture = view.findViewById(R.id.profil_picture);
        vProfilName = view.findViewById(R.id.profil_name);
        //vProfilUsername = view.findViewById(R.id.profil_username);
        vProfilBiography = view.findViewById(R.id.profil_biography);
        vProfilPublicationCount = view.findViewById(R.id.profil_publication_count);
        vProfilFollowingCount = view.findViewById(R.id.profil_following_count);
        vProfilFollowersCount = view.findViewById(R.id.profil_followers_count);
        vPublicationRecyclerView = view.findViewById(R.id.profil_publication_recycler_view);;

        //  Button
        view.findViewById(R.id.button_edit_profile).setOnClickListener(this);

        //  Set recycler view
        vLayoutManager = new LinearLayoutManager(activity);
        vPublicationRecyclerView.setLayoutManager(vLayoutManager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(activity.getUserAuth() != null)
        {
            fbUser = activity.getUserAuth().getCurrentUser();
            if(fbUser != null)
            {
                //  Get User ID
                String uid = fbUser.getUid();

                //  Get database reference
                dbUserData = dbManager.getDatabaseUser(uid);
                dbUserProfilPicture = dbManager.getDatabaseUserProfilPicture(uid);
                dbUserPost = dbManager.getDatabaseUserPost(uid);

                //  Get storage reference
                stUserProfilPicture = dbManager.getStorageUserProfilPicture(uid);

                //  Update UI
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
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.button_edit_profile:
                Log.d(TAG, "edit_post clicked");
                startActivity(new Intent(activity.getApplicationContext(), EditProfileActivity.class ));
                activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                //mHandleFragment.inflateFragment(R.string.fragment_edit_profile, "");
                break;
        }
    }

    private void updateUI() {
        //  Get User-data
        dbUserData.addListenerForSingleValueEvent(loadUserData());

        //  Get User-Profil
        if(dbUserProfilPicture != null) {
            dbUserProfilPicture.addListenerForSingleValueEvent(loadUserProfilPicture());
        }

        //  Get User-Post
        dbUserPost.addListenerForSingleValueEvent(loadUserPost());
    }

    private ValueEventListener loadUserData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUserData = dataSnapshot.getValue(User.class);

                if(mUserData != null) {
                    if (mUserData.getName() != null && !mUserData.getName().isEmpty())
                        vProfilName.setText(mUserData.getName());
                    //if (mUserData.getUsername() != null && !mUserData.getUsername().isEmpty())
                    //    mUsernameField.setText(mUserData.getUsername());
                    if (mUserData.getBiography() != null && !mUserData.getBiography().isEmpty())
                        vProfilBiography.setText(mUserData.getBiography());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    private ValueEventListener loadUserProfilPicture() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listUserProfilPicture.clear();

                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    listUserProfilPicture.add(userSnapshot.getValue(UserStorage.class));
                }
                UserStorage userStorage = new UserStorage();
                for(int i = 0; i < listUserProfilPicture.size(); i++)
                {
                    if(listUserProfilPicture.get(i).isProfilPicture()) {
                        userStorage = listUserProfilPicture.get(i);
                        break;
                    }
                }
                Glide.with(activity).load(stUserProfilPicture.child(userStorage.getPid())).into(vProfilPicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    //TODO: display post made by the user
    private ValueEventListener loadUserPost() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listUserPost.clear();
                listUserData.clear();

                for(DataSnapshot userPost: dataSnapshot.getChildren()) {
                    listUserPost.add(userPost.getValue(Post.class));
                    listUserData.add(mUserData);
                }
                if(listUserPost != null) {
                    vProfilPublicationCount.setText((Integer.toString(listUserPost.size())));
                }
                vAdapter = new HomeAdapter(activity, fbUser, listUserPost, listUserData);
                vPublicationRecyclerView.setAdapter(vAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

}
