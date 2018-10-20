package ca.uqac.lecitoyen.userUI.settings;

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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.User;


public class UserSettingsFragment extends BaseFragment implements AdapterView.OnItemLongClickListener {

    private static String TAG = "UserSettingsActivity";

    private DatabaseManager dbManager;
    private DatabaseReference dbUserData;
    private User mUserData;
    private FirebaseAuth fbAuth;
    private FirebaseUser fbUser;

    private FrameLayout mVerifyAccountLayout;
    private ImageView mCloseWarning;
    private Button mVerifyAccount;
    private ListView vEditSettingListView;
    private ArrayAdapter<String> vAdapter;

    private String[] notVerify;
    private String[] verify;


    private UserSettingsActivity activity;
    private iHandleFragment mHandleFragment;

    public UserSettingsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (UserSettingsActivity) getActivity();
        this.dbManager = DatabaseManager.getInstance();
        this.fbAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.drawable.ic_arrow_back_white_24dp, true, false);


        //  View
        vEditSettingListView = view.findViewById(R.id.user_setting_listview);

        //  Button
        vEditSettingListView.setOnItemLongClickListener(this);

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
                dbUserData = dbManager.getDatabaseUser(uid);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        return false;
    }


    private void updateUI() {

        dbUserData.addListenerForSingleValueEvent(loadUserData());

    }

    private ValueEventListener loadUserData() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserData = dataSnapshot.getValue(User.class);

                if(mUserData != null) {
                    ArrayAdapter<String> adapter;
                    if (mUserData.isVerify()) {
                        adapter = new ArrayAdapter<String>(activity,
                                android.R.layout.simple_list_item_1,
                                activity.getResources().getStringArray(R.array.user_settings_verify));
                    } else {
                        adapter = new ArrayAdapter<String>(activity,
                                android.R.layout.simple_list_item_1,
                                activity.getResources().getStringArray(R.array.user_settings_not_verify));
                    }
                    vEditSettingListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

}
