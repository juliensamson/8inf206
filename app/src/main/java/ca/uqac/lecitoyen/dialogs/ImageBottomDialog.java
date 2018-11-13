package ca.uqac.lecitoyen.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.FrameLayout;

import ca.uqac.lecitoyen.R;

public class ImageBottomDialog implements BottomSheetDialog.OnDismissListener {

    private static final int REQUEST_CAMERA_CODE= 111;
    private static final int REQUEST_GALLERY_CODE = 222;
    private static final int DELETE_REQUEST_CODE = 0;

    Activity mActivity;

    private BottomSheetDialog mBottomSheetDialog;
    private View mSheetView;

    private FrameLayout mCamera, mGallery, mSeeMore, mDelete;

    public ImageBottomDialog(Activity activity) {

        inflate(activity);

        if(mBottomSheetDialog == null)
            mBottomSheetDialog = new BottomSheetDialog(activity);

        mBottomSheetDialog.setContentView(mSheetView);
        mBottomSheetDialog.show();
    }

    public void cameraOnClickListener(View.OnClickListener listener) {
        mCamera.setOnClickListener(listener);
    }

    public void galleryOnClickListener() {
        mGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    public void seeMoreOnClickListener(View.OnClickListener listener) {
        mSeeMore.setOnClickListener(listener);
    }

    public void deleteOnClickListener(View.OnClickListener listener) {
        mDelete.setOnClickListener(listener);
    }


    public ImageBottomDialog hideOptionCamera() {
        mCamera.setVisibility(View.GONE);
        return this;
    }

    public ImageBottomDialog hideOptionGallery() {
        mGallery.setVisibility(View.GONE);
        return this;
    }

    public ImageBottomDialog hideOptionSeeMore() {
        mSeeMore.setVisibility(View.GONE);
        return this;
    }

    public ImageBottomDialog hideOptionDelet() {
        mDelete.setVisibility(View.GONE);
        return this;
    }


    private void inflate(Activity activity) {
        mActivity = activity;
        mSheetView = activity.getLayoutInflater().inflate(R.layout.dialog_bottom_camera, null);
        mCamera = mSheetView.findViewById(R.id.dialog_bottom_camera);
        mGallery = mSheetView.findViewById(R.id.dialog_bottom_gallery);
        mSeeMore = mSheetView.findViewById(R.id.dialog_bottom_see_more);
        mDelete = mSheetView.findViewById(R.id.dialog_bottom_delete);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
       mBottomSheetDialog = null;
       mSheetView = null;
       mCamera = null;
       mGallery = null;
       mSeeMore = null;
       mDelete = null;
    }

    private void openGallery() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        if (openGalleryIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            openGalleryIntent.setType("image/*");
            mActivity.startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);
        }
    }
}
