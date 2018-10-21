package ca.uqac.lecitoyen.userUI.profile;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditEmailFragment extends BaseFragment {

    private static final String TAG = "EditEmailFragment";

    private EditText viewPasswordField;
    private EditText viewNewEmailField;
    private EditText viewNewEmailFieldVerify;

    //
    private User userData;

    //  Firebase Authentification
    private DatabaseManager dbManager;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;
    private DatabaseReference dbUserData;

    private EditProfileActivity activity;
    private iHandleFragment mHandleFragment;

    public EditEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (EditProfileActivity) getActivity();
        this.fbAuth = FirebaseAuth.getInstance();
        this.dbManager = DatabaseManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profil_email, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_close_white_24dp, true, true);

        //  Views
        viewPasswordField = view.findViewById(R.id.change_email_password);
        viewNewEmailField = view.findViewById(R.id.change_email_new_email);
        viewNewEmailFieldVerify = view.findViewById(R.id.change_email_verify_new_email);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(fbAuth != null)
        {
            fbUser = fbAuth.getCurrentUser();
            if(fbUser != null)
            {
                String uid = fbUser.getUid();
                //  Get user database reference
                userData = activity.getUserData();
                dbUserData = dbManager.getDatabaseUser(uid);
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mHandleFragment.inflateFragment(R.string.fragment_edit_profil,"");
                return true;
            case R.id.menu_confirm:
                if(fbUser != null) reauthenticateUser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reauthenticateUser() {
        Log.d(TAG, "reauthenticateUser");

        if(!validateForm()) {
            return;
        }
        //  Check credential
        AuthCredential credential = EmailAuthProvider.getCredential(
                fbUser.getEmail(),
                viewPasswordField.getText().toString()
        );

        fbUser.reauthenticate(credential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateEmail();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Mot de passe actuel erroné", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmail() {
        Log.d(TAG, "updateEmail");

        showProgressDialog();
        //TODO: check if email is valid
        boolean isEmailValid = true;

        String email = viewNewEmailField.getText().toString();
        String emailAgain = viewNewEmailFieldVerify.getText().toString();
        boolean emailMatch = email.equals(emailAgain);

        if(isEmailValid) {

            if (emailMatch) {

                fbUser.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateDB();
                        Toast.makeText(getContext(), "Email changed", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Email not valid", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Email are different", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Email is not valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDB() {
        Log.d(TAG, "updateDB");

        dbUserData.child("email")
                .setValue(viewNewEmailField.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mHandleFragment.inflateFragment(R.string.fragment_edit_profil, "");
                        Toast.makeText(getContext(), "Change saved", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Data inserted ");
                        hideProgressDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Couldn't be saved", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Something went wrong inserting data");
                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = viewPasswordField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            viewPasswordField.setError("Required.");
            valid = false;
        } else {
            viewPasswordField.setError(null);
        }

        String newEmail = viewNewEmailField.getText().toString();
        if (TextUtils.isEmpty(newEmail)) {
            viewNewEmailField.setError("Required.");
            valid = false;
        } else {
            viewNewEmailField.setError(null);
        }

        String newEmailAgain = viewNewEmailFieldVerify.getText().toString();
        if (TextUtils.isEmpty(newEmailAgain)) {
            viewNewEmailFieldVerify.setError("Required.");
            valid = false;
        } else {
            viewNewEmailFieldVerify.setError(null);
        }

        return valid;
    }

}
