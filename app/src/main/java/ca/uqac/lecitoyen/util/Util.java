package ca.uqac.lecitoyen.util;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.PostHistory;


public final class Util {


    private ProgressDialog mProgressDialog;

    private Util() {}

    /**
     *
     *      Time Util
     *
     */

    public static String setEventDate(Activity activity, long time) {

        Date date = new Date(time) ;

        String sdf = new SimpleDateFormat(
                activity.getResources().getString(R.string.complete_date_format),
                Locale.CANADA_FRENCH)
                .format(date);

        return sdf;

    }

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

    public static  int getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getCurrentMonth() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
    }

    public static long getAWeekFromNow() {
        return System.currentTimeMillis() + Constants.DAY * 7;
    }

    public static long getAMonthFromNow() {
        return System.currentTimeMillis() + Constants.DAY * 30;
    }


    /**
     *
     *      Size & Screen Util
     *
     */

    //  params.height = (int) getResources().getDimension(R.dimen.profile_image_view_small);
    //params.width = (int) getResources().getDimension(R.dimen.profile_image_view_small);

    public static int getToolbarHeight(Context context) {
        int[] attrs = new int[] {R.attr.actionBarSize};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        int toolBarHeight = ta.getDimensionPixelSize(0, -1);
        ta.recycle();
        return toolBarHeight;
    }

    public static int getImageHeight(Activity activity, Bitmap bitmap) {

        /*Bitmap bitmapOrg = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_launcher);

        bitmap = bitmapOrg;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        long lengthbmp = imageInByte.length;
        long heightBmp = imageInByte.*/

        return 0;
    }


    public static int getScreenHeightPixel(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int getScreenWidthPixel(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getImageHeight(Activity activity, int imageHeight) {
        int scale = imageHeight / getScreenHeightPixel(activity);
        return imageHeight / scale;
    }


    public static int getImageWidth(Activity activity, int imageWidth) {
        int scale = imageWidth / getScreenWidthPixel(activity);
        return imageWidth / scale;
    }


    public static int getRandomNumber() {
        return (int)(Math.random() * 1000 + 1);
    }

    /**
     *
     *      String util
     *
     */

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
