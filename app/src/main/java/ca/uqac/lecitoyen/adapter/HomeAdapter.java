package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.userUI.UserMainActivity;
import ca.uqac.lecitoyen.userUI.newsfeed.EditPostActivity;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static String TAG = "HomeAdapter";

    private static String longDateFormat = "HH:mm:ss - dd MMM yyyy";

    private static long second = 1000;
    private static long minute = 60 * 1000;
    private static long hour = 60 * 60 * 1000;
    private static long day = 24 * 60 * 60 * 1000;

    private Context mContext;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private User userDataFromPost = new User();
    private String mUserId;
    private ArrayList<Post> mPostList = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();

    public HomeAdapter(Context context, ArrayList<Post> postList, ArrayList<User> userList) {
        Log.d(TAG, "HomeAdapter");
        this.mContext = context;
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
    public void onBindViewHolder(@NonNull final HomeAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        final Post currentPost = mPostList.get(holder.getAdapterPosition());
        User userFromPost = getUserPost(holder.getAdapterPosition());
        //userDataFromPost = getUserPost(position);

        holder.mAuthor.setText(userFromPost.getName());
        holder.mUserName.setText(userFromPost.getUsername());
        holder.mPost.setText(currentPost.getPost());
        holder.mDate.setText(getTimeElapseSincePost(holder.getAdapterPosition()));


        holder.mParentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUser.getUid().equals(currentPost.getUid()))
                {
                    Bundle extras = new Bundle();
                    Intent intent = new Intent(mContext, EditPostActivity.class);
                    intent.putExtra("postid", currentPost.getPostid());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    private User getUserPost(int position) {
        //  Get uid for the post encounter
        mUserId = mPostList.get(position).getUid();

        //  Get the detail of the user from the post
        User user = new User();
        for(int it = 0; it < mUserList.size(); it++) {
            user = mUserList.get(it);
            if(mUserId.equals(user.getUid())) {
                break;
            }
        }
        return user;
    }

    private String getTimeElapseSincePost(int position) {

        long postTime = mPostList.get(position).getDate();
        long timeElapse = System.currentTimeMillis() - postTime;

        Date postDate = new Date(postTime) ;

        String timeDisplayed;
        if(timeElapse < minute)
        {
            timeDisplayed = String.valueOf(timeElapse / second);
            return timeDisplayed + "s";
        }
        else if(timeElapse >= minute && timeElapse < hour)
        {
            timeDisplayed = String.valueOf(timeElapse / minute);
            return timeDisplayed + "m";
        }
        else if(timeElapse >= hour && timeElapse < day)
        {
            timeDisplayed = String.valueOf(timeElapse / hour);
            return timeDisplayed + "h";
        }
        else
        {
            timeDisplayed = new SimpleDateFormat(
                    longDateFormat,
                    Locale.CANADA_FRENCH)
                    .format(postDate);
            return timeDisplayed;
        }
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
