package ca.uqac.lecitoyen.userUI.cityfeed;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.userUI.UserMainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityfeedFragment extends BaseFragment {

    final private static String TAG = "CityfeedFragment";

    private UserMainActivity userMainActivity;
    private iHandleFragment mHandleFragment;

    private TextView mDisplayedMessage;

    public CityfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.userMainActivity = (UserMainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cityfeed, container, false);
        mDisplayedMessage = view.findViewById(R.id.fragment_cityfeed_title);
        setFragmentToolbar(view, userMainActivity, R.id.toolbar_newsfeed, getTag(), false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (UserMainActivity) getActivity();
    }
}
