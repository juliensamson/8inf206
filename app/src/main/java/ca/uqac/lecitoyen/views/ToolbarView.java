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
import android.view.Gravity;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class ToolbarView extends FrameLayout {

    private final static String TAG = ToolbarView.class.getSimpleName();

    public final static int GRAVITY_START = 333;
    public final static int GRAVITY_END   = 666;

    private Context mContext;

    private FrameLayout mToolbarLayout;
    private TextView mToolbarTitle;
    private CircleImageView mImageView;

    private View rootView;

    public ToolbarView(Context context) {
        super(context);
        inflate(context);
    }

    public ToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    private void inflate(Context context) {
        rootView  = inflate(context, R.layout.custom_toolbar_view, this);

        this.mContext = context;

        this.mToolbarLayout = rootView.findViewById(R.id.toolbar_view_with_image_layout);
        this.mToolbarTitle  = rootView.findViewById(R.id.toolbar_view_with_image_title);
        this.mImageView     = rootView.findViewById(R.id.toolbar_view_image_view);

    }

    public void createToolbarWithImageView(Activity activity, boolean displayHome, int returnHomeIcon) {

        //View rootView = inflate(activity);

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        try {
            Toolbar toolbar = findViewById(R.id.custom_toolbar);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);

            ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(displayHome);
            ((AppCompatActivity) activity).getSupportActionBar().setHomeAsUpIndicator(returnHomeIcon);

        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }
    }

    public ToolbarView create(Activity parent, int style, String title, StorageReference image) {

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        setTitle(title);

        setImage(image);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        ((AppCompatActivity)parent).setSupportActionBar(toolbar);

        LayoutParams paramsImageView = (LayoutParams) mImageView.getLayoutParams();
        switch (style) {

            case GRAVITY_START:
                paramsImageView.gravity = Gravity.START;
                paramsImageView.setMarginStart(0);
                mImageView.setLayoutParams(paramsImageView);
                break;
            case GRAVITY_END:
                paramsImageView.gravity = Gravity.END;
                mImageView.setLayoutParams(paramsImageView);

                LayoutParams paramsTitle = (LayoutParams) mToolbarTitle.getLayoutParams();
                paramsTitle.setMarginStart(0);
                mToolbarTitle.setLayoutParams(paramsTitle);
                break;

        }

        return this;
    }


    public ToolbarView createToolbarWithImageView(Activity parent, Fragment fragment, boolean displayHome, int returnHomeIcon) {

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

        return this;
    }

    public ToolbarView setTitle(String title) {

        if(mToolbarTitle != null){
            mToolbarTitle.setText(title);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        return this;
    }

    public ToolbarView setImage(Uri uri) {

        if(mImageView != null) {
            Glide.with(mContext).load(uri).into(mImageView);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        return this;
    }

    public ToolbarView setImage(StorageReference st) {

        if(mImageView != null) {
            Glide.with(mContext).load(st).into(mImageView);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

        return this;
    }


    public void hide() {

        if(mImageView != null) {
            mImageView.setVisibility(GONE);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

    }

    public void show() {

        if(mImageView != null) {
            mImageView.setVisibility(VISIBLE);
        } else {
            Log.e(TAG, "The views are not initialize");
        }

    }

    public void onImageClickListener(OnClickListener listener) {
        mImageView.setOnClickListener(listener);
    }

}
