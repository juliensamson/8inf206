package ca.uqac.lecitoyen.userUI.settings;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeEmailFragment extends BaseFragment {

    private static final String TAG = "ChangeEmailFragment";

    private EditText mActualPasswordField;
    private EditText mNewEmailField;
    private EditText mVerifyNewEmailField;

    //  Firebase Authentification
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;

    private boolean passwordFieldEmpty = true;
    private boolean newEmailFieldEmpty = true;
    private boolean verifyEmailFieldEmpty = true;

    private UserSettingsActivity activity;
    private iHandleFragment mHandleFragment;

    private boolean fieldsCompleted = false;

    Menu confirmMenu;

    public ChangeEmailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (UserSettingsActivity) getActivity();
        //  Get user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_close_white_24dp, true, true);

        mActualPasswordField = view.findViewById(R.id.change_email_actual_password);
        mNewEmailField= view.findViewById(R.id.change_email_new_email);
        mVerifyNewEmailField = view.findViewById(R.id.change_email_verify_new_email);

        isPasswordEmpty();
        return view;
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
        /*confirmMenu = menu;
        MenuItem item = confirmMenu.findItem(R.id.menu_confirm);
        if (fieldsCompleted) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            // disabled
            item.setEnabled(false);
            item.getIcon().setAlpha(90);
        }*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mHandleFragment.inflateFragment(R.string.fragment_main_user_settings,"");
                return true;
            case R.id.menu_confirm:
                if(!mActualPasswordField.getText().toString().equals("")
                        && !mNewEmailField.getText().toString().equals("")
                        && !mVerifyNewEmailField.getText().toString().equals(""))
                    reauthenticateUser();
                reauthenticateUser();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }





    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isPasswordEmpty() {

        mActualPasswordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isNewEmailFieldEmpty() && !isVerifyEmailFieldEmpty()) {
                    if (charSequence == null || charSequence.length() == 0) {
                        fieldsCompleted = false;
                    } else {
                        fieldsCompleted = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return passwordFieldEmpty;
    }

    private boolean isNewEmailFieldEmpty() {
        mNewEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    newEmailFieldEmpty = false;
                } else {
                    newEmailFieldEmpty = true;
                    Toast.makeText(getContext(), R.string.toast_email_field_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return newEmailFieldEmpty;
    }

    private boolean isVerifyEmailFieldEmpty() {

        mVerifyNewEmailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    verifyEmailFieldEmpty = false;
                } else {
                    verifyEmailFieldEmpty = true;
                    Toast.makeText(getContext(), R.string.toast_email_field_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return verifyEmailFieldEmpty;
    }

    private void reauthenticateUser() {
        Log.d(TAG, "reauthenticateUser");

        //  Check credential
        AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), mActualPasswordField.getText().toString());

        activity.showProgressDialog();
        mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                activity.hideProgressDialog();
                if(task.isSuccessful())
                {
                    updateEmail();
                }
                else
                {
                    Toast.makeText(getContext(), "Mot de passe actuel erroné", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //TODO: Check if the email is valid
    private void updateEmail() {
        Log.d(TAG, "updateEmail");

        if(isValidEmail(mNewEmailField.getText().toString()))
        {
            if (mNewEmailField.getText().toString().equals(mVerifyNewEmailField.getText().toString()))
            {
                activity.showProgressDialog();
                mUser.updateEmail(mNewEmailField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        activity.hideProgressDialog();
                        if (task.isSuccessful())
                        {
                            Log.w(TAG, "updateEmail succeed");
                            mHandleFragment.inflateFragment(R.string.fragment_main_user_settings, "");
                            updateDB();
                            Toast.makeText(getContext(), "Email changed", Toast.LENGTH_SHORT).show();

                        }
                        else
                        {
                            Log.e(TAG, "updateEmail failed");
                            Toast.makeText(getContext(), "Email not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Log.e(TAG, "Email does not correspond");
                Toast.makeText(getContext(), "Email are different", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "The email is not valid", Toast.LENGTH_SHORT).show();
        }
    }


    public void updateDB() {
        Log.d(TAG, "updateDB");

        DatabaseManager.getInstance().getReference()
                .child("users")
                .child(mUserId)
                .child("email")
                .setValue(mNewEmailField.getText().toString())
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
        activity.hideProgressDialog();
    }

}
