package ca.uqac.lecitoyen.utility;

import android.app.Activity;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;

public class TimeUtility {

    private static String TAG = "TimeUtility";

    final private static long second = 1000;
    final private static long minute = 60 * second;
    final private static long hour = 60 * minute;
    final private static long day = 24 * hour;

    private Context mContext;


    public TimeUtility(Context context) {
        this.mContext = context;
    }

    public String getTimeDifference(final Post currentPost) {

        String textBeforeAgo = mContext.getResources().getString(R.string.text_time_ago) + " ";
        String textBeforeThe  = mContext.getResources().getString(R.string.text_time_the) + " ";
        String textSecond = mContext.getResources().getString(R.string.time_short_second);
        String textMinute = mContext.getResources().getString(R.string.time_short_minute);
        String textHour   = mContext.getResources().getString(R.string.time_short_hour);

        long postTime = currentPost.getDate();
        long timeElapse = System.currentTimeMillis() - postTime;

        Date postDate = new Date(postTime) ;

        String timeDisplayed;
        if(timeElapse < minute)
        {
            timeDisplayed = String.valueOf(timeElapse / second);
            return textBeforeAgo + timeDisplayed + textSecond;
        }
        else if(timeElapse >= minute && timeElapse < hour)
        {
            timeDisplayed = String.valueOf(timeElapse / minute);
            return textBeforeAgo = timeDisplayed + textMinute;
        }
        else if(timeElapse >= hour && timeElapse < day)
        {
            timeDisplayed = String.valueOf(timeElapse / hour);
            return textBeforeAgo + timeDisplayed + textHour;
        }
        else
        {
            timeDisplayed = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.short_date_format),
                    Locale.CANADA_FRENCH)
                    .format(postDate);
            return textBeforeThe + timeDisplayed;
        }
    }

}
