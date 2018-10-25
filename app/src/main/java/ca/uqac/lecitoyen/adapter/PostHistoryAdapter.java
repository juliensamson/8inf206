package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.PostHistory;

public class PostHistoryAdapter extends RecyclerView.Adapter<PostHistoryAdapter.ViewHolder> {

    final private static String TAG = "PostHistoryAdapter";

    private static long second = 1000;
    private static long minute = 60 * second;
    private static long hour = 60 * minute;
    private static long day = 24 * hour;

    private Context mContext;

    private ArrayList<PostHistory> mPostHistory;

    public PostHistoryAdapter(Context context, ArrayList<PostHistory> postHistory) {
        this.mContext = context;
        this.mPostHistory = postHistory;
    }

    @NonNull
    @Override
    public PostHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_post_history, parent, false);
        return new PostHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHistoryAdapter.ViewHolder holder, int position) {
        final PostHistory currentPostHistory = mPostHistory.get(holder.getAdapterPosition());
        holder.number.setText(getHistoryNumber(currentPostHistory));
        holder.date.setText(getTimeElapseSincePost(currentPostHistory));
        holder.post.setText(currentPostHistory.getPost());
    }

    @Override
    public int getItemCount() {
        return mPostHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView number, date, post;

        public ViewHolder(View itemView) {
            super(itemView);
            //  View
            number = itemView.findViewById(R.id.listview_post_history_number);
            date = itemView.findViewById(R.id.listview_post_history_date);
            post = itemView.findViewById(R.id.listview_post_history_original);
        }
    }

    private String getHistoryNumber(final PostHistory postHistory) {
        String textOriginal = mContext.getResources().getString(R.string.post_history_original);
        String textModif    = mContext.getResources().getString(R.string.post_history_number_modify);
        int num = postHistory.getModifcationNumber();

        if(num == 0)
            return textOriginal;
        else
            return textModif + " " + num;
    }

    @NonNull
    private String getTimeElapseSincePost(final PostHistory postHistory) {

        String textBeforeAgo = mContext.getResources().getString(R.string.text_time_ago) + " ";
        String textBeforeThe  = mContext.getResources().getString(R.string.text_time_the) + " ";
        String textBeforeAt  = mContext.getResources().getString(R.string.text_time_at);
        String textSecond = mContext.getResources().getString(R.string.time_short_second);
        String textMinute = mContext.getResources().getString(R.string.time_short_minute);
        String textHour   = mContext.getResources().getString(R.string.time_short_hour);

        long postTime = postHistory.getModificationTimestamp();
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
            return textBeforeAgo + timeDisplayed + textMinute;
        }
        else if(timeElapse >= hour && timeElapse < day)
        {
            timeDisplayed = String.valueOf(timeElapse / hour);
            return textBeforeAgo + timeDisplayed + textHour;
        }
        else
        {
            String dateDisplayed = new SimpleDateFormat(
                mContext.getResources().getString(R.string.short_date_format),
                Locale.CANADA_FRENCH)
                .format(postDate);
            timeDisplayed = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.time_format),
                    Locale.CANADA_FRENCH)
                    .format(postDate);
            return textBeforeThe + dateDisplayed + textBeforeAt + timeDisplayed;
        }
    }

    private void comparePostModification() {

    }
}
