package ca.uqac.lecitoyen.User.UserSettings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.User.UserFragments.CityFragment;
import ca.uqac.lecitoyen.User.UserFragments.HomeFragment;
import ca.uqac.lecitoyen.User.UserFragments.MessageFragment;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;

import static ca.uqac.lecitoyen.BaseActivity.currentTimeMillis;


public class MainUserSettingsFragment extends BaseFragment implements View.OnClickListener {

    private static String TAG = "UserSettingsActivity";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private User mUserData;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText mNameField;
    private EditText mUserNameField;
    private TextView mEmail;

    private UserSettingsActivity activity;
    private iHandleFragment mHandleFragment;

    public MainUserSettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (UserSettingsActivity) getActivity();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_user_settings, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.id.toolbar_default, R.drawable.ic_arrow_back_white_24dp, true, true);

        //  View
        mNameField = view.findViewById(R.id.user_setting_realname);
        mUserNameField = view.findViewById(R.id.user_setting_username);
        mEmail = view. findViewById(R.id.user_setting_email);

        //  Button
        view.findViewById(R.id.change_email_button).setOnClickListener(this);
        view.findViewById(R.id.change_password_button).setOnClickListener(this);
        view.findViewById(R.id.signout_account_button).setOnClickListener(this);
        view.findViewById(R.id.delete_account_button).setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(mUser);
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
                activity.onBackPressed();
                return true;
            case R.id.menu_confirm:
                activity.showProgressDialog();
                if(!mUserNameField.getText().toString().equals(""))
                    updateDB(mUser);
                Log.w(TAG, "Information saved");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.change_email_button:
                mHandleFragment.inflateFragment(R.string.fragment_change_email,"");
                break;
            case R.id.change_password_button:
                mHandleFragment.inflateFragment(R.string.fragment_change_password,"");
                break;
            case R.id.delete_account_button:
                mHandleFragment.inflateFragment(R.string.fragment_delete_account,"");
                break;
            case R.id.signout_account_button:
                activity.signOutAccount();
                break;
            default:
                break;
        }
    }

    public void updateUI(final FirebaseUser user) {

        //  Initialize database manager
        mUserReference = DatabaseManager.getInstance().getReference();

        activity.showProgressDialog();

        mUserReference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //  Add the user to the database if he wasn't added before
                mUserData = dataSnapshot.getValue(User.class);

                mNameField.setText(mUserData.getName());
                mUserNameField.setText(mUserData.getUsername());
                mEmail.setText(mUserData.getEmail());

                activity.hideProgressDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getDetails());
            }
        });


    }

    //TODO: Check if USERNAME already exist
    public void updateDB(FirebaseUser user) {

        mUserData.setName(mNameField.getText().toString());
        mUserData.setUsername(mUserNameField.getText().toString());

        mUserReference.child("users").child(user.getUid()).setValue(mUser)
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
