package ca.uqac.lecitoyen.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ca.uqac.lecitoyen.activities.EditProfilActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.dialogs.ExpandMediaDialog;
import ca.uqac.lecitoyen.dialogs.SelectImageTypeDialog;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.models.UserStorage;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.util.ImageHandler;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserEditProfileFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = UserEditProfileFragment.class.getSimpleName();
    private static final String ARG_USER = "user-auth";

    private EditProfilActivity activity;
    private iHandleFragment mHandleFragment;


    private CircleImageView mProfileImage;
    private EditText mNameField;
    private EditText mUsernameField;
    private EditText mBiographyField;
    private LinearLayout mEmailLayout;
    private EditText mEmailField;
    private EditText mPhoneField;

    //private Uri imageUri;
    public static int count = 0;
    private static final int REQUEST_CAMERA = 111;
    private static final int CAMERA_REQUEST_CODE= 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int DELETE_REQUEST_CODE = 0;

    private ArrayList<UserStorage> listProfilPicture = new ArrayList<>();
    private String pid;
    private User mUserAuth;
    private UserStorage mUserStorage;
    //private FirebaseUser mUser;
    //private String mUid;

    //  Firebase
    private ImageHandler mImageHandler;
    private DatabaseManager dbManager;

    public UserEditProfileFragment() {}

    public static UserEditProfileFragment newInstance(User user) {
        UserEditProfileFragment fragment = new UserEditProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mImageHandler = ImageHandler.getInstance();
        this.dbManager = DatabaseManager.getInstance();
        this.activity = (EditProfilActivity) getActivity();

        if(getArguments() != null) {
            mUserAuth =  getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profil, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_arrow_back_white_24dp, true, true);

        //  View
        mProfileImage = view.findViewById(R.id.edit_profil_photo);
        mNameField = view.findViewById(R.id.edit_profil_name);
        mUsernameField = view.findViewById(R.id.edit_profil_username);
        mBiographyField = view.findViewById(R.id.edit_profil_biography);
        mEmailField = view.findViewById(R.id.edit_profil_change_email);
        //mPhoneField = view.findViewById(R.id.edit_profil_phone);

        //  Button
        view.findViewById(R.id.edit_profil_change_photo).setOnClickListener(this);
        view.findViewById(R.id.edit_profil_change_email).setOnClickListener(this);
        //view.findViewById(R.id.user_setting_phone).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (iHandleFragment) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.confirm_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case android.R.id.home:
                activity.finish();
                return true;
            case R.id.menu_confirm:
                Log.d(TAG, "confrim click");
                updateDB();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.edit_profil_change_photo:
                final SelectImageTypeDialog selector = new SelectImageTypeDialog(activity);
                selector.create().OnCameraClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = mImageHandler.openCamera(activity);
                        startActivityForResult(intent, Constants.REQUEST_CAMERA_CODE);
                        selector.dismiss();
                    }
                }).OnGalleryClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = mImageHandler.openGallery(activity);
                        startActivityForResult(intent, Constants.REQUEST_GALLERY_CODE);
                        selector.dismiss();
                    }
                }).show();
                //showDialogProfilPictureChoice(view);
                break;
            case R.id.edit_profil_change_email:
                mHandleFragment.inflateFragment(R.string.fragment_edit_profil_email, "");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri imageUri = data.getData();

        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case GALLERY_REQUEST_CODE:
                    updateStorage(imageUri);
                    break;
                case CAMERA_REQUEST_CODE:    //TODO: Make this work somehow
                    updateStorage(imageUri);
                    break;
                case DELETE_REQUEST_CODE:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*

            Handle Firebase database, storage, and UI

     */

    private void updateUI() {

        try {

            if(mUserAuth == null)
                throw new NullPointerException("User is null");

            String pid = mUserAuth.getPid();
            if(pid != null && !pid.isEmpty()) {
                StorageReference profileImage = dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), pid);
                Glide.with(this).load(profileImage).into(mProfileImage);
            }

            String name = mUserAuth.getName();
            if (name != null && !name.isEmpty())
                mNameField.setText(name);

            String username = mUserAuth.getUsername();
            if (username != null && !username.isEmpty())
                mUsernameField.setText(username);

            String biography = mUserAuth.getBiography();
            if (biography != null && !biography.isEmpty())
                mBiographyField.setText(biography);

            String email = mUserAuth.getEmail();
            if (email != null && !email.isEmpty())
                mEmailField.setText(email);


        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void updateStorage(Uri imageUri) {

        showProgressDialog();

        final DatabaseReference dbProfilePicture = dbManager.getDatabaseUserProfilPicture(mUserAuth.getUid());

        //  Set all previous profile picture as false (for the current profile picture)
        dbProfilePicture.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProfilPicture.clear();

                //  Get the list of the user profil picture
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                   listProfilPicture.add(userSnapshot.getValue(UserStorage.class));
                }

                //  Set all the profil picture to false
                if (listProfilPicture.size() != 0)
                {
                    for (int i = 0; i < listProfilPicture.size(); i++) {
                        UserStorage userStorage = listProfilPicture.get(i);
                        userStorage.setProfilPicture(false);
                        dbProfilePicture.child(userStorage.getPid()).setValue(userStorage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });


        //  Create a new instance of the profile picture
        mUserStorage = new UserStorage();
        mUserStorage.setIid(dbProfilePicture.push().getKey());
        mUserStorage.setUploadTimestamp(System.currentTimeMillis());
        mUserStorage.setProfilPicture(true);
        mUserAuth.setPid(mUserStorage.getPid());


        //  Upload image to firebase storage
        StorageReference stProfileImage = dbManager.getStorageUserProfilPicture(mUserAuth.getUid(), mUserAuth.getPid());
        stProfileImage
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dbProfilePicture
                                .child(mUserStorage.getPid())
                                .setValue(mUserStorage);
                        updateDB();
                        updateUI();
                        hideProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                });
    }

    private void updateDB() {

        //TODO: Check if username already exist
        if (!validateForm()) {
            return;
        }

        Log.e(TAG, mNameField.getText().toString());
        mUserAuth.setName(mNameField.getText().toString());
        mUserAuth.setUsername(mUsernameField.getText().toString());
        mUserAuth.setBiography(mBiographyField.getText().toString());

        dbManager.updateUserdata(mUserAuth);
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = mNameField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mNameField.setError("Required.");
            valid = false;
        } else {
            mNameField.setError(null);
        }

        return valid;
    }

}
