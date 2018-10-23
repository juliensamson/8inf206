package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostModification;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.newsfeed.EditPostActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostModification;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.newsfeed.EditPostActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder> {

    private static String TAG = "PublicationAdapter";

    private static long second = 1000;
    private static long minute = 60 * second;
    private static long hour = 60 * minute;
    private static long day = 24 * hour;

    private Context mContext;

    private FirebaseUser mCurrentUser;
    private DatabaseManager dbManager = DatabaseManager.getInstance();

    private ArrayList<Post> mPostList = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserStorage> mProfilPictureList = new ArrayList<>();

    /*

            Constructor & Viewholder

     */

    public PublicationAdapter(Context context, ArrayList<Post> postList) {
        Log.d(TAG, "PublicationAdapter");
        this.mContext = context;
        this.mPostList = postList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        FrameLayout postLayout, profileLayout, moreLayout;
        CircleImageView profilPicture;
        TextView name, userName, post, time, modify;

        public ViewHolder(View itemView) {
            super(itemView);

            //  View
            profilPicture = itemView.findViewById(R.id.listview_feed_profil_picture);
            name = itemView.findViewById(R.id.listview_feed_name);
            userName = itemView.findViewById(R.id.listview_feed_username);
            post  = itemView.findViewById(R.id.listview_feed_post);
            time  = itemView.findViewById(R.id.listview_feed_time);
            modify = itemView.findViewById(R.id.listview_feed_modify);

            //  Layout
            profileLayout = itemView.findViewById(R.id.listview_feed_profil_layout);
            moreLayout = itemView.findViewById(R.id.listview_feed_more_layout);
            postLayout = itemView.findViewById(R.id.listview_feed_post_layout);
        }
    }

    @NonNull
    @Override
    public PublicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_feed, parent, false);
        return new PublicationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicationAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        /*

               Get the information

         */

        final Post currPost = mPostList.get(holder.getAdapterPosition());
        StorageReference storage = dbManager.getStorageUserProfilPicture(currPost.getUid());

        /*

               Views

         */

        if(currPost.getPid() != null)
            Glide.with(mContext).load(storage.child(currPost.getPid())).into(holder.profilPicture);
        holder.name.setText(currPost.getName());
        holder.userName.setText(currPost.getUsername());
        holder.post.setText(currPost.getPost());
        holder.time.setText(getTimeDifference(currPost));
        if(currPost.getModifications().size() > 1) {holder.modify.setVisibility(View.VISIBLE);}

         /*

               OnClickListener

         */

        holder.postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "post_layout clicked");
            }
        });

        if(currPost.getModifications().size() > 1) {
            holder.postLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPostHistory(currPost);
                    return true;
                }
            });
        }

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "profile_layout clicked");
            }
        });

        holder.moreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMoreChoiceDialog(currPost);
            }
        });
    }

    /*

            Methods

     */

    @Override
    public int getItemCount() {
        return mPostList.size();
    }


    private String getTimeDifference(final Post currentPost) {

        String textBeforeAgo = mContext.getResources().getString(R.string.text_time_ago) + " ";
        String textBeforeThe  = mContext.getResources().getString(R.string.text_time_the) + " ";
        String textSecond = mContext.getResources().getString(R.string.time_short_second);
        String textMinute = mContext.getResources().getString(R.string.time_short_minute);
        String textHour   = mContext.getResources().getString(R.string.time_short_hour);

        long postTime = currentPost.getDate();
        long timeElapse = System.currentTimeMillis() - postTime;

        Date postDate = new Date(postTime) ;

        String timeDisplayed;
        if(timeElapse < minute)
        {
            timeDisplayed = String.valueOf(timeElapse / second);
            return textBeforeAgo + timeDisplayed + textSecond;
        }
        else if(timeElapse >= minute && timeElapse < hour)
        {
            timeDisplayed = String.valueOf(timeElapse / minute);
            return textBeforeAgo = timeDisplayed + textMinute;
        }
        else if(timeElapse >= hour && timeElapse < day)
        {
            timeDisplayed = String.valueOf(timeElapse / hour);
            return textBeforeAgo + timeDisplayed + textHour;
        }
        else
        {
            timeDisplayed = new SimpleDateFormat(
                    mContext.getResources().getString(R.string.short_date_format),
                    Locale.CANADA_FRENCH)
                    .format(postDate);
            return textBeforeThe + timeDisplayed;
        }
    }

    private void setMoreChoiceDialog(final Post currentPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        if (mCurrentUser.getUid().equals(currentPost.getUid())) {
            builder.setItems(R.array.private_more_choice_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:     //Historique
                            break;
                        case 1:     //Modifier
                            Intent intent = new Intent(mContext, EditPostActivity.class);
                            intent.putExtra("postid", currentPost.getPostid());
                            mContext.startActivity(intent);
                            break;
                        case 2:     //Supprimer
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

        builder.setTitle(mContext.getResources().getString(R.string.post_history))
                .setView(mRecyclerView)
                .setPositiveButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void delete(final int position){
        mPostList.remove(position);
        notifyItemRemoved(position);
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
