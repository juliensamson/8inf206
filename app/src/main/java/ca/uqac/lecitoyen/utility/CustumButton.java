package ca.uqac.lecitoyen.utility;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapter.PublicationAdapter;

public class CustumButton {

    private static float SELECT_TRANSPARENCE = 0.89f;
    private static float UNSELECT_TRANSPARENCE = 0.54f;
    private boolean isRepost = false;
    private boolean isUpvote = false;

    Context mContext;

    public CustumButton(Context context) {
        this.mContext = context;
    }

}
