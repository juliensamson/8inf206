package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.PostModification;

public class PostHistoryAdapter extends RecyclerView.Adapter<PostHistoryAdapter.ViewHolder> {

    final private static String TAG = "PostHistoryAdapter";
    final private static String dateFormat = "dd MMM yyyy";
    final private static String timeFormat = "HH:MM:ss";
    final private static long second = 1000;
    final private static long minute = 60 * 1000;
    final private static long hour = 60 * 60 * 1000;
    final private static long day = 24 * 60 * 60 * 1000;


    private Context mContext;

    private ArrayList<PostModification> mPostHistory;

    public PostHistoryAdapter(Context context, ArrayList<PostModification> postHistory) {
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
        final PostModification currentPostHistory = mPostHistory.get(holder.getAdapterPosition());
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

    private String getHistoryNumber(final PostModification postHistory) {
        int num = postHistory.getModifcationNumber();
        if(num == 0)
            return "Original";
        else
            return "#" + num + " modifié";
    }


    private String getTimeElapseSincePost(final PostModification postHistory) {

        long postTime = postHistory.getModificationTimestamp();
        long timeElapse = System.currentTimeMillis() - postTime;

        Date postDate = new Date(postTime) ;

        String timeDisplayed;
        if(timeElapse < minute)
        {
            timeDisplayed = String.valueOf(timeElapse / second);
            return "il y a " + timeDisplayed + "s";
        }
        else if(timeElapse >= minute && timeElapse < hour)
        {
            timeDisplayed = String.valueOf(timeElapse / minute);
            return "il y a " + timeDisplayed + "m";
        }
        else if(timeElapse >= hour && timeElapse < day)
        {
            timeDisplayed = String.valueOf(timeElapse / hour);
            return "il y a " + timeDisplayed + "h";
        }
        else
        {
            String dateDisplayed = new SimpleDateFormat(
                dateFormat,
                Locale.CANADA_FRENCH)
                .format(postDate);
            timeDisplayed = new SimpleDateFormat(
                    timeFormat,
                    Locale.CANADA_FRENCH)
                    .format(postDate);
            return "Le " + dateDisplayed + " à " + timeDisplayed;
        }
    }
}
