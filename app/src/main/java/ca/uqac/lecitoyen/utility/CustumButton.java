package ca.uqac.lecitoyen.utility;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import ca.uqac.lecitoyen.R;

public class CustumButton extends Activity {

    private static float SELECT_TRANSPARENCE = 0.89f;
    private static float UNSELECT_TRANSPARENCE = 0.54f;
    private boolean isRepost = false;
    private boolean isUpvote = false;

    Context mContext;

    public CustumButton(Context context) {
        this.mContext = context;
    }

    public void Repost(TextView icon, TextView count) {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            if (!isRepost) {
                isRepost = true;

                icon.setBackground(getDrawable(R.drawable.ic_repost_secondary_24dp));
                count.setTextColor(getResources().getColor(R.color.i_secondary_700));

                icon.setAlpha(SELECT_TRANSPARENCE);
                count.setAlpha(SELECT_TRANSPARENCE);
            }
            else {
                isRepost = false;

                icon.setBackground(getDrawable(R.drawable.ic_repost_black_24dp));
                count.setTextColor(getResources().getColor(R.color.black_900));

                icon.setAlpha(UNSELECT_TRANSPARENCE);
                count.setAlpha(UNSELECT_TRANSPARENCE);
            }
        }
    }

    public void Repost(ImageView icon, TextView count) {
        if (!isRepost) {
            isRepost = true;

            icon.setImageResource(R.drawable.ic_repost_secondary_24dp);
            count.setTextColor(getResources().getColor(R.color.i_secondary_700));

            icon.setAlpha(SELECT_TRANSPARENCE);
            count.setAlpha(SELECT_TRANSPARENCE);
        }
        else {
            isRepost = false;

            icon.setImageResource(R.drawable.ic_repost_black_24dp);
            count.setTextColor(getResources().getColor(R.color.black_900));

            icon.setAlpha(UNSELECT_TRANSPARENCE);
            count.setAlpha(UNSELECT_TRANSPARENCE);
        }
    }
}
