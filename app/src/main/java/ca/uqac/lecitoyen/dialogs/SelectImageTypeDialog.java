package ca.uqac.lecitoyen.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.util.ImageHandler;

public class SelectImageTypeDialog implements View.OnClickListener, BottomSheetDialog.OnDismissListener{

    private static final String TAG = SelectImageTypeDialog.class.getSimpleName();

    private static final int REQUEST_CAMERA_CODE= 111;
    private static final int REQUEST_GALLERY_CODE = 222;
    private static final int DELETE_REQUEST_CODE = 0;

    private ImageHandler mImageHandler;
    private Fragment mFragment;
    private Activity mActivity;

    private BottomSheetDialog mBottomSheetDialog;
    private View mSheetView;

    private FrameLayout mCamera, mGallery, mSeeMore, mDelete;

    public SelectImageTypeDialog(Activity activity) {
        this.mActivity = activity;
        this.mImageHandler = ImageHandler.getInstance();

        if(mBottomSheetDialog == null)
            mBottomSheetDialog = new BottomSheetDialog(mActivity);

    }

    public SelectImageTypeDialog create() {
        View rootView = View.inflate(mActivity, R.layout.dialog_bottom_camera, null);

        rootView.findViewById(R.id.dialog_bottom_gallery).setOnClickListener(this);
        mCamera = rootView.findViewById(R.id.dialog_bottom_camera);
        mGallery = rootView.findViewById(R.id.dialog_bottom_gallery);
        mSeeMore = rootView.findViewById(R.id.dialog_bottom_see_more);


        mBottomSheetDialog.setContentView(rootView);
        return this;
    }

    public void show() {
        mBottomSheetDialog.show();
    }

    public void dismiss() {mBottomSheetDialog.dismiss();}

    public SelectImageTypeDialog OnCameraClickListener(View.OnClickListener listener) {
        mCamera.setOnClickListener(listener);
        return this;
    }

    public SelectImageTypeDialog OnGalleryClickListener(View.OnClickListener listener) {
        mGallery.setOnClickListener(listener);
        return this;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.dialog_bottom_gallery:
                Log.d(TAG, "Gallery called");
                mImageHandler.openGallery(mActivity);
                break;

        }
    }
}
