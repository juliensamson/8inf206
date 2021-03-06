package ca.uqac.lecitoyen.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.R;

public class BaseActivity extends AppCompatActivity {

    private static String TAG = "BaseActivity";

    final public static long currentTimeMillis = System.currentTimeMillis();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @VisibleForTesting
    private ProgressDialog mProgressDialog;
    private Toolbar mToolbarByDefault;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
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

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setToolbar(int toolbarId, String title, int returnHomeIcon, boolean isDisplayHomeEnable) {
        Toolbar toolbar = findViewById(toolbarId);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(mToolbarByDefault);
        toolbarTitle.setText(title);

        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeEnable);
            getSupportActionBar().setHomeAsUpIndicator(returnHomeIcon);
            getSupportActionBar().setElevation(5);

        } catch (NullPointerException npe) {

            Log.e(TAG, npe.getMessage());

        }
    }

    public void createToolbar(String title, boolean displayReturnHome) {
        mToolbarByDefault = findViewById(R.id.toolbar_default);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);


        setSupportActionBar(mToolbarByDefault);
        toolbarTitle.setText(title);


        try {

            getSupportActionBar().setDisplayHomeAsUpEnabled(displayReturnHome);

        } catch (NullPointerException npe) {

            Log.e(TAG, npe.getMessage());

        }
    }

    public void showToolbar() {
        if(mToolbarByDefault == null) {
            mToolbarByDefault = new Toolbar(this);
        }
        mToolbarByDefault.setVisibility(View.VISIBLE);
    }

    public void hideToolbar() {
        if(mToolbarByDefault == null) {
            mToolbarByDefault = new Toolbar(this);
        }
        mToolbarByDefault.setVisibility(View.GONE);
    }



    protected void doFragmentTransaction(int containerId, Fragment fragment, String tag, boolean addToBackStack, String message) {
        Log.d(TAG, "doFragmentTransaction");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(containerId, fragment, tag);

        if(addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    protected void destroyPreviousActivity(Context currActivityContext, Class nextActivity) {
        Log.e(TAG, "destroyPreviousActivity");
        Intent intent = new Intent(currActivityContext, nextActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void setNameTextView(TextView textView, String name) {

    }

    protected void setImageView(ImageView imageView, StorageReference st) {

        if(imageView != null) {

            if(st != null)
                Glide.with(this).load(st).into(imageView);
            else
                Glide.with(this).load(R.color.black_200).into(imageView);

        } else {

            Log.e(TAG, "The image view widget is null");
            // set place id if it doesn't work
        }
    }

}
