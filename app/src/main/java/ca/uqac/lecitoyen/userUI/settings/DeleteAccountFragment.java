package ca.uqac.lecitoyen.userUI.settings;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteAccountFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = "DeleteAccountFragment";

    private EditText mPasswordField;
    private Button mConfirmDeleteAccount;

    DatabaseManager mDatabaseManager;

    View view;

    //  Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;

    private UserSettingsActivity activity;
    private iHandleFragment mHandleFragment;

    public DeleteAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Get user
        activity = (UserSettingsActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserId = mUser.getUid();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.id.toolbar_default, R.drawable.ic_close_white_24dp, true, true);

        //  View
        mPasswordField = view.findViewById(R.id.delete_account_actual_password);

        //  Button
        view.findViewById(R.id.delete_account_confirm_button).setOnClickListener(this);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mHandleFragment.inflateFragment(R.string.fragment_main_user_settings,"");
                return true;
            case R.id.menu_confirm:
                reauthenticateUser();
                return true;
        }
        return false;
    }

    private void reauthenticateUser() {
        Log.d(TAG, "reauthenticateUser");

        if (!mPasswordField.getText().toString().isEmpty())
        {
            //  Check credential
            AuthCredential credential = EmailAuthProvider.getCredential(mUser.getEmail(), mPasswordField.getText().toString());

            mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        view.findViewById(R.id.delete_account_confirm_button).setEnabled(true);
                        view.findViewById(R.id.delete_account_confirm_button).setBackgroundColor(getResources().getColor(R.color.i_secondary_700));
                    } else {
                        view.findViewById(R.id.delete_account_confirm_button).setEnabled(false);
                        view.findViewById(R.id.delete_account_confirm_button).setBackgroundColor(getResources().getColor(R.color.black_100));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            });
        }
    }

    private void deleteAccount() {
        Log.d(TAG, "deleteAccount");

        mUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.w(TAG,"Account deleted");
                    // Delete account but also delete data in reference to the account
                    updateDB();
                    //Close all previous activity
                    destroyPreviousActivity(getContext(), MainActivity.class);
                } else {
                    Log.e(TAG,"Cannot delete account!");
                }
            }
        });
    }

    private void updateDB() {
        DatabaseManager.getInstance().getReference()
                .child("users")
                .child(mUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getContext(), "Données supprimer", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Data deleted  ");
                }
                else
                {
                    Log.e(TAG, "Couldn't remove firebase user database ");
                }

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.delete_account_confirm_button:
                activity.showProgressDialog();
                deleteAccount();
                activity.hideProgressDialog();
                break;
            default:
                break;
        }
    }

}
