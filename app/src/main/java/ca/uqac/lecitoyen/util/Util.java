package ca.uqac.lecitoyen.util;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.PostHistory;


public final class Util {


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


    /**
     *
     *      Button
     *
     */



    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
