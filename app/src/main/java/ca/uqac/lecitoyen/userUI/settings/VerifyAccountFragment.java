package ca.uqac.lecitoyen.userUI.settings;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;

public class VerifyAccountFragment extends BaseFragment {

    private static final String TAG = "VerifyAccountFragment";


    public VerifyAccountFragment() {
        // Required empty public constructor
    }



    //  Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String mUserId;

    private UserSettingsActivity activity;
    private iHandleFragment mHandleFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (UserSettingsActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_account, container, false);

        //  Toolbar
        mHandleFragment.setToolbarTitle(getTag());
        setFragmentToolbar(activity, R.id.toolbar_default, R.drawable.ic_close_white_24dp, true, true);


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
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
