package ca.uqac.lecitoyen.fragments.userUI;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.fragments.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityfeedFragment extends BaseFragment {

    final private static String TAG = "CityfeedFragment";

    private MainUserActivity mainUserActivity;
    private iHandleFragment mHandleFragment;

    private TextView mDisplayedMessage;

    public CityfeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainUserActivity = (MainUserActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cityfeed, container, false);
        mDisplayedMessage = view.findViewById(R.id.fragment_cityfeed_title);
        setFragmentToolbar(view, mainUserActivity, R.id.toolbar_default, getTag(), false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHandleFragment = (MainUserActivity) getActivity();
    }
}
