package ca.uqac.lecitoyen.buttons;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ca.uqac.lecitoyen.Interface.iToggleButton;
import ca.uqac.lecitoyen.R;

public class ToggleButton extends FrameLayout implements iToggleButton {

    private static final String TAG = ToggleButton.class.getSimpleName();

    public static final int CIRCLE_BUTTON = 99;
    private static final int RIGHT = 88;
    private static final int LEFT = 88;


    private int mDrawableId;

    private FrameLayout mButtonOn, mButtonOff;
    private TextView mShapeOn, mShapeOff, mButtonTitle;
    private ImageView mIconOn, mIconOff;

    private boolean isButtonOn = false;
    private int typeSelect = 0;

    private Context mContext;

    public ToggleButton(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public ToggleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ToggleButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void create(int type) {

        switch (type) {
            case CIRCLE_BUTTON:
                typeSelect = CIRCLE_BUTTON;
                inflateEventTypeButton(mContext);
                break;
        }

    }

    /**
     *
     *      Public methods
     *
     */

    @Override
    public void setButtonOn() {

        Log.d(TAG, "ButtonOn: " + String.valueOf(isButtonOn())
                + "ButtonOff: " + String.valueOf(isButtonOn()));

        if(mButtonOn != null)
            showButtonOn();

        if(mButtonOff != null)
            hideButtonOff();

        isButtonOn = true;
    }

    @Override
    public void setButtonOff() {

        Log.d(TAG, "ButtonOn: " + String.valueOf(isButtonOn())
                + "ButtonOff: " + String.valueOf(isButtonOn()));

        if(mButtonOn != null)
            hideButtonOn();

        if(mButtonOff != null)
            showButtonOff();

        isButtonOn = false;
    }

    @Override
    public void setButtonCount(long count) {

    }

    @Override
    public boolean isButtonOn() {
        return isButtonOn;
    }

    public void setTitle(String title) {

        if(mButtonTitle != null)
            mButtonTitle.setText(title);

    }

    public String getTitle() {
        return mButtonTitle.getText().toString();
    }

    public void setCircleButtonColor(int color, int drawableId) {

        if(typeSelect != CIRCLE_BUTTON)
            throw new IllegalArgumentException("Cannot change style on a none circle button");

        if(drawableId != 0) {
            mDrawableId = drawableId;
            mIconOn.setImageResource(drawableId);
            mIconOff.setImageResource(drawableId);
        }

        if(mShapeOn != null && mIconOn != null) {

            DrawableCompat.setTint(mShapeOn.getBackground(), ContextCompat.getColor(getContext(), color));
            DrawableCompat.setTint(mIconOn.getDrawable(), ContextCompat.getColor(getContext(), R.color.white_50));
        }
        //  Color Button Off
        if(mShapeOff != null && mIconOff != null) {

            GradientDrawable drawable = (GradientDrawable)mShapeOff.getBackground();
            drawable.setStroke(8, getResources().getColor(color));
            DrawableCompat.setTint(mIconOff.getDrawable(), ContextCompat.getColor(getContext(), color));
        }
    }

    public int getButtonColor() {
        return mIconOn.getSolidColor();
    }

    public int getButtonDrawableId() {
        return mDrawableId;
    }



    public void createButton(int drawableId, int drawablePosition) {

        /*switch (drawablePosition) {
            case COUNT_RIGHT:
                setButtonCountRight();
                break;
        }*/
    }

    /**
     *
     *      Private methods
     *
     */

    private void inflateEventTypeButton(Context context) {
        View rootView = inflate(context, R.layout.button_event_type, this);

        mButtonTitle = rootView.findViewById(R.id.button_event_type_title);

        mButtonOn = rootView.findViewById(R.id.button_event_type_on_layout);
        mShapeOn = rootView.findViewById(R.id.button_event_type_on_shape);
        mIconOn = rootView.findViewById(R.id.button_event_type_on_icon);

        mButtonOff = rootView.findViewById(R.id.button_event_type_off_layout);
        mShapeOff = rootView.findViewById(R.id.button_event_type_off_shape);
        mIconOff = rootView.findViewById(R.id.button_event_type_off_icon);

        setAllViewsGone();
    }
    

    private void setButtonCountRight() {

    }

    private void setAllViewsGone() {
        hideButtonOn();
        hideButtonOff();
    }

    private void showButtonOn() {
        mButtonOn.setVisibility(VISIBLE);
    }

    private void hideButtonOn() {
        mButtonOn.setVisibility(GONE);
    }

    private void showButtonOff() {
        mButtonOff.setVisibility(VISIBLE);
    }

    private void hideButtonOff() {
        mButtonOff.setVisibility(GONE);
    }
}
