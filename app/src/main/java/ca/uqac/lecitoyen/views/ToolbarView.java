package ca.uqac.lecitoyen.views;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.BaseActivity;
import ca.uqac.lecitoyen.fragments.BaseFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class ToolbarView extends FrameLayout  {

    private final static String TAG = ToolbarView.class.getSimpleName();

    public final static int GRAVITY_START = 333;
    public final static int GRAVITY_END   = 666;

    private Context mContext;

    private FrameLayout mToolbarLayout;
    private TextView mToolbarTitle, mToolbarButton;
    private CircleImageView mToolbarImage;

    private ImageView mToolbarClose, mReturn;
    private EditText mSearchEditText;
    private SearchView mSearchBar;

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
        this.mToolbarButton = rootView.findViewById(R.id.toolbar_view_button);
        this.mToolbarImage     = rootView.findViewById(R.id.toolbar_view_image_view);
        this.mSearchEditText = rootView.findViewById(R.id.toolbar_view_text_view);
        this.mToolbarClose = rootView.findViewById(R.id.toolbar_view_close);
        this.mReturn = rootView.findViewById(R.id.toolbar_view_return);

        hideAllViews();

    }

    public void defaultToolbar(Activity parent, int style, String title, StorageReference image) {

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        setToolbarTitle(title);
        setToolbarImage(image);
        setToolbarButton(null);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        ((AppCompatActivity)parent).setSupportActionBar(toolbar);

        LayoutParams paramsImageView = (LayoutParams) mToolbarImage.getLayoutParams();
        switch (style) {

            case GRAVITY_START:
                paramsImageView.gravity = Gravity.START;
                paramsImageView.setMarginStart(0);
                mToolbarImage.setLayoutParams(paramsImageView);
                break;
            case GRAVITY_END:
                paramsImageView.gravity = Gravity.END;
                mToolbarImage.setLayoutParams(paramsImageView);

                LayoutParams paramsTitle = (LayoutParams) mToolbarTitle.getLayoutParams();
                paramsTitle.setMarginStart(0);
                mToolbarTitle.setLayoutParams(paramsTitle);
                break;

        }

    }

    public void simpleToolbar(Activity parent, String title) {

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        try {

        setToolbarTitle(title);
        setToolbarImage((Uri) null);
        setToolbarButton(null);

            Toolbar toolbar = findViewById(R.id.custom_toolbar);
            ((AppCompatActivity)parent).setSupportActionBar(toolbar);
            ((AppCompatActivity) parent).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) parent).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_primary_24dp);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }

    }

    public void buttonToolbar(Activity activity, String title) {

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        setToolbarTitle(null);
        setToolbarImage((Uri) null);
        setToolbarButton(title);

        try {
            Toolbar toolbar = findViewById(R.id.custom_toolbar);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            ((AppCompatActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) activity).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_primary_24dp);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }

    }

    public void buttonToolbar(Fragment fragment, String title) {

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        setToolbarTitle(null);
        setToolbarImage((Uri) null);
        setToolbarButton(title);
        showToolbarClose();

        try {
            Toolbar toolbar = findViewById(R.id.custom_toolbar);
            ((AppCompatActivity) fragment.getActivity()).setSupportActionBar(toolbar);
        } catch (NullPointerException npe) {
            Log.e(TAG, npe.getMessage());
        }

    }

    public void searchToolbar(Activity parent, String title, StorageReference image) {

        if(rootView == null)
            throw new IllegalArgumentException("Make sure the view is inflated");

        mToolbarTitle.setVisibility(GONE);

        mSearchEditText.setHint(title);
        mSearchEditText.setVisibility(VISIBLE);

        final LayoutParams paramsEditText = (LayoutParams) mSearchEditText.getLayoutParams();
        final int dp = paramsEditText.getMarginStart() / 48;

            /*mSearchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Log.e(TAG, "focus text clicked");
                    Animation expandLeft = AnimationUtils.loadAnimation(mContext, R.anim.expand_left);
                    expandLeft.start();


                    mSearchEditText.setPaddingRelative(64 * dp, 8 * dp, 16 * dp, 8 * dp);
                    paramsEditText.setMarginStart(0);
                    mSearchEditText.setLayoutParams(paramsEditText);

                    mReturn.setVisibility(VISIBLE);
                }
            });*/
        mSearchEditText.setFocusable(true);

            mSearchEditText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e(TAG, "edit text clicked");
                    Animation expandLeft = AnimationUtils.loadAnimation(mContext, R.anim.expand_left);
                    expandLeft.start();


                    mSearchEditText.setPaddingRelative(64 * dp, 8 * dp, 16 * dp, 8 * dp);
                    paramsEditText.setMarginStart(0);
                    mSearchEditText.setLayoutParams(paramsEditText);

                    mReturn.setVisibility(VISIBLE);
                }
            });
        mReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation retractRight = AnimationUtils.loadAnimation(mContext, R.anim.retract_right);
                retractRight.start();


                mSearchEditText.setPaddingRelative(16 * dp, 8 * dp,16 * dp,8 * dp);
                paramsEditText.setMarginStart(48 * dp);
                mSearchEditText.setLayoutParams(paramsEditText);

                mReturn.setVisibility(GONE);
                mSearchEditText.setFocusable(false);
                mSearchEditText.clearFocus();
            }
        });

        setToolbarImage(image);

        Toolbar toolbar = findViewById(R.id.custom_toolbar);
        ((AppCompatActivity)parent).setSupportActionBar(toolbar);

        LayoutParams paramsImageView = (LayoutParams) mToolbarImage.getLayoutParams();
        paramsImageView.gravity = Gravity.START;
        paramsImageView.setMarginStart(0);
        mToolbarImage.setLayoutParams(paramsImageView);
    }

    public void showSearchView() {
        mSearchBar.setVisibility(VISIBLE);
    }


    public ToolbarView setToolbarTitle(String title) {

        showToolbarTitle();

        if(mToolbarTitle == null)
            throw new IllegalArgumentException("Toolbar title not inflated");

        if(title == null || title.isEmpty())
            hideToolbarTitle();
        else
            mToolbarTitle.setText(title);

        return this;
    }

    public ToolbarView setToolbarButton(String text) {

        showToolbarButton();

        if(mToolbarButton == null)
            throw new IllegalArgumentException("Toolbar button not inflated");

        if(text == null || text.isEmpty())
            hideToolbarButton();
        else
            mToolbarButton.setText(text);

        return this;
    }

    public ToolbarView setToolbarImage(Uri uri) {

        showToolbarImage();

        if(mToolbarTitle == null)
            throw new IllegalArgumentException("Toolbar image not inflated");

        if(uri == null)
            hideToolbarImage();
        else
            Glide.with(mContext).load(uri).into(mToolbarImage);

        return this;
    }

    public ToolbarView setToolbarImage(StorageReference st) {

        showToolbarImage();

        if(mToolbarTitle == null)
            throw new IllegalArgumentException("Toolbar image not inflated");

        if(st == null)
            hideToolbarImage();
        else
            Glide.with(mContext).load(st).into(mToolbarImage);

        return this;
    }

    public void onImageClickListener(OnClickListener listener) {
        mToolbarImage.setOnClickListener(listener);
    }

    public void onButtonClickListener(OnClickListener listener) {
        mToolbarButton.setOnClickListener(listener);
    }

    public void onCloseClickListener(OnClickListener listener) {
        mToolbarClose.setOnClickListener(listener);
    }

    public void onSearchViewClick(OnClickListener listener) {
        mSearchEditText.setOnClickListener(listener);
    }

    public void onSearchTextWatcher(TextWatcher listener) {
        mSearchEditText.addTextChangedListener(listener);
    }

    /**
     *
     *
     *      Private method
     *
     *
     */

    private void hideAllViews() {
        hideToolbarTitle();
        hideToolbarButton();
        hideToolbarImage();
        hideToolbarClose();
    }

    private void showToolbarTitle() {
        mToolbarTitle.setVisibility(VISIBLE);
    }

    private void hideToolbarTitle() {
        mToolbarTitle.setVisibility(GONE);
    }

    private void showToolbarButton() {
        mToolbarButton.setVisibility(VISIBLE);
    }

    private void hideToolbarButton() {
        mToolbarButton.setVisibility(GONE);
    }

    private void showToolbarImage() {
        mToolbarImage.setVisibility(VISIBLE);
    }

    private void hideToolbarImage() {
        mToolbarImage.setVisibility(GONE);
    }

    private void showToolbarClose() { mToolbarClose.setVisibility(VISIBLE); }

    private void hideToolbarClose() { mToolbarClose.setVisibility(GONE); }


}
