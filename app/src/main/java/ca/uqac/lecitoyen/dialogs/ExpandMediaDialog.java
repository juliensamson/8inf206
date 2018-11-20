package ca.uqac.lecitoyen.dialogs;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.util.Util;
import ca.uqac.lecitoyen.views.ToolbarView;
import nl.changer.audiowife.AudioWife;

public class ExpandMediaDialog {

    private static final String TAG = ExpandMediaDialog.class.getSimpleName();

    private Activity mActivity;

    private BottomSheetDialog bottomSheetDialog;

    private ToolbarView toolbarView;
    private FrameLayout mainLayout, audioPlayer;
    private ImageView imageView;

    public ExpandMediaDialog(Activity activity) {
        this.mActivity = activity;
        this.bottomSheetDialog = new BottomSheetDialog(mActivity);
    }

    public ExpandMediaDialog create() {

        Log.d(TAG, "create");
        View rootView = View.inflate(mActivity, R.layout.dialog_bottom_expand_media, null);

        toolbarView = rootView.findViewById(R.id.expand_post_media_toolbar);
        toolbarView.simpleToolbar(mActivity, "");
        toolbarView.onCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        mainLayout = rootView.findViewById(R.id.expand_post_media_layout);
        imageView = rootView.findViewById(R.id.expand_post_media_picture);
        audioPlayer = rootView.findViewById(R.id.expand_post_media_mediaplayer);

        bottomSheetDialog.setContentView(rootView);

        View parent =  (View) rootView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        behavior.setPeekHeight(Util.getScreenHeightPixel(mActivity));

        return this;
    }

    public ExpandMediaDialog withImage(StorageReference ref) {
        Glide.with(mActivity)
                .load(ref)
                .into(imageView);
        return this;
    }

    public ExpandMediaDialog withAudio(StorageReference ref) {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                AudioWife.getInstance().useDefaultUi(audioPlayer, mActivity.getLayoutInflater())
                        .init(mActivity, uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
        return this;
    }

    public void show() {
        bottomSheetDialog.show();
    }

}
