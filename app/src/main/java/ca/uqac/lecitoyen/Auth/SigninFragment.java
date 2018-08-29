package ca.uqac.lecitoyen.Auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.uqac.lecitoyen.MainActivity;
import ca.uqac.lecitoyen.R;

/**
 * Created by jul_samson on 18-08-29.
 */

public class SigninFragment extends Fragment {

    private static final String TAG = "SigninFragment";

    private TextView mLoginFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin_layout, container, false);

        mLoginFragment = view.findViewById(R.id.go_to_login_fragment);
        mLoginFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).setViewPager(0);
            }
        });
        return view;
    }
}
