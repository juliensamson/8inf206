package ca.uqac.lecitoyen.User;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.uqac.lecitoyen.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CityFragment extends Fragment {

    final private static String TAG = "CityFragment";

    private iUserActivity mUserInterface;

    private TextView mDisplayedMessage;

    public CityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserInterface.setToolbarTitle(getTag());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_city, container, false);
        mDisplayedMessage = view.findViewById(R.id.fragment_city_title);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mUserInterface = (UserActivity) getActivity();
    }
}
