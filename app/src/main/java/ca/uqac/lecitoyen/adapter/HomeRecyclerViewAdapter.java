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

import java.util.ArrayList;

import ca.uqac.lecitoyen.R;


public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>{

    private static String TAG = "HomeRecyclerViewAdapter";

    private Context mContext;

    private ArrayList<String> mRealNameList = new ArrayList<>();
    private ArrayList<String> mUserNameList = new ArrayList<>();
    private ArrayList<String> mMessageList = new ArrayList<>();

    public HomeRecyclerViewAdapter(ArrayList<String> mRealNameList, ArrayList<String> mUserNameList, ArrayList<String> mMessageList) {
        Log.d(TAG, "HomeRecyclerViewAdapter");
        this.mRealNameList = mRealNameList;
        this.mUserNameList = mUserNameList;
        this.mMessageList = mMessageList;
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
        holder.mRealName.setText(mRealNameList.get(position));
        holder.mUserName.setText(mUserNameList.get(position));
        holder.mMessage.setText(mMessageList.get(position));

        holder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked on " +
                        mRealNameList.get(position) + " " + mUserNameList.get(position));
                //Toast.makeText(mContext,
                 //       mRealNameList.get(position) + " " + mUserNameList.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRealNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout mParentLayout;
        TextView mRealName;
        TextView mUserName;
        TextView mMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            mRealName = itemView.findViewById(R.id.recycler_view_realname);
            mUserName = itemView.findViewById(R.id.recycler_view_username);
            mMessage  = itemView.findViewById(R.id.recycler_view_message);
            mParentLayout = itemView.findViewById(R.id.recycler_view_layout);
        }
    }
}
