package ca.uqac.lecitoyen;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    final private static String TAG = "BaseFragment";

    final private static long mCurrentTime = System.currentTimeMillis();

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

    protected void setFragmentToolbar(Activity parentActivity, int returnHomeIcon, boolean isDisplayHomeEnable, boolean hasOptionMenu) {
        try {
            Toolbar toolbar = parentActivity.findViewById(R.id.toolbar_default);
            ((AppCompatActivity) parentActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeEnable);
            ((AppCompatActivity) parentActivity).getSupportActionBar().setHomeAsUpIndicator(returnHomeIcon);
            setHasOptionsMenu(hasOptionMenu);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }


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