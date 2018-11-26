package ca.uqac.lecitoyen.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.models.Audio;
import ca.uqac.lecitoyen.util.Util;

public class MultimediaView extends FrameLayout {

    private final static String TAG = MultimediaView.class.getSimpleName();

    public class loadImages extends android.support.v7.widget.AppCompatImageView {

        public loadImages(Context context) {
            super(context);
            createImageView(context);
        }

        public loadImages(Context context, AttributeSet attrs) {
            super(context, attrs);
            createImageView(context);
        }

        public loadImages(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            createImageView(context);
        }

        private void createImageView(Context context) {
            //Set all element of view
            mRemove  = mRootView.findViewById(R.id.multimedia_remove);
            mPicture = mRootView.findViewById(R.id.multimedia_picture);
        }

    }

    private MultimediaView mMultimediaView;
    private Context mContext;
    private Activity mActivity;

    private View mRootView;
    private RoundedImageView mPicture;
    private LinearLayout mBottomLayout;
    private ImageView mIconType;
    private TextView mTitle;
    private TextView mLink;
    private TextView mRemove;

    private boolean isEditable = false;
    private boolean isPhoto = false;
    private boolean isAudio = false;
    private boolean isLink  = false;

    public MultimediaView(Context context, Boolean isEditable) {
        super(context);
        this.mContext = context;
        this.isEditable = isEditable;
        inflateView(context);
    }

