package ca.uqac.lecitoyen.buttons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import ca.uqac.lecitoyen.Interface.iToggleButton;

public class InterestButton extends FrameLayout implements iToggleButton {


    public InterestButton(@NonNull Context context) {
        super(context);
    }

    public InterestButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterestButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void setButtonOn() {

    }

    @Override
    public void setButtonOff() {

    }

    @Override
    public void setButtonCount(long count) {

    }

    @Override
    public boolean isButtonOn() {
        return false;
    }
}
