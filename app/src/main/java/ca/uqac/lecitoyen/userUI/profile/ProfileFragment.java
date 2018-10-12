package ca.uqac.lecitoyen.userUI.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.userUI.settings.UserSettingsActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ProfilFragment";
    TextView mEditProfile;

    private CircleImageView mProfilImage;


    private UserMainActivity activity;
    private iHandleFragment mHandleFragment;

    private DatabaseReference mUserDataRef;
    private StorageReference mUserProfilImageRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (UserMainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());

        //  View
        mProfilImage = view.findViewById(R.id.profil_photo);

        //  Button
        view.findViewById(R.id.button_edit_profile).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(activity.getUserAuth() != null)
        {
            FirebaseUser user = activity.getUserAuth().getCurrentUser();
            if(user != null)
            {
                String uid = user.getUid();

                if(activity.getDatabaseRef() != null)
                    mUserDataRef = activity.getDatabaseRef()
                            .child("users")
                            .child(uid);
                if(activity.getStorageRef() != null) {
                    mUserProfilImageRef = activity.getStorageRef()
                            .child("users")
                            .child(uid)
                            .child("profil-image");
                    Glide.with(this).load(mUserProfilImageRef).into(mProfilImage);
                }
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
}
