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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static String TAG = "HomeAdapter";

    private static String longDateFormat = "HH:mm:ss - dd MMM yyyy";
    private static String simpleDateFormat = "dd MMM";
    private static String hourElapseFormat = "HHh.";
    private static String minuteElapseFormat = "mmmin.";
    private static String secondElapseFormat = "sssec.";

    private Context mContext;

    private User mUserPost = new User();
    private String mUserId;
    private ArrayList<Post> mPostList = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();

    public HomeAdapter(ArrayList<Post> postList, ArrayList<User> userList) {
        Log.d(TAG, "HomeAdapter");
        this.mPostList = postList;
        this.mUserList = userList;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_home_layout, parent, false);
        return new HomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        //  Get uid from the post
        mUserId = mPostList.get(position).getUserId();
        Log.e(TAG, "UserSize: " + mUserList.size());
        //  Get user data from the id
        for(int it = 0; it < mUserList.size(); it++) {
            Log.e(TAG, "User: " + mUserList.get(it).getName());
            mUserPost = mUserList.get(it);

            if(mUserId.equals(mUserPost.getUid())) {
               break;
            }
        }
        /*int it = 0;
        do {
            mUser = mUserList.get(it);
            it++;
        } while (!mUserId.equals(mUser.getUid()));

*/

        holder.mAuthor.setText(mUserPost.getName());
        holder.mUserName.setText(mUserPost.getUsername());

        holder.mPost.setText(mPostList.get(position).getPost());

        String dateString = new SimpleDateFormat(longDateFormat).format(
                new Date(mPostList.get(position).getDate()));
        holder.mDate.setText(dateString);

        holder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }



    public void updatePostList(ArrayList<Post> postList) {
        mPostList.clear();
        mPostList.addAll(postList);
        this.notifyDataSetChanged();
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
