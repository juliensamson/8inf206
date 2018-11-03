package ca.uqac.lecitoyen.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.models.Post;

public abstract class BaseFragment extends Fragment {

    final private static String TAG = "BaseFragment";

    final private static long second = 1000;
    final private static long minute = 60 * second;
    final private static long hour = 60 * minute;
    final private static long day = 24 * hour;

    final private static long mCurrentTime = System.currentTimeMillis();



    private iHandleFragment mHandleFragment;

    @VisibleForTesting
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbarByDefault;


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

    /*

            Views

     */

    protected void setFragmentToolbar(View view, Activity parentActivity, int toolbarId, String toolbarTitle, boolean hasOptionMenu) {
        try {
            //  Views
            Toolbar toolbar = view.findViewById(toolbarId);
            TextView title = view.findViewById(R.id.toolbar_title);
            if(title == null)
                Log.e(TAG, "WHY");
            Log.d(TAG, toolbarTitle);
            title.setText(toolbarTitle);
            ((AppCompatActivity) parentActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(hasOptionMenu);

        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    protected void setFragmentToolbar(Activity parentActivity, int returnHomeIcon, boolean isDisplayHomeEnable, boolean hasOptionMenu) {
        try {
            ((AppCompatActivity) parentActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeEnable);
            ((AppCompatActivity) parentActivity).getSupportActionBar().setHomeAsUpIndicator(returnHomeIcon);
            setHasOptionsMenu(hasOptionMenu);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /*

            Others

    */

    protected long getCurrentTime() {
        return mCurrentTime;
    }

    protected void destroyPreviousActivity(Context currActivityContext, Class nextActivity) {
        Log.e(TAG, "destroyPreviousActivity");
        Intent intent = new Intent(currActivityContext, nextActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
