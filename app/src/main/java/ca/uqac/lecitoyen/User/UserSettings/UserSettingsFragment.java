package ca.uqac.lecitoyen.User.UserSettings;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

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
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;


public class UserSettingsFragment extends BaseFragment implements View.OnClickListener {

    private static String TAG = "UserSettingsActivity";

    private DatabaseManager mDatabaseManager;
    private DatabaseReference mUserReference;
    private User mUserData;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private FrameLayout mVerifyAccountLayout;
    private ImageView mCloseWarning;
    private Button mVerifyAccount;
    private EditText mNameField;
    private EditText mUsernameField;
    private EditText mBiographyField;
    private LinearLayout mEmailLayout;
    private EditText mEmailField;
    private EditText mPhoneField;
    private EditText mLocationField;

    private UserSettingsActivity activity;
    private iHandleFragment mHandleFragment;

    public UserSettingsFragment() {
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
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.id.toolbar_default, R.drawable.ic_arrow_back_white_24dp, true, true);

        //  Verify Warning
        mVerifyAccountLayout = view.findViewById(R.id.user_setting_verify_user_layout);
        view.findViewById(R.id.user_setting_close_warning).setOnClickListener(this);
        view.findViewById(R.id.user_setting_verify_user).setOnClickListener(this);

        //  View
        mNameField = view.findViewById(R.id.user_setting_realname);
        mUsernameField = view.findViewById(R.id.user_setting_username);
        mBiographyField = view.findViewById(R.id.user_setting_biography);
        mEmailField = view.findViewById(R.id.user_setting_email);
        mPhoneField = view.findViewById(R.id.user_setting_phone);
        mLocationField = view.findViewById(R.id.user_setting_location);

        //  Button
        view.findViewById(R.id.change_email_button).setOnClickListener(this);
        view.findViewById(R.id.user_setting_phone).setOnClickListener(this);
        view.findViewById(R.id.change_password_button).setOnClickListener(this);
        view.findViewById(R.id.delete_account_button).setOnClickListener(this);
        view.findViewById(R.id.signout_account_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //displayVerifyuUserWarning();
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
                if(!mUsernameField.getText().toString().equals(""))
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
            case R.id.user_setting_verify_user:
                mHandleFragment.inflateFragment(R.string.fragment_verify_account,"");
                break;
            case R.id.user_setting_close_warning:
                mVerifyAccountLayout.setVisibility(View.GONE);
                break;
            case R.id.change_email_button:
                mHandleFragment.inflateFragment(R.string.fragment_change_email,"");
                break;
            case R.id.user_setting_phone:
                Log.d(TAG, "phone");
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

    private void setVerifyAccountWarning() {
        if(!mUserData.isVerify()) {
            mVerifyAccountLayout.setVisibility(View.VISIBLE);
        } else {
            mVerifyAccountLayout.setVisibility(View.GONE);
        }
    }

    public void updateUI(final FirebaseUser user) {
       Log.d(TAG, "getUserData");

       //activity.showProgressDialog();

       DatabaseManager.getInstance().getReference()
               .child("users")
               .child(user.getUid())
               .addListenerForSingleValueEvent(new ValueEventListener()
               {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       mUserData = dataSnapshot.getValue(User.class);

                       //   Set Verify warning if necessary
                       setVerifyAccountWarning();

                       //   Set the field with data
                       mNameField.setText(mUserData.getName());
                       mUsernameField.setText(mUserData.getUsername());
                       mBiographyField.setText(mUserData.getBiography());
                       mEmailField.setText(mUserData.getEmail());


                       //activity.hideProgressDialog();
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {
                       Log.e(TAG, databaseError.getDetails());
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
}
