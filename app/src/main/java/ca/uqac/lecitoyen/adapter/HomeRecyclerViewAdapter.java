package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.Post;


public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>{

    private static String TAG = "HomeRecyclerViewAdapter";

    private Context mContext;

    private ArrayList<Post> mPostList = new ArrayList<>();

    public HomeRecyclerViewAdapter(ArrayList<Post> postList) {
        Log.d(TAG, "HomeRecyclerViewAdapter");
        this.mPostList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_home_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder");
        holder.mAuthor.setText(mPostList.get(position).getAuthor());
        holder.mUserName.setText(mPostList.get(position).getUserName());
        holder.mPost.setText(mPostList.get(position).getPost());

        String dateString = new SimpleDateFormat("HH:mm - dd MMM yyyy").format(
                new Date(mPostList.get(position).getDate()));
        holder.mDate.setText(dateString);

        holder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked on " +
                        mPostList.get(position).getAuthor() + " " +
                        mPostList.get(position).getUserName() + " " +
                        mPostList.get(position).getPost());
               }
        });
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout mParentLayout;
        TextView mAuthor;
        TextView mUserName;
        TextView mPost;
        TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);

            mAuthor = itemView.findViewById(R.id.recycler_view_realname);
            mUserName = itemView.findViewById(R.id.recycler_view_username);
            mPost  = itemView.findViewById(R.id.recycler_view_message);
            mDate  = itemView.findViewById(R.id.recycler_view_date);
            mParentLayout = itemView.findViewById(R.id.recycler_view_layout);
        }
    }
}
