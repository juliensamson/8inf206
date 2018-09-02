package ca.uqac.lecitoyen.Auth;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;

/**
 * Created by jul_samson on 18-08-29.
 */

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private Intent mLoginAuthActivity;

    private Button mEmailLogin;

    private TextView mSigninFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_layout, container, false);

        //  View
        mEmailLogin = view.findViewById(R.id.button_login_email);
        mSigninFragment = view.findViewById(R.id.go_to_signin_fragment);


        //  Buttons
        mEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginAuthActivity = new Intent(getContext(), EmailPasswordActivity.class);
                startActivity(mLoginAuthActivity);
            }
        });
        mSigninFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setViewPager(1);
            }
        });

        return view;
    }

}
