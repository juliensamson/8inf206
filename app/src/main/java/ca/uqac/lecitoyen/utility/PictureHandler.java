package ca.uqac.lecitoyen.utility;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseActivity;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.profile.EditProfileActivity;
import ca.uqac.lecitoyen.userUI.profile.EditProfileFragment;
import ca.uqac.lecitoyen.userUI.profile.ProfilFragment;


public class PictureHandler extends BaseActivity {

    private static final String TAG = "PictureHandler";


    private static final int CAMERA_REQUEST_CODE= 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int DELETE_REQUEST_CODE = 0;

    Activity mActivity;
    StorageReference stReference;
    DatabaseReference dbReference;

    private ArrayList<UserStorage> listProfilPicture = new ArrayList<>();


    public PictureHandler() {};

    public PictureHandler(Activity activity, DatabaseReference dbRef, StorageReference stRef, User userdata) {
        this.mActivity = activity;
        this.dbReference = dbRef;
        this.stReference = stRef;
    }

    public void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        if (openGalleryIntent.resolveActivity(getPackageManager()) != null) {
            openGalleryIntent.setType("image/*");
            startActivityForResult(openGalleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    public void takePicture() {

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
                    //updateStorage(imageUri);
                    break;
                case CAMERA_REQUEST_CODE:    //TODO: Make this work somehow
                    //checkInternalStorage();
                    //updateStorage(imageUri);
                    break;
                case DELETE_REQUEST_CODE:
                    break;
                default:
                    break;
            }
        }
    }

    /*private void updateStorage(Uri imageUri) {

        showProgressDialog();

        if(mActivity instanceof EditProfileActivity)
            dbReference.addListenerForSingleValueEvent(updateUserProfilPicture());

        final UserStorage userStorage = new UserStorage();
        userStorage.setIid(dbUserProfilPicture.push().getKey());
        userStorage.setUploadTimestamp(System.currentTimeMillis());
        userStorage.setProfilPicture(true);

        //  Set main profil picture id to user
        mUserData.setPid(userStorage.getPid());
        pid = userStorage.getPid();

        stUserProfilPicture
                .child(mUserData.getPid())
                .putFile(imageUri)
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
                        updateDB();
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
    }*/

    /*private ValueEventListener updateUserProfilPicture() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (listProfilPicture.size() != 0)
                {
                    for (int i = 0; i < listProfilPicture.size(); i++) {
                        mUserStorage = listProfilPicture.get(i);
                        mUserStorage.setProfilPicture(false);
                        dbUserProfilPicture.child(mUserStorage.getPid()).setValue(mUserStorage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        };
    }*/

}
