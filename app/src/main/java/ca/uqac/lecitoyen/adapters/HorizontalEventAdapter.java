package ca.uqac.lecitoyen.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Event;
import ca.uqac.lecitoyen.util.Util;

public class HorizontalEventAdapter extends RecyclerView.Adapter<HorizontalEventAdapter.ViewHolder> {

    private static final String TAG = HorizontalEventAdapter.class.getSimpleName();

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView mainLayout;
        TextView title, date, location, attendeesCount;
        ImageView image, price, type;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.adapter_event_title);
            date  = itemView.findViewById(R.id.adapter_event_date);
            location = itemView.findViewById(R.id.adapter_event_location);
            attendeesCount = itemView.findViewById(R.id.adapter_event_attendees_count);

            image = itemView.findViewById(R.id.adapter_event_image);
            price = itemView.findViewById(R.id.adapter_event_price);
            type  = itemView.findViewById(R.id.adapter_event_type);

            mainLayout = itemView.findViewById(R.id.adapter_event_main_layout);
        }
    }

    private Context mContext;
    private MainUserActivity mUserActivity;
    private DatabaseManager dbManager;

    private ArrayList<Event> mEventsList = new ArrayList<>();

    public HorizontalEventAdapter(MainUserActivity activity) {
        this.mUserActivity = activity;
        this.dbManager = DatabaseManager.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_horizontal_event, parent, false);
        return new HorizontalEventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        DisplayMetrics displayMetrics = new DisplayMetrics();
        mUserActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        int borderWidth = (int) (width * 20) / 100 ;

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.mainLayout.getLayoutParams();
        params.width = width - borderWidth;

        holder.mainLayout.setLayoutParams(params);

        Event holderEvent = mEventsList.get(holder.getAdapterPosition());

        if(holderEvent != null) {

            StorageReference stEvent = dbManager.getStorageEvent(holderEvent.getEid());
            if(holderEvent.getPid() != null)
                Glide.with(mUserActivity).load(stEvent.child(holderEvent.getPid())).into(holder.image);

            if(holderEvent.getTitle() != null)
                holder.title.setText(holderEvent.getTitle());

            holder.date.setText(Util.setEventDate(mUserActivity, holderEvent.getEventDate()));

            if(holderEvent.getLocation() != null)
                holder.location.setText(holderEvent.getLocation());

            if(holderEvent.getEventType() != null) {

                String music = mUserActivity.getString(R.string.event_type_music);
                String art = mUserActivity.getString(R.string.event_type_art);
                String photo = mUserActivity.getString(R.string.event_type_photography);
                String other = mUserActivity.getString(R.string.event_type_other);

                if(holderEvent.getEventType().equals(music)) {
                    holder.type.setImageResource(R.drawable.ic_music_note_white_24dp);
                }
                if(holderEvent.getEventType().equals(art)) {
                    holder.type.setImageResource(R.drawable.ic_brush_white_24dp);
                }
                if(holderEvent.getEventType().equals(photo)) {
                    holder.type.setImageResource(R.drawable.ic_photo_camera_white_24dp);
                }
                if(holderEvent.getEventType().equals(other)) {
                    holder.type.setImageResource(R.drawable.ic_free_breakfast_white_24dp);
                }

            }

            if(holderEvent.getPrice() != 0) {
                holder.price.setImageResource(R.drawable.ic_attach_money_white_24dp);
            } else {
                holder.price.setImageResource(R.drawable.ic_money_off_white_24dp);
            }


        }

        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }

    public void updateList(ArrayList<Event> events) {
        this.mEventsList = events;
        notifyDataSetChanged();
    }

}
