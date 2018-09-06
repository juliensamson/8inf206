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

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "LoginFragment";

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
        view.findViewById(R.id.button_login_email).setOnClickListener(this);
        view.findViewById(R.id.go_to_signin_fragment).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.button_login_email) {
            Intent intent = new Intent(getContext(), EmailPasswordActivity.class);
            Bundle extras = new Bundle();
            extras.putString("display_button", "login");
            intent.putExtras(extras);
            startActivity(intent);
        } else if (id == R.id.go_to_signin_fragment) {
            ((MainActivity)getActivity()).setupViewPager(1);
        }
    }
}
