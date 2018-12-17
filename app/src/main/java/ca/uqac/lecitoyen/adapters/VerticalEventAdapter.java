package ca.uqac.lecitoyen.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.helpers.CustomLinearLayoutManager;
import ca.uqac.lecitoyen.models.Event;
import ca.uqac.lecitoyen.util.Util;


public class VerticalEventAdapter extends RecyclerView.Adapter<VerticalEventAdapter.ViewHolder> {

    private static final String TAG = VerticalEventAdapter.class.getSimpleName();

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;

        RecyclerView mRecyclerView;
        HorizontalEventAdapter horizontalAdapter;
        LinearLayoutManager linearLayoutManager;

        public ViewHolder(View itemView) {
            super(itemView);

            mRecyclerView = itemView.findViewById(R.id.adapter_event_horizontal_recycler_view);
            mRecyclerView.setHasFixedSize(true);
            linearLayoutManager = new LinearLayoutManager(mUserActivity);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mRecyclerView.setLayoutManager(linearLayoutManager);

            horizontalAdapter =  new HorizontalEventAdapter(mUserActivity);
            mRecyclerView.setAdapter(horizontalAdapter);

            mRecyclerView.setNestedScrollingEnabled(false);

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);

            date = itemView.findViewById(R.id.adapter_event_date);

            //  Horizontal Recycler View
        }

        public void setData(ArrayList<Event> events) {
            horizontalAdapter.updateList(events);
        }

    }

    private MainUserActivity mUserActivity;
    private String mDateRange;
    private ArrayList<ArrayList<Event>> mEventsList = new ArrayList<>();
    CustomLinearLayoutManager llm;

    public VerticalEventAdapter(MainUserActivity activity,
                                ArrayList<ArrayList<Event>> events) {
        this.mUserActivity = activity;
        this.mEventsList = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.e(TAG, "ViewType " + viewType);
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_vertical_event, parent, false);
        return new VerticalEventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Log.e(TAG, "Position " + position);

        //less than a week
        if (holder.getAdapterPosition() == 0) {
            holder.date.setText("La prochaine semaine");
        }
        if (holder.getAdapterPosition() == 1) {
            holder.date.setText("Le prochain mois");
        }
        if (holder.getAdapterPosition() == 2) {
            holder.date.setText("Dans la prochaine année");
        }

        holder.setData(mEventsList.get(holder.getAdapterPosition()));


    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }
}
