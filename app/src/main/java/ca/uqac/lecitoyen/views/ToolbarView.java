package ca.uqac.lecitoyen.views;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import org.w3c.dom.Text;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.BaseActivity;

public class ToolbarView extends AppBarLayout {

    private final static String TAG = ToolbarView.class.getSimpleName();

    public final static int GRAVITY_START = 333;
    public final static int GRAVITY_END   = 666;

    private Context mContext;

    private FrameLayout mToolbarLayoutEnd;
    private TextView mToolbarTitleEnd;
    private RoundedImageView mImageViewEnd;

    private LinearLayout mToolbarLayoutStart;
    private TextView mToolbarTitleStart;
    private RoundedImageView mImageViewStart;

    private View rootView;

    public ToolbarView(Context context) {
        super(context);
        inflate(context);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    private View inflate(Context context) {
        rootView  = inflate(context, R.layout.custom_toolbar_view, this);

        this.mContext = context;

        this.mToolbarLayoutEnd = rootView.findViewById(R.id.toolbar_view_with_image_right_layout);
        this.mToolbarTitleEnd  = rootView.findViewById(R.id.toolbar_view_with_image_right_title);
        this.mImageViewEnd     = rootView.findViewById(R.id.toolbar_view_image_view_right);

        this.mToolbarLayoutStart = rootView.findViewById(R.id.toolbar_view_with_image_left_layout);
        this.mToolbarTitleStart  = rootView.findViewById(R.id.toolbar_view_with_image_left_title);
        this.mImageViewStart     = rootView.findViewById(R.id.toolbar_view_image_view_left);

        return rootView;
    }

    public void createToolbarWithImageView(Activity activity, boolean displayHome, int returnHomeIcon) {

        //View rootView = inflate(activity);

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        try {
            Toolbar toolbar = rootView.findViewById(R.id.custom_toolbar);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);

            ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(displayHome);
            ((AppCompatActivity) activity).getSupportActionBar().setHomeAsUpIndicator(returnHomeIcon);

        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    public void createToolbarWithImageView(Activity parent, Fragment fragment, boolean displayHome, int returnHomeIcon) {

        //View rootView = inflate(activity);

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        try {
            Toolbar toolbar = rootView.findViewById(R.id.custom_toolbar);
            ((AppCompatActivity) parent).getSupportActionBar().setDisplayHomeAsUpEnabled(displayHome);
            ((AppCompatActivity) parent).getSupportActionBar().setHomeAsUpIndicator(returnHomeIcon);
            fragment.setHasOptionsMenu(true);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    public ToolbarView setTitle(String title) {

        if(mToolbarTitleStart != null){
            mToolbarTitleStart.setText(title);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        if(mToolbarTitleEnd != null){
            mToolbarTitleEnd.setText(title);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        return this;
    }

    public ToolbarView setImageView(Uri uri) {

        if(mImageViewStart != null) {
            Glide.with(mContext).load(uri).into(mImageViewStart);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        if(mImageViewEnd != null) {
            Glide.with(mContext).load(uri).into(mImageViewEnd);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        return this;
    }

    public ToolbarView setImageView(StorageReference st) {

        if(mImageViewStart != null) {
            Glide.with(mContext).load(st).into(mImageViewStart);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        if(mImageViewEnd != null) {
            Glide.with(mContext).load(st).into(mImageViewEnd);
        } else {
            Log.e(TAG, "The views are not initialize");
        }
        return this;
    }

    public ToolbarView setImageGravity(int gravity) {

        if(gravity == GRAVITY_START) {

            mToolbarLayoutStart.setVisibility(VISIBLE);
            mToolbarLayoutEnd.setVisibility(GONE);

        } else if(gravity == GRAVITY_END) {

            mToolbarLayoutEnd.setVisibility(GONE);
            mToolbarLayoutStart.setVisibility(VISIBLE);

        } else {
            Log.e(TAG, "Doesn't exist");
        }
        //params.height = mPicture.getMinimumHeight();
        //mPicture.setLayoutParams(params);
        //Log.d(TAG, "Height " + params.height);

        return this;
    }

    public RoundedImageView hide() {

        if(mImageViewStart != null) {
            mImageViewStart.setVisibility(GONE);
            return mImageViewStart;
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        if(mImageViewEnd != null) {
            mImageViewEnd.setVisibility(GONE);
            return  mImageViewEnd;
        } else {
            Log.e(TAG, "The views are not initialize");
        }
        return null;
    }

    public RoundedImageView show() {

        if(mImageViewStart != null) {
            mImageViewStart.setVisibility(GONE);
            return mImageViewStart;
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        if(mImageViewEnd != null) {
            mImageViewEnd.setVisibility(GONE);
            return  mImageViewEnd;
        } else {
            Log.e(TAG, "The views are not initialize");
        }
        return null;
    }

}
