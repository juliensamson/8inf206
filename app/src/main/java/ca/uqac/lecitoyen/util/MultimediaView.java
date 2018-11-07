package ca.uqac.lecitoyen.util;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;

import ca.uqac.lecitoyen.R;

public class MultimediaView extends FrameLayout {



    private MultimediaView mMultimediaView;
    private Context mContext;

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

        setAllLayoutGone();
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

    public void setMultimediaImage(Uri uri) {
        setPictureLayout(uri, null);
        setMediaUsed(true, false, false);
    }

    public void setMultimediaImage(StorageReference storageReference) {
        setPictureLayout(null, storageReference);
        setMediaUsed(true, false, false);
    }

    public void setMultimediaMusic(String title) {
        setBottomTextLayout(R.drawable.ic_play_circle_outline_black_24dp, title, "");
        setMediaUsed(false, true, false);
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

    /**
     *
     *
     *      Private access only by the class
     *
     *
     */

    private void setAllLayoutGone() {
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
        else
            mRemove.setVisibility(VISIBLE);
    }

    private void setPictureLayout(Uri uri, StorageReference stReference) {
        removeBottomTextLayout();
        mRootView.setVisibility(VISIBLE);
        mPicture.setVisibility(VISIBLE);
        if(uri != null)
            Glide.with(mContext).load(uri).into(mPicture);
        if(stReference != null)
            Glide.with(mContext).load(stReference).into(mPicture);
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
            mBottomLayout.setBackground(getResources().getDrawable(R.drawable.shape_corner));
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