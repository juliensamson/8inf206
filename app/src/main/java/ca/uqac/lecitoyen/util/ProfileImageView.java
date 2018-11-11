package ca.uqac.lecitoyen.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.fragments.userUI.UserProfileFragment;
import ca.uqac.lecitoyen.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageView extends RoundedImageView {

    private final static String TAG = ProfileImageView.class.getSimpleName();

    public final static int SMALL  = 1000;
    public final static int NORMAL = 1001;
    public final static int LARGE  = 1002;

    private Activity mActivity;

    private View mRootView;
    private CircleImageView mProfileImage;

    public ProfileImageView(Context context) {
        super(context);
        create(context);
    }

    public ProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        create(context);

        /*TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Options, 0, 0);
        String titleText = a.getString(R.styleable.PrOptions_titleText);
        int valueColor = a.getColor(R.styleable.Options_valueColor,
                android.R.color.holo_blue_light);
        a.recycle();*/

    }

    public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        create(context);
    }

    private void create(Context context) {
        //mRootView = inflate(context, R.layout.custom_view_profile_image, this);
        //mProfileImage = mRootView.findViewById(R.id.view_profile_image_view);
    }

    public ProfileImageView create(Activity activity) {

        //mRootView = inflate(activity, R.layout.custom_view_profile_image, this);
        mProfileImage = findViewById(R.id.view_profile_image_view);
        this.mActivity = activity;

        return this;
    }

    /**
     *
     *      Public methods
     *
     **/

    public ProfileImageView setSize(int size) {

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mProfileImage.getLayoutParams();

        switch (size) {

            case SMALL:
                params.height = (int) getResources().getDimension(R.dimen.profile_image_view_small);
                params.width = (int) getResources().getDimension(R.dimen.profile_image_view_small);
                break;
            case NORMAL:
                params.height = (int) getResources().getDimension(R.dimen.profile_image_view_normal);
                params.width = (int) getResources().getDimension(R.dimen.profile_image_view_normal);
                break;
            case LARGE:
                params.height = (int) getResources().getDimension(R.dimen.profile_image_view_large);
                params.width = (int) getResources().getDimension(R.dimen.profile_image_view_large);
                break;
            default:
                Log.e(TAG, "The size sent doesn't correspond to anything");
                break;

        }
        mProfileImage.setLayoutParams(params);

        return this;
    }

    public ProfileImageView with(final String authUid, final User userFromPost) {

        if(mProfileImage == null)
            throw new IllegalArgumentException("Make sure to use method Create() first");

        mProfileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment fragment = UserProfileFragment.newInstance(authUid, userFromPost);
                //((MainUserActivity)mActivity).doUserProfilFragmentTransaction(fragment, true);
            }
        });

        return this;

    }

    public void load(StorageReference ref) {
        Glide.with(mActivity).load(ref).into(mProfileImage);
    }

}
