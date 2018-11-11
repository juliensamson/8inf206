package ca.uqac.lecitoyen.fragments.userUI;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreatePostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreatePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePostFragment extends Fragment {

    private static final String TAG = CreatePostFragment.class.getSimpleName();

    private static final String ARG_TYPE = "type";
    private static final String ARG_USER = "user";
    private static final int TYPE_CREATE = 90;
    private static final int TYPE_EDIT   = 91;

    private MainUserActivity mUserActivity;
    private static int mType;
    private User mUserAuth;

    private OnFragmentInteractionListener mListener;

    public CreatePostFragment() {
        // Required empty public constructor
    }

    public static CreatePostFragment newInstance(int type, User userAuth) {

        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putParcelable(ARG_USER, userAuth);

        CreatePostFragment fragment = new CreatePostFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mUserActivity = (MainUserActivity) getActivity();


        if (getArguments() != null) {
            mType = getArguments().getInt(ARG_TYPE);
            mUserAuth = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }
}
