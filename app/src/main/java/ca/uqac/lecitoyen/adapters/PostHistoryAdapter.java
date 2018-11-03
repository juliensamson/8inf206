package ca.uqac.lecitoyen.adapters;

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
import ca.uqac.lecitoyen.models.PostHistory;
import ca.uqac.lecitoyen.util.Util;

public class PostHistoryAdapter extends RecyclerView.Adapter<PostHistoryAdapter.ViewHolder> {

    final private static String TAG = PostHistoryAdapter.class.getSimpleName();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView number, date, post;

        public ViewHolder(View itemView) {
            super(itemView);
            //  View
            number = itemView.findViewById(R.id.listview_post_history_number);
            date = itemView.findViewById(R.id.listview_post_history_date);
            post = itemView.findViewById(R.id.listview_post_history_original);
        }
    }

    private Context mContext;

    private ArrayList<PostHistory> mHistories = new ArrayList<>();

    public PostHistoryAdapter(Context context, ArrayList<PostHistory> histories) {
        this.mContext = context;
        this.mHistories = histories;
    }

    @NonNull
    @Override
    public PostHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.listview_post_history, parent, false);
        return new PostHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHistoryAdapter.ViewHolder holder, int position) {

        final PostHistory holderPostHistory = mHistories.get(holder.getAdapterPosition());

        if(holderPostHistory != null) {

            holder.number.setText(getHistoryNumber(holderPostHistory.getModifcationNumber()));

            holder.date.setText(Util.setDisplayTime(mContext, holderPostHistory.getModificationTimestamp()));

            if(holderPostHistory.getPost() != null && !holderPostHistory.getPost().isEmpty())
                holder.post.setText(holderPostHistory.getPost());

        }
    }

    @Override
    public int getItemCount() {
        return mHistories.size();
    }

    private String getHistoryNumber(long number) {

        if(number == 0)
            return mContext.getResources().getString(R.string.dialog_post_history_post_original);
        else
            return mContext.getResources().getString(R.string.dialog_post_history_post_number) + " " + number;

    }
}
