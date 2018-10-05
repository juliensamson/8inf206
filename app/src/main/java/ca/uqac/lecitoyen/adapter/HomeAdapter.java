package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostModification;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.userUI.newsfeed.EditPostActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private static String TAG = "HomeAdapter";

    private static String longDateFormat = "dd MMM yyyy";
    private String[] publicChoiceString;
    private String[] privateChoiceString;

    private static long second = 1000;
    private static long minute = 60 * 1000;
    private static long hour = 60 * 60 * 1000;
    private static long day = 24 * 60 * 60 * 1000;

    private Context mContext;

    private FirebaseUser mCurrentUser;

    private Post mCurrentPost;
    private User userDataFromPost = new User();
    private String mUserId;
    private ArrayList<Post> mPostList = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();

    public HomeAdapter(Context context, FirebaseUser currentUser, ArrayList<Post> postList, ArrayList<User> userList) {
        Log.d(TAG, "HomeAdapter");
        this.mContext = context;
        this.mCurrentUser = currentUser;
        this.mPostList = postList;
        this.mUserList = userList;
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_home, parent, false);
        return new HomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder " + position);
        final Post currentPost = mPostList.get(holder.getAdapterPosition());
        User userFromPost = getUserPost(holder.getAdapterPosition());
        //userDataFromPost = getUserPost(position);

        //holder.profileImage.setImageResource();
        holder.name.setText(userFromPost.getName());
        holder.userName.setText(userFromPost.getUsername());
        holder.post.setText(currentPost.getPost());
        holder.time.setText(getTimeElapseSincePost(holder.getAdapterPosition()));

        ////  TODO: display post information
        holder.postLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showPostHistory(currentPost);
                return true;
            }
        });
        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "profile_layout clicked");
            }
        });
        holder.moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMoreChoiceDialog(currentPost);
            }
        });
        holder.postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "post_layout clicked");
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mainLayout;
        FrameLayout postLayout, profileLayout, moreLayout;
        CircleImageView profileImage;
        TextView name, userName, post, time;

        public ViewHolder(View itemView) {
            super(itemView);

            //  View
            profileImage = itemView.findViewById(R.id.listview_home_profil_image);
            name = itemView.findViewById(R.id.listview_home_name);
            userName = itemView.findViewById(R.id.listview_home_username);
            post  = itemView.findViewById(R.id.listview_home_post);
            time  = itemView.findViewById(R.id.listview_home_time);

            //  Layout
            mainLayout = itemView.findViewById(R.id.listview_home_main_layout);
            profileLayout = itemView.findViewById(R.id.listview_home_profil_layout);
            moreLayout = itemView.findViewById(R.id.listview_more_layout);
            postLayout = itemView.findViewById(R.id.listview_home_post_layout);
        }
    }

    //  METHODS

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

    private void setMoreChoiceDialog(final Post currentPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        ListView moreChoiceView = new ListView(mContext);

        String[] choiceString;
        Log.e(TAG, "uid in post: " + currentPost.getUid());
        Log.e(TAG, "uid connect: " + mCurrentUser.getUid());
        if (mCurrentUser.getUid().equals(currentPost.getUid())) {
            builder.setItems(R.array.private_more_choice_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:     //Historique
                            Log.e(TAG, "0");
                            break;
                        case 1:     //Modifier
                            Log.e(TAG, "1");
                            Intent intent = new Intent(mContext, EditPostActivity.class);
                            intent.putExtra("postid", currentPost.getPostid());
                            mContext.startActivity(intent);
                            break;
                        case 2:     //Supprimer
                            Log.e(TAG, "2");
                            deleteCurrentPost(currentPost);
                            break;
                        default:
                            break;
                    }
                }
            });
        } else {
            builder.setItems(R.array.public_more_choice_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:     //Historique
                            Log.e(TAG, "0");
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        builder.show();
    }

    private void showPostHistory(final Post currentPost) {
        RecyclerView mRecyclerView = new RecyclerView(mContext);

        RecyclerView.Adapter adapter = new PostHistoryAdapter(
                mContext,
                (ArrayList<PostModification>) currentPost.getModifications());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Historique")
                .setView(mRecyclerView)
                .setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void deleteCurrentPost(final Post currentPost) {
        DatabaseManager.getInstance().getReference()
                .child("posts")
                .child(currentPost.getPostid())
                .removeValue();
        DatabaseManager.getInstance().getReference()
                .child("user-post")
                .child(currentPost.getUid())
                .child(currentPost.getPostid())
                .removeValue();
    }

}
