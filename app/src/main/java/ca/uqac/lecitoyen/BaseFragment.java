package ca.uqac.lecitoyen;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
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
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.database.Post;

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

    public String getTimeDifference(Context mContext, final Post currentPost) {

        String textBeforeAgo = mContext.getResources().getString(R.string.text_time_ago) + " ";
        String textBeforeThe  = mContext.getResources().getString(R.string.text_time_the) + " ";
        String textSecond = mContext.getResources().getString(R.string.time_short_second);
        String textMinute = mContext.getResources().getString(R.string.time_short_minute);
        String textHour   = mContext.getResources().getString(R.string.time_short_hour);

        long postTime = currentPost.getDate();
        long timeElapse = System.currentTimeMillis() - postTime;

        Date postDate = new Date(postTime) ;

        String timeDisplayed;
        if(timeElapse < minute)
        {
            timeDisplayed = String.valueOf(timeElapse / second);
            return textBeforeAgo + timeDisplayed + textSecond;
        }
        else if(timeElapse >= minute && timeElapse < hour)
        {
            timeDisplayed = String.valueOf(timeElapse / minute);
            return textBeforeAgo = timeDisplayed + textMinute;
        }
        else if(timeElapse >= hour && timeElapse < day)
        {
            timeDisplayed = String.valueOf(timeElapse / hour);
            return textBeforeAgo + timeDisplayed + textHour;
        }
        else
        {
            timeDisplayed = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.short_date_format),
                    Locale.CANADA_FRENCH)
                    .format(postDate);
            return textBeforeThe + timeDisplayed;
        }
    }


    protected void destroyPreviousActivity(Context currActivityContext, Class nextActivity) {
        Log.e(TAG, "destroyPreviousActivity");
        Intent intent = new Intent(currActivityContext, nextActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
