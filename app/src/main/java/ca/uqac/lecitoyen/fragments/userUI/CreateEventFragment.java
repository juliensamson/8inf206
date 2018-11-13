package ca.uqac.lecitoyen.fragments.userUI;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.EditProfilActivity;
import ca.uqac.lecitoyen.activities.SettingsActivity;
import ca.uqac.lecitoyen.dialogs.ImageBottomDialog;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import ca.uqac.lecitoyen.fragments.settings.EditPasswordFragment;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.views.ToolbarView;

public class CreateEventFragment extends BaseFragment {

    private static final String TAG = CreateEventFragment.class.getSimpleName();

    private static final String ARG_USER = "user";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mUserAuth;
    private String mParam2;


    // Views
    private ToolbarView mToolbarEvent;
    private EditText mAddImageEditText;

    private OnFragmentInteractionListener mListener;

    public CreateEventFragment() {
        // Required empty public constructor
    }

    public static CreateEventFragment newInstance(User userAuth) {
        CreateEventFragment fragment = new CreateEventFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, userAuth);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserAuth = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_event, container, false);
        mToolbarEvent = view.findViewById(R.id.create_event_toolbar);
        mToolbarEvent.buttonToolbar(getActivity(), "Créer évènement");
        mAddImageEditText = view.findViewById(R.id.create_event_add_image);
        mAddImageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageBottomDialog dialog = new ImageBottomDialog(getActivity());
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "onCreateOptionsMenu");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.w(TAG, "item selected");
        switch (item.getItemId())
        {
            case android.R.id.home:
                onButtonPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
