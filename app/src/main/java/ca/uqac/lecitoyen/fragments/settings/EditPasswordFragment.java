package ca.uqac.lecitoyen.fragments.settings;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.activities.SettingsActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.fragments.BaseFragment;

public class EditPasswordFragment extends BaseFragment {

    private static String TAG = "EditPasswordFragment";

    private EditText mActualPasswordField;
    private EditText mNewPasswordField;
    private EditText mVerifyNewPasswordField;

    //  Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private SettingsActivity activity;
    private iHandleFragment mHandleFragment;

    public EditPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Get user
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        activity = (SettingsActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_close_white_24dp, true, true);

        //  View
        mActualPasswordField = view.findViewById(R.id.change_password_actual_password);
        mNewPasswordField= view.findViewById(R.id.change_password_new_password);
        mVerifyNewPasswordField = view.findViewById(R.id.change_password_verify_new_password);

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
                mHandleFragment.inflateFragment(R.string.fragment_main_user_settings, "");
                return true;
            case R.id.menu_confirm:
                if(!mActualPasswordField.getText().toString().equals("")
                        && !mNewPasswordField.getText().toString().equals("")
                        && !mVerifyNewPasswordField.getText().toString().equals(""))
                    reauthenticateUser();
                else
                    Toast.makeText(getContext(), "Un des champs est vide", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                    updatePassword();
                }
                else
                {
                    Toast.makeText(getContext(), R.string.toast_reset_password_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePassword() {
        Log.d(TAG, "updatePassword");
        if (mNewPasswordField.getText().toString().equals(mVerifyNewPasswordField.getText().toString())) {

            activity.showProgressDialog();
            mUser.updatePassword(mNewPasswordField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    activity.hideProgressDialog();

                    if (task.isSuccessful())
                    {
                        Log.w(TAG, "updatePassword succeed");
                        Toast.makeText(getContext(), "Mot de passe changé", Toast.LENGTH_SHORT).show();
                        mHandleFragment.inflateFragment(R.string.fragment_main_user_settings, "");
                    }
                    else
                    {
                        Log.e(TAG, "updatePassword failed");
                        Toast.makeText(getContext(), "Mot de passe est trop court", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            Log.e(TAG, "Passwords does not correspond");
            Toast.makeText(getContext(), R.string.toast_password_different, Toast.LENGTH_SHORT).show();
        }
    }

}
