package ca.uqac.lecitoyen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity {

    private static String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    public Toolbar mToolbarByDefault;

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

    public void showToolbar(String tag, String title) {

        Toolbar mToolbarByDefault = findViewById(R.id.toolbar_default);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(mToolbarByDefault);
        toolbarTitle.setText(title);

        if (!tag.equals("MainActivity"))
        {
            try {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (NullPointerException npe) {
                Log.e(TAG, npe.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
}
