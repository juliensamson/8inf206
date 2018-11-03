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
import android.widget.ToggleButton;

import ca.uqac.lecitoyen.Interface.iToggleButton;
import ca.uqac.lecitoyen.R;

public class RepostButton extends FrameLayout implements iToggleButton {

    private static final String TAG = RepostButton.class.getSimpleName();
    private LinearLayout mRepostOn;
    private LinearLayout mRepostOff;
    private TextView mRepostCountOn;
    private TextView mRepostCountOff;

    private boolean isRepostOn = false;

    public RepostButton(@NonNull Context context) {
        super(context);
        create(context);
    }

    public RepostButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        create(context);
    }

    public RepostButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create(context);
    }

    private void create(Context context) {
        Log.d(TAG, "create");
        View rootView = inflate(context, R.layout.button_repost, this);

        mRepostOn  = rootView.findViewById(R.id.button_repost_layout_on);
        mRepostCountOn = rootView.findViewById(R.id.button_repost_count_on);

        mRepostOff = rootView.findViewById(R.id.button_repost_layout_off);
        mRepostCountOff = rootView.findViewById(R.id.button_repost_count_off);

        setButtonOff();
    }

    @Override
    public void setButtonOn() {
        Log.d(TAG, "on()");

        if(mRepostOn != null)
            mRepostOn.setVisibility(VISIBLE);

        if(mRepostOff != null)
            mRepostOff.setVisibility(GONE);

        isRepostOn = true;

    }

    @Override
    public void setButtonOff() {
        Log.d(TAG, "off()");
        if(mRepostOn != null)
            mRepostOn.setVisibility(GONE);

        if(mRepostOff != null)
            mRepostOff.setVisibility(VISIBLE);

        isRepostOn = false;

    }

    @Override
    public void setButtonCount(long count) {

        if(mRepostCountOn != null)
            mRepostCountOn.setText(String.valueOf(count));

        if(mRepostCountOff != null)
            mRepostCountOff.setText(String.valueOf(count));

    }

    @Override
    public boolean isButtonOn() {
        return isRepostOn;
    }

}