    public MultimediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        inflateView(context);
    }

    public MultimediaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        inflateView(context);
    }

    private void inflateView(Context context) {
        mRootView = inflate(context, R.layout.layout_multimedia, this);
        //Set all element of view
        mRemove  = mRootView.findViewById(R.id.multimedia_remove);
        mPicture = mRootView.findViewById(R.id.multimedia_picture);
        mBottomLayout = mRootView.findViewById(R.id.multimedia_bottom_layout);
        mIconType = mRootView.findViewById(R.id.multimedia_icon_type);
        mTitle   = mRootView.findViewById(R.id.multimedia_title);
        mLink    = mRootView.findViewById(R.id.multimedia_link);

        hideAllLayoutGone();
        isFrameEditable();

        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to another acivity
            }
        });

    }

    /**
     *
     *
     *      Public methods
     *
     *
     */

    public MultimediaView with(Activity activity) {
        mActivity = activity;
        return this;
    }

    public MultimediaView loadImages(Uri uri, String title) {

        isFrameEditable();

        if(!title.isEmpty())
            mTitle.setText(title);
        else
            hideAllLayoutGone();

        setPictureLayout(uri, null, null);
        setMediaUsed(true, false, false);

        return this;
    }


    public MultimediaView loadImages(File file, String title) {

        isFrameEditable();

        if(!title.isEmpty())
            mTitle.setText(title);
        else
            hideAllLayoutGone();

        setPictureLayout(null, null, null);
        setMediaUsed(true, false, false);

        Glide.with(mContext).load(file).into(mPicture);

        return this;
    }


    public MultimediaView loadImages(Bitmap bitmap, String title) {

        isFrameEditable();

        if(!title.isEmpty())
            mTitle.setText(title);
        else
            hideAllLayoutGone();

        setPictureLayout(null, bitmap, null);
        setMediaUsed(true, false, false);

        return this;
    }

    public MultimediaView loadImages(StorageReference storageReference) {

        setPictureLayout(null, null, storageReference);
        setMediaUsed(true, false, false);

        return this;
    }

    public MultimediaView loadAudio(Audio audio) {
        
        setBottomTextLayout(R.drawable.ic_play_circle_outline_black_24dp, audio.getTitle(), "");
        setMediaUsed(false, true, false);

        return this;
    }

    public void setMultimediaLink(String title, String link) {
        setBottomTextLayout(R.drawable.ic_link_black_24dp, title, link);
        setMediaUsed(false, false, true);
    }

    public boolean isPhoto() {
        return isPhoto;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public boolean isLink() {
        return isLink;
    }

    public MultimediaView setEditable(boolean isEditable) {
        this.isEditable = isEditable;
        return this;
    }

    public boolean isEditable() {
        return this.isEditable;
    }


    public MultimediaView setFrameSize() {
        LayoutParams params = (LayoutParams) mPicture.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.width  = ( Util.getScreenWidthPixel(mActivity) * 75 ) / 100 ;
        params.height = ( Util.getScreenHeightPixel(mActivity) * 75 ) / 100;
        mPicture.setLayoutParams(params);
        return this;
    }

    public MultimediaView setFullHeight() {
        mPicture.requestLayout();
        LayoutParams params = (LayoutParams) mPicture.getLayoutParams();
        params.height = mPicture.getMaxHeight();
        mPicture.setLayoutParams(params);

        return this;
    }

    public MultimediaView setMinimumHeight() {
        LayoutParams params = (LayoutParams) mPicture.getLayoutParams();
        params.height = mPicture.getMinimumHeight();
        mPicture.setLayoutParams(params);

        return this;
    }

    public MultimediaView setCustumHeight(int pixelHeight) {
        LayoutParams params = (LayoutParams) mPicture.getLayoutParams();
        params.height = pixelHeight;
        mPicture.setLayoutParams(params);
        return this;
    }

    /**
     *
     *
     *      Private access only by the class
     *
     *
     */

    private void hideAllLayoutGone() {
        mRootView.setVisibility(GONE);
        mRemove.setVisibility(GONE);
        mPicture.setVisibility(GONE);
        mBottomLayout.setVisibility(GONE);
        mIconType.setVisibility(GONE);
        mTitle.setVisibility(GONE);
        mLink.setVisibility(GONE);
    }

    private void isFrameEditable() {
        if(!isEditable)
            mRemove.setVisibility(GONE);
        else {
            mRemove.setVisibility(VISIBLE);
            mRemove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRootView.setVisibility(GONE);
                    mPicture.setVisibility(GONE);
                }
            });
        }
    }

    private void setPictureLayout(Uri uri, Bitmap bitmap, StorageReference stReference) {
        mRootView.setVisibility(VISIBLE);
        mPicture.setVisibility(VISIBLE);
        if(uri != null)
            Glide.with(mContext).load(uri).into(mPicture);
        if(stReference != null)
            Glide.with(mContext).load(stReference).into(mPicture);
        if(bitmap != null)
            Glide.with(mContext).load(bitmap).into(mPicture);
    }

    private void setBottomTextLayout(int resId, String title, String link) {
        mRootView.setVisibility(VISIBLE);
        mBottomLayout.setVisibility(VISIBLE);
        mIconType.setVisibility(VISIBLE);
        mIconType.setImageResource(resId);

        if(mPicture.getVisibility() != GONE) {
            //mPicture.setVisibility(VISIBLE);
            mBottomLayout.setBackground(getResources().getDrawable(R.drawable.shape_corner_bottom));
        } else {
            //mPicture.setVisibility(GONE);
            mBottomLayout.setBackground(getResources().getDrawable(R.drawable.edit_text_search));
        }

        if(!title.isEmpty()) {
            //do something
            mTitle.setVisibility(VISIBLE);
            mTitle.setText(title);
        } else {
            mTitle.setVisibility(GONE);
        }

        if(!link.isEmpty()) {
            mLink.setVisibility(VISIBLE);
            mLink.setText(link);
        } else {
            mLink.setVisibility(GONE);
        }
    }

    private void removeBottomTextLayout() {
        mBottomLayout.setVisibility(GONE);
        mIconType.setVisibility(GONE);
        mTitle.setVisibility(GONE);
        mLink.setVisibility(GONE);
    }

    private void setMediaUsed(boolean isPhoto, boolean isAudio, boolean isLink) {
        this.isPhoto = isPhoto;
        this.isAudio = isAudio;
        this.isLink  = isLink;
    }

}
