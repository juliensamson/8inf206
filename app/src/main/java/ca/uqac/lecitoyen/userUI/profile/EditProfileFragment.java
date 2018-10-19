package ca.uqac.lecitoyen.userUI.profile;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideModule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapter.HomeAdapter;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.userUI.newsfeed.EditPostActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "EditProfileFragment" ;
    private EditProfileActivity activity;
    private iHandleFragment mHandleFragment;


    private CircleImageView mProfilImage;
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
    private User mUserData;
    private UserStorage mUserStorage;
    //private FirebaseUser mUser;
    //private String mUid;

    //  Firebase
    private DatabaseManager dbManager;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseReference dbUserData;
    private DatabaseReference dbUserProfilPicture;
    private StorageReference stUserProfilPicture;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (EditProfileActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_arrow_back_white_24dp, true, true);

        //  View
        mProfilImage = view.findViewById(R.id.edit_profil_photo);
        mNameField = view.findViewById(R.id.edit_profil_name);
        mUsernameField = view.findViewById(R.id.edit_profil_username);
        mBiographyField = view.findViewById(R.id.edit_profil_biography);
        mEmailField = view.findViewById(R.id.edit_profil_email);
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

        fbAuth = activity.getUserAuth();
        if(fbAuth != null)
        {
            fbUser = activity.getUserAuth().getCurrentUser();
            if(fbUser != null)
            {
                String uid = fbUser.getUid();

                //  Get database reference
                dbUserData = dbManager.getDatabaseUser(uid);
                dbUserProfilPicture = dbManager.getDatabaseUserProfilPicture(uid);
                //  Get storage reference
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
                showDialogProfilPictureChoice(view);
                break;
        }
    }

    private void showDialogProfilPictureChoice(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final int TAKE_PICTURE = 0, IMPORT_PICTURE = 1, DELETE_PICTURE = 2;

        builder.setTitle(R.string.edit_profil_title)
                .setItems(R.array.edit_profil_change_photo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i)
                    {
                        case TAKE_PICTURE:     //  Take a picture
                            takeProfilPicture();
                            break;
                        case IMPORT_PICTURE:     //  Import picture
                            importProfilPicture();
                            break;
                        case DELETE_PICTURE:     //  Delete picture
                            deleteProfilPicture();
                            break;
                        default:
                            break;
                    }
                }
            });
        builder.show();
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
                    checkInternalStorage();
                    updateStorage(imageUri);
                    break;
                case DELETE_REQUEST_CODE:
                    break;
                default:
                    break;
            }
        }
    }

    private void updateUI() {

        //  Load user-data
        dbUserData.addListenerForSingleValueEvent(loadUserData());

        if(dbUserProfilPicture != null) {
            //  Load user-picture
            dbUserProfilPicture.addListenerForSingleValueEvent(loadUserProfilPicture());
        }
    }

    private void updateDB() {

        //TODO: Check if username already exist
        if (!validateForm()) {
            return;
        }

        mUserData.setName(mNameField.getText().toString());
        mUserData.setUsername(mUsernameField.getText().toString());
        mUserData.setBiography(mBiographyField.getText().toString());

        showProgressDialog();

        dbUserData.setValue(mUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, "Information enregistré", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
        });
    }

    private void updateStorage(Uri imageUri) {

        showProgressDialog();

        dbUserProfilPicture.addListenerForSingleValueEvent(checkMainUserProfilPicture());

        final UserStorage userStorage = new UserStorage(
                dbUserProfilPicture.push().getKey(),
                DatabaseManager.CHILD_PROFIL_PICTURE,
                System.currentTimeMillis()
        );
        userStorage.setProfilPicture(true);

        stUserProfilPicture.child(userStorage.getPid()).putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Byte transferred: " + taskSnapshot.getBytesTransferred());
                        //TODO: add byte transfer to ProgressDialog
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dbUserProfilPicture.child(userStorage.getPid()).setValue(userStorage);
                        updateUI();
                        hideProgressDialog();
                        Toast.makeText(activity, "Image telecharger", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
        });
    }

    private void importProfilPicture() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        if (openGalleryIntent.resolveActivity(activity.getPackageManager()) != null) {
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    private void takeProfilPicture() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void checkInternalStorage() {
        count++;
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/lecitoyen-images/";
        String file = dir + mUserData.getUsername() + count + ".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        }
        catch (IOException e)
        {
        }
        //imageUri = Uri.fromFile(newfile);
        //data.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
    }

    private void deleteProfilPicture() {
        //TODO: Delete list of profil picture;
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

    /*

            load data from firebase

     */

    private ValueEventListener loadUserData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUserData = dataSnapshot.getValue(User.class);

                if(mUserData != null) {
                    if (mUserData.getName() != null && !mUserData.getName().isEmpty())
                        mNameField.setText(mUserData.getName());
                    if (mUserData.getUsername() != null && !mUserData.getUsername().isEmpty())
                        mUsernameField.setText(mUserData.getUsername());
                    if (mUserData.getBiography() != null && !mUserData.getBiography().isEmpty())
                        mBiographyField.setText(mUserData.getBiography());
                    if (mUserData.getEmail() != null && !mUserData.getEmail().isEmpty())
                        mEmailField.setText(mUserData.getEmail());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    private ValueEventListener checkMainUserProfilPicture() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listProfilPicture.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    listProfilPicture.add(userSnapshot.getValue(UserStorage.class));
                }
                int size = listProfilPicture.size();
                for (int i = 0; i < listProfilPicture.size(); i++) {
                    UserStorage userStorage = listProfilPicture.get(i);
                    userStorage.setProfilPicture(false);
                    dbUserProfilPicture
                            .child(userStorage.getPid())
                            .setValue(userStorage);
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

                listProfilPicture.clear();

                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    listProfilPicture.add(userSnapshot.getValue(UserStorage.class));
                }
                UserStorage userStorage = new UserStorage();
                for(int i = 0; i < listProfilPicture.size(); i++)
                {
                    if(listProfilPicture.get(i).isProfilPicture()) {
                        userStorage = listProfilPicture.get(i);
                        break;
                    }
                }
                Glide.with(activity).load(stUserProfilPicture.child(userStorage.getPid())).into(mProfilImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
