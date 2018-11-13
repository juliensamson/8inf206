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

import org.w3c.dom.Attr;

import ca.uqac.lecitoyen.Interface.iToggleButton;
import ca.uqac.lecitoyen.R;


public class EventTypeButton extends FrameLayout implements iToggleButton {

    private static final String TAG = EventTypeButton.class.getSimpleName();

    private FrameLayout mButtonOn, mButtonOff;
    private TextView mShapeOn, mShapeOff, mButtonTitle;
    private ImageView mIconOn, mIconOff;

    private boolean isEventTypeOn = false;

    public EventTypeButton(@NonNull Context context) {
        super(context);
        inflate(context, null, 0);
    }

    public EventTypeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, attrs, 0);
    }

    public EventTypeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, attrs, defStyleAttr);
    }


    private void inflate(@NonNull Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
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

        isEventTypeOn = true;
    }

    @Override
    public void setButtonOff() {

        Log.d(TAG, "ButtonOn: " + String.valueOf(isButtonOn())
                + "ButtonOff: " + String.valueOf(isButtonOn()));

        if(mButtonOn != null)
            hideButtonOn();

        if(mButtonOff != null)
            showButtonOff();

        isEventTypeOn = false;
    }

    @Override
    public void setButtonCount(long count) {

    }

    @Override
    public boolean isButtonOn() {
        return isEventTypeOn;
    }

    public void setTitle(String title) {

        if(mButtonTitle != null)
            mButtonTitle.setText(title);

    }

    public String getTitle() {
        return mButtonTitle.getText().toString();
    }

    public void setButtonStyle(int color) {
        //  Color Button On
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

    public void setColorButtonOn(int color) {
        DrawableCompat.setTint(mShapeOn.getBackground(), ContextCompat.getColor(getContext(), color));
    }

    public void setColorButtonOff(int color, int drawable) {
        DrawableCompat.setTint(mShapeOn.getBackground(), ContextCompat.getColor(getContext(), color));
    }

    /**
     *
     *      Private methods
     *
     */

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
