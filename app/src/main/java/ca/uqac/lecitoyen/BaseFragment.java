package ca.uqac.lecitoyen;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ca.uqac.lecitoyen.Interface.iHandleFragment;

public abstract class BaseFragment extends Fragment {

    private static String TAG = "BaseActivity";

    private iHandleFragment mHandleFragment;


    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void setFragmentToolbar(Activity parentActivity, int toolbarResid, int returnHomeIcon,
                                      boolean isDisplayHomeEnable, boolean hasOptionMenu) {

        try {
            Toolbar toolbar = parentActivity.findViewById(toolbarResid);
            ((AppCompatActivity) parentActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeEnable);
            ((AppCompatActivity) parentActivity).getSupportActionBar().setHomeAsUpIndicator(returnHomeIcon);
            setHasOptionsMenu(hasOptionMenu);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

}
