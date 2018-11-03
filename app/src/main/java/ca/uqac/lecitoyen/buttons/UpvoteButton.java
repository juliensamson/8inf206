package ca.uqac.lecitoyen.buttons;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import ca.uqac.lecitoyen.Interface.iToggleButton;
import ca.uqac.lecitoyen.R;

public class UpvoteButton extends FrameLayout implements iToggleButton {

    private static final String TAG = UpvoteButton.class.getSimpleName();
    private LinearLayout mUpvoteOn;
    private LinearLayout mUpvoteOff;
    private TextView mUpvoteCountOn;
    private TextView mUpvoteCountOff;

    private boolean isUpvoteOn = false;

    public UpvoteButton(@NonNull Context context) {
        super(context);
        create(context);
    }

    public UpvoteButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        create(context);
    }

    public UpvoteButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create(context);
    }

    public void create(Context context) {
        Log.d(TAG, "create");
        View rootView = inflate(context, R.layout.button_upvote, this);

        mUpvoteOn  = rootView.findViewById(R.id.button_upvote_layout_on);
        mUpvoteCountOn = rootView.findViewById(R.id.button_upvote_count_on);

        mUpvoteOff = rootView.findViewById(R.id.button_upvote_layout_off);
        mUpvoteCountOff = rootView.findViewById(R.id.button_upvote_count_off);

        setButtonOff();
    }

    @Override
    public void setButtonOn() {
        Log.d(TAG, "on()");

        if(mUpvoteOn != null)
            mUpvoteOn.setVisibility(VISIBLE);

        if(mUpvoteOff != null)
            mUpvoteOff.setVisibility(GONE);

        isUpvoteOn = true;
    }

    @Override
    public void setButtonOff() {
        Log.d(TAG, "off()");

        if(mUpvoteOn != null)
            mUpvoteOn.setVisibility(GONE);

        if(mUpvoteOff != null)
            mUpvoteOff.setVisibility(VISIBLE);

        isUpvoteOn = false;
    }

    @Override
    public void setButtonCount(long count) {
        if(mUpvoteCountOn != null)
            mUpvoteCountOn.setText(String.valueOf(count));

        if(mUpvoteCountOff != null)
            mUpvoteCountOff.setText(String.valueOf(count));

    }

    @Override
    public boolean isButtonOn() {
        return isUpvoteOn;
    }
}