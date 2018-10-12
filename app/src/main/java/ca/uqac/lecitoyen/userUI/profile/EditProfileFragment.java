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

    //private FrameLayout mVerifyAccountLayout;
    //private ImageView mCloseWarning;
    //private Button mVerifyAccount;
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

    private User mUserData;
    //private FirebaseUser mUser;
    //private String mUid;
    private DatabaseReference mUserDataRef;
    private StorageReference mUserProfilImageRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (EditProfileActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_arrow_back_white_24dp, true, true);

        //  Verify Warning
        //mVerifyAccountLayout = view.findViewById(R.id.user_setting_verify_user_layout);
        //view.findViewById(R.id.user_setting_close_warning).setOnClickListener(this);
        //view.findViewById(R.id.user_setting_verify_user).setOnClickListener(this);

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
                if(activity.getStorageRef() != null)
                    mUserProfilImageRef = activity.getStorageRef()
                            .child("users")
                            .child(uid)
                            .child("profil-image");
                updateUI(user);
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
                //activity.showProgressDialog();
                //if(!mUsernameField.getText().toString().equals(""))
                //    updateDB(mUser);
                //Log.w(TAG, "Information saved");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.edit_profil_change_photo:
                showDialogProfilImageChoice(view);
                break;
        }
    }

    private void showDialogProfilImageChoice(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.edit_profil_title)
                .setItems(R.array.edit_profil_change_photo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    switch (i)
                    {
                        case 0:     //  Take a picture
                            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                                ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                            }
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                            }
                            break;
                        case 1:     //  Import picture
                            Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
                            if (openGalleryIntent.resolveActivity(activity.getPackageManager()) != null) {
                                openGalleryIntent.setType("image/*");
                                startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
                            }
                            break;
                        case 2:     //  Delete picture
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
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
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
            imageUri = Uri.fromFile(newfile);
            data.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mProfilImage.setImageBitmap(imageBitmap);
            //File photo = new File(Environment.getExternalStorageDirectory());
            //data.putExtra(MediaStore.EXTRA_OUTPUT,
            //        Uri.fromFile(photo));
            //imageUri = Uri.fromFile(photo);
            updateStorage(data, imageUri);
            /*Uri uri = data.getData();
            File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);*/
        }
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
           updateStorage(data, imageUri);
        }
    }

    private void updateUI(final FirebaseUser user) {
        Log.d(TAG, "getUserData");

        Glide.with(this).load(mUserProfilImageRef).into(mProfilImage);

        mUserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mUserData = dataSnapshot.getValue(User.class);
                    //   Set Verify warning if necessary
                    //setVerifyAccountWarning();

                    if(mUserData != null) {
                        //   Set the field with data
                        if (mUserData.getName() != null && !mUserData.getName().isEmpty())
                            mNameField.setText(mUserData.getName());
                        if (mUserData.getUsername() != null && !mUserData.getUsername().isEmpty())
                            mUsernameField.setText(mUserData.getUsername());
                        if (mUserData.getBiography() != null && !mUserData.getBiography().isEmpty())
                            mBiographyField.setText(mUserData.getBiography());
                        if (mUserData.getEmail() != null && !mUserData.getEmail().isEmpty())
                            mEmailField.setText(mUserData.getEmail());
                    }

                    //activity.hideProgressDialog();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, databaseError.getDetails());
                }
            });
    }

    private void updateStorage(Intent data, Uri imageUri) {

        activity.showProgressDialog();

        mUserProfilImageRef.putFile(imageUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Byte transferred: " + taskSnapshot.getBytesTransferred());
                        //Toast.makeText(activity, "Byte transferd: " + taskSnapshot.getBytesTransferred(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(activity, "Image uploaded", Toast.LENGTH_SHORT).show();
                activity.hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(activity, "Some problem occured while uploading", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //TODO: Check if USERNAME already exist
    //TODO: Check if field are empty
    public void updateDB(FirebaseUser user) {

        mUserData.setName(mNameField.getText().toString());
        mUserData.setUsername(mUsernameField.getText().toString());
        mUserData.setBiography(mBiographyField.getText().toString());

        Log.d(TAG, "User: " + mUserData.getName());

        DatabaseManager.getInstance().getReference()
                .child("users")
                .child(user.getUid())
                .setValue(mUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Change saved", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Data inserted ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Couldn't be saved", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Something went wrong inserting data");
                    }
                });
        //activity.hideProgressDialog();
    }

    private void photo() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        StorageReference profilImageRef = storageRef.child("profilImages");


        /*
        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
        uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
