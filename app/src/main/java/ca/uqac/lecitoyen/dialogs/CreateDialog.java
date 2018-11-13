package ca.uqac.lecitoyen.dialogs;


import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.util.MultimediaView;
import ca.uqac.lecitoyen.util.Util;
import ca.uqac.lecitoyen.views.ToolbarView;

public class CreateDialog implements View.OnClickListener, BottomSheetDialog.OnDismissListener {

    private static final String TAG = CreateDialog.class.getSimpleName() ;

    private static final int CREATE_POST_LAYOUT = 999;
    private static final int CREATE_EVENT_LAYOUT = 1000;

    private int layoutInflated = 0;

    private Activity mActivity;
    private Fragment mFragment;

    private BottomSheetDialog mBottomSheetDialog;
    private View mSheetView;

    //  Views
    private ToolbarView mToolbar;
    private EditText mAddImageEditText;

    public CreateDialog(Fragment fragment) {

        this.mFragment = fragment;
        this.mActivity = fragment.getActivity();

        if(mBottomSheetDialog == null)
            mBottomSheetDialog = new BottomSheetDialog(mActivity);

    }

    /**
     *
     *
     *      Public methods
     *
     *
     */

    public CreateDialog createPostView(Uri uri) {
        View view = View.inflate(mActivity, R.layout.dialog_bottom_create_post, null);

        layoutInflated = CREATE_POST_LAYOUT;

        //  Views
        mToolbar = view.findViewById(R.id.create_post_toolbar);
        mToolbar.buttonToolbar(mFragment, "Publier");
        MultimediaView mMultimediaView = view.findViewById(R.id.create_post_multimedia);
        mMultimediaView.setEditable(true).loadImages(uri).setFullHeight();

        //  Button
        mToolbar.onButtonClickListener(this);
        mToolbar.onCloseClickListener(this);


        //  Set bottom sheet view
        mBottomSheetDialog.setContentView(view);
        setBottomSheetHeight(view);

        return this;
    }

    public CreateDialog createEventView() {
        View view = View.inflate(mActivity, R.layout.dialog_bottom_create_event, null);

        layoutInflated = CREATE_EVENT_LAYOUT;

        //  Views
        mToolbar = view.findViewById(R.id.create_event_toolbar);
        mToolbar.buttonToolbar(mFragment, "Créer");

        //  Button
        mToolbar.onButtonClickListener(this);
        mToolbar.onCloseClickListener(this);
        view.findViewById(R.id.create_event_add_image).setOnClickListener(this);


        //  Set bottom sheet view
        mBottomSheetDialog.setContentView(view);
        setBottomSheetHeight(view);

        return this;
    }

    public void show() {
        mBottomSheetDialog.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.toolbar_view_close:
                onDismiss(mBottomSheetDialog);
                break;
            case R.id.toolbar_view_button:
                Log.d(TAG, "create event click");
                //updateDB();
                break;
        }

        switch (layoutInflated) {
            case CREATE_EVENT_LAYOUT:
                setEventOnClick(view);
                break;
            case CREATE_POST_LAYOUT:
                setPostOnClick(view);
        }
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    /**
     *
     *
     *      Private methods
     *
     *
     */



    private void setBottomSheetHeight(View view) {
        View parentView = (View) view.getParent();
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parentView);
        bottomSheetBehavior.setPeekHeight(Util.getScreenHeightPixel(mActivity));
    }

    private void setEventOnClick(View view) {

        switch (view.getId()) {
            case R.id.create_event_add_image:
                ImageBottomDialog dialog = new ImageBottomDialog(mActivity);
                break;
        }
    }

    private void setPostOnClick(View view) {

        switch (view.getId()) {

        }
    }
}
