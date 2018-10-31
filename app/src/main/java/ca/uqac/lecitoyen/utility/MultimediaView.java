package ca.uqac.lecitoyen.utility;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.R;

public class MultimediaView extends CardView {



    private MultimediaView mMultimediaView;
    private Context mContext;

    private View mRootView;
    private ImageView mPicture;
    private LinearLayout mBottomLayout;
    private ImageView mIconType;
    private TextView mTitle;
    private TextView mLink;
    private TextView mRemove;

    public MultimediaView(Context context) {
        super(context);
        this.mContext = context;
        init(context);
    }

    public MultimediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context);
    }

    public MultimediaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        mRootView = inflate(context, R.layout.layout_multimedia, this);
        mRootView.setVisibility(GONE);

        mRemove  = mRootView.findViewById(R.id.multimedia_remove);
        mPicture = mRootView.findViewById(R.id.multimedia_picture);
        mBottomLayout = mRootView.findViewById(R.id.multimedia_bottom_layout);
        mIconType = mRootView.findViewById(R.id.multimedia_icon_type);
        mTitle   = mRootView.findViewById(R.id.multimedia_title);
        mLink    = mRootView.findViewById(R.id.multimedia_link);

        mRootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to another acivity
            }
        });

    }


    public void setMultimediaImage(Uri uri) {
        mRootView.setVisibility(VISIBLE);
        mPicture.setVisibility(VISIBLE);
        mRemove.setVisibility(GONE);
        mBottomLayout.setVisibility(GONE);
        Glide.with(mContext).load(uri).into(mPicture);
    }
    public void setMultimediaImage(StorageReference storageReference) {
        mRootView.setVisibility(VISIBLE);
        mPicture.setVisibility(VISIBLE);
        mRemove.setVisibility(GONE);
        mBottomLayout.setVisibility(GONE);
        Glide.with(mContext).load(storageReference).into(mPicture);
    }

    public void setMultimediaMusic(String title) {

        mRootView.setVisibility(VISIBLE);
        mRemove.setVisibility(GONE);
        mBottomLayout.setVisibility(VISIBLE);
        mTitle.setVisibility(VISIBLE);
        mLink.setVisibility(GONE);
        mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));
        mTitle.setText(title);

        //SET visible if there is an image
    }

    public void setMultimediaLink(String title, String link) {
        mRemove.setVisibility(GONE);
        mBottomLayout.setVisibility(VISIBLE);
        mTitle.setVisibility(VISIBLE);
        mLink.setVisibility(VISIBLE);
        mIconType.setImageDrawable(getResources().getDrawable(R.drawable.ic_link_black_24dp));
        mTitle.setText(title);
        mLink.setText(link);
    }


}
