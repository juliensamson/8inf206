package ca.uqac.lecitoyen.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class SigninFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SigninFragment";

    private Button mCreateAccountEmail;
    private TextView mLoginFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin_layout, container, false);

        //  Views
        mCreateAccountEmail = view.findViewById(R.id.button_create_account_email);
        mLoginFragment = view.findViewById(R.id.go_to_login_fragment);

        // Buttons
        view.findViewById(R.id.button_create_account_email).setOnClickListener(this);
        view.findViewById(R.id.go_to_login_fragment).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_create_account_email) {
            Intent intent = new Intent(getContext(), EmailPasswordActivity.class);
            Bundle extras = new Bundle();
            extras.putString("display_button", "create_account");
            intent.putExtras(extras);
            startActivity(intent);
        } else if (id == R.id.go_to_login_fragment) {
            ((MainActivity)getActivity()).setupViewPager(0);
        }
    }
}
