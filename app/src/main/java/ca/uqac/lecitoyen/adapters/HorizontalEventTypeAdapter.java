package ca.uqac.lecitoyen.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.buttons.EventTypeButton;
import ca.uqac.lecitoyen.buttons.ToggleButton;

public class HorizontalEventTypeAdapter extends RecyclerView.Adapter<HorizontalEventTypeAdapter.ViewHolder> {

    private static final String TAG = HorizontalEventTypeAdapter.class.getSimpleName();

    public class ViewHolder extends RecyclerView.ViewHolder {

        //private TextView mButton, mButtonTitle;
        //private ImageView mButtonIcon;

        private EventTypeButton mButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mButton = itemView.findViewById(R.id.adapter_event_type_button);
            mButton.setButtonOff();
            //mButtonIcon = itemView.findViewById(R.id.adapter_event_type_icon);
            //mButtonTitle = itemView.findViewById(R.id.adapter_event_type_title);
        }
    }

    private MainUserActivity mUserActivity;
    private ArrayList<EventTypeButton> mEventTypeButtons;
    private ArrayList<ToggleButton> mToggleButtons;

    /*public HorizontalEventTypeAdapter(MainUserActivity activity, ArrayList<EventTypeButton> eventTypeButtons) {
        this.mUserActivity = activity;
        this.mEventTypeButtons = eventTypeButtons;
    }*/

    public HorizontalEventTypeAdapter(MainUserActivity activity, ArrayList<ToggleButton> toggleButtons) {
        this.mUserActivity = activity;
        this.mToggleButtons = toggleButtons;
    }

    @NonNull
    @Override
    public HorizontalEventTypeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_event_type, parent, false);
        return new HorizontalEventTypeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HorizontalEventTypeAdapter.ViewHolder holder, int position) {

        final ToggleButton button = mToggleButtons.get(holder.getAdapterPosition());
        holder.mButton.setTitle(button.getTitle());
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "Button click " + String.valueOf(holder.mButton.isButtonOn()));

                if(!holder.mButton.isButtonOn()) {
                    holder.mButton.setButtonOn();
                } else {
                    holder.mButton.setButtonOff();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mToggleButtons.size();
    }
}
