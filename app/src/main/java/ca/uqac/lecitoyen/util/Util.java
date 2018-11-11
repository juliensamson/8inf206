package ca.uqac.lecitoyen.util;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.PostHistory;


public final class Util {


    private ProgressDialog mProgressDialog;

    private Util() {}

    public static String setDisplayTime(Context context, long time) {

        long timeElapse = System.currentTimeMillis() - time;

        String timeDisplayed;

        if (timeElapse < Constants.MINUTE) {

            timeDisplayed = String.valueOf( timeElapse / Constants.SECOND );

            return timeDisplayed + context.getResources().getString(R.string.time_short_second);

        }
        else if (timeElapse < Constants.HOUR && timeElapse >= Constants.MINUTE) {

            timeDisplayed = String.valueOf( timeElapse / Constants.MINUTE );

            return timeDisplayed + context.getResources().getString(R.string.time_short_minute);

        }
        else if (timeElapse < Constants.DAY && timeElapse >= Constants.HOUR ) {

            timeDisplayed = String.valueOf( timeElapse / Constants.HOUR );

            return timeDisplayed + context.getResources().getString(R.string.time_short_hour);

        }
        else if (timeElapse >= Constants.DAY ) {

            Date date = new Date(time) ;

            timeDisplayed = new SimpleDateFormat(
                    context.getResources().getString(R.string.short_date_format_without_year),
                    Locale.CANADA_FRENCH)
                    .format(date);

            return timeDisplayed;

        } else {

            Date date = new Date(time) ;

            timeDisplayed = new SimpleDateFormat(
                    context.getResources().getString(R.string.short_date_format_with_year),
                    Locale.CANADA_FRENCH)
                    .format(date);

            return timeDisplayed;

        }

    }

    public static int getToolbarHeight(Context context) {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public static int getScreenSize(Context context) {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public static String setStringPlurial(int size, String word) {

        if (size == 0 || size == 1 )
            return Integer.toString(size) + Constants.SPACE + " " + word + " ";
        else
            return Integer.toString(size) + Constants.SPACE + " " + word + Constants.PLURIAL + " ";

    }

    public static void checkLongDelay(long startDelay) {
        //if(System.currentTimeMillis() - startDelay > 5000) {
        //    return
        //}
    }



    /**
     *
     *      Button
     *
     */



    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
