package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostModification;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.newsfeed.EditPostActivity;
import ca.uqac.lecitoyen.utility.CustumButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationAdapter extends RecyclerView.Adapter<PublicationAdapter.ViewHolder> {

    private static String TAG = "PublicationAdapter";

    private static float SELECT_TRANSPARENCE = 0.89f;
    private static float UNSELECT_TRANSPARENCE = 0.54f;

    private static long second = 1000;
    private static long minute = 60 * second;
    private static long hour = 60 * minute;
    private static long day = 24 * hour;

    private Context mContext;

    private FirebaseUser fbUser;
    private DatabaseManager dbManager = DatabaseManager.getInstance();


    private ArrayList<Post> mPostList = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserStorage> mProfilPictureList = new ArrayList<>();

    private boolean isUpvote = false;
    private boolean isRepost = false;

    private CustumButton mCustumButton;

    /*

            Constructor & Viewholder

     */

    public PublicationAdapter(Context context, FirebaseUser user, ArrayList<Post> postList) {
        Log.d(TAG, "PublicationAdapter");
        this.mContext = context;
        this.fbUser = user;
        this.mPostList = postList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout profilLayout;
        FrameLayout messageLayout;
        CardView pictureLayout;
        CircleImageView profilPicture;
        TextView name, userName, time, modify, message;
        ImageView picture, dropdown;


        LinearLayout upvoteLayout, repostLayout, commentLayout;
        FrameLayout shareLayout;
        TextView upvote, repost, comment, share;
        TextView upvoteCount, repostCount, commentCount;

        public ViewHolder(View itemView) {
            super(itemView);

            //  View
            profilPicture = itemView.findViewById(R.id.publication_profil_picture);
            name = itemView.findViewById(R.id.publication_profil_name);
            userName = itemView.findViewById(R.id.publication_profil_username);
            message  = itemView.findViewById(R.id.publication_message);
            time  = itemView.findViewById(R.id.publication_publish_time);
            modify = itemView.findViewById(R.id.publication_is_modify);
            picture = itemView.findViewById(R.id.publication_picture);

            dropdown = itemView.findViewById(R.id.publication_dropdown_menu);

            //  Social button
            upvoteLayout = itemView.findViewById(R.id.publication_social_upvote_layout);
            repostLayout = itemView.findViewById(R.id.publication_social_repost_layout);
            commentLayout = itemView.findViewById(R.id.publication_social_comment_layout);
            shareLayout = itemView.findViewById(R.id.publication_social_share_layout);

            upvote = itemView.findViewById(R.id.publication_social_upvote);
            repost = itemView.findViewById(R.id.publication_social_repost);
            comment = itemView.findViewById(R.id.publication_social_comment);
            share = itemView.findViewById(R.id.publication_social_share);

            upvoteCount = itemView.findViewById(R.id.publication_social_upvote_count);
            repostCount = itemView.findViewById(R.id.publication_social_repost_count);
            commentCount = itemView.findViewById(R.id.publication_social_comment_count);


            //  Layout
            profilLayout = itemView.findViewById(R.id.publication_profil_information_layout);
            messageLayout = itemView.findViewById(R.id.publication_message_layout);
            pictureLayout = itemView.findViewById(R.id.publication_picture_layout);


            //  Transparency
            repost.setAlpha(UNSELECT_TRANSPARENCE);
            repostCount.setAlpha(UNSELECT_TRANSPARENCE);
            upvote.setAlpha(UNSELECT_TRANSPARENCE);
            upvoteCount.setAlpha(UNSELECT_TRANSPARENCE);
            comment.setAlpha(UNSELECT_TRANSPARENCE);
            commentCount.setAlpha(UNSELECT_TRANSPARENCE);
            shareLayout.setAlpha(UNSELECT_TRANSPARENCE);
        }
    }

    @NonNull
    @Override
    public PublicationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_publication, parent, false);
        return new PublicationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PublicationAdapter.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder " + position);

        /*

               Get the information

         */

        final Post currPost = mPostList.get(holder.getAdapterPosition());

        /*

               Views

         */
        setAdapterViews(holder, currPost);

        /*

               OnClickListener

        */
        setMessageOnClickListener(holder, currPost);

        setProfilOnClickListener(holder, currPost);

        setSocialOnClickListener(holder, currPost);

    }

    /*

            Methods

     */

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    private void setAdapterViews(@NonNull final PublicationAdapter.ViewHolder holder, final Post post) {

        Log.d(TAG, "setAdapterViews");

        if(post != null)
        {
            //DatabaseReference postsSocial = dbManager.getDatabasePostsSocial(post.getPostid());
            StorageReference storage = dbManager.getStorageUserProfilPicture(post.getUid());
            StorageReference stPosts = dbManager.getStoragePost(post.getPostid());

            if(post.getPid() != null && !post.getPid().isEmpty())                                   //User's profil picture
                Glide.with(mContext).load(storage.child(post.getPid())).into(holder.profilPicture);
            if(post.getName() != null && !post.getName().isEmpty())                                 //User's name
                holder.name.setText(post.getName());
            if(post.getUsername() != null && !post.getUsername().isEmpty())                         //User's username
                holder.userName.setText(post.getUsername());
            if(post.getPost() != null && !post.getPost().isEmpty())                                 //Post message
                holder.message.setText(post.getPost());
            if(post.getDate() != 0)                                                                 //Time of publication
                holder.time.setText(getTimeDifference(post));
            if(post.getModifications().size() > 1)                                                  //Show if post modified
                holder.modify.setVisibility(View.VISIBLE);
            if(post.getPictureId() != null) {                                                       //Check if image exist
                holder.pictureLayout.setVisibility(View.VISIBLE);                                   //Set picture layout visible
                if(!post.getPictureId().isEmpty())
                    Glide.with(mContext).load(stPosts.child(post.getPictureId())).into(holder.picture); //Fill ImageView
            }
        } else {
            //TODO: Create blank canvas
        }
    }

    private void setMessageOnClickListener(@NonNull final PublicationAdapter.ViewHolder holder, final Post currPost) {
        holder.messageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "message layout clicked");
            }
        });

        if(currPost.getModifications().size() > 1) {
            holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showPostHistory(currPost);
                    return true;
                }
            });
        }
    }

    private void setProfilOnClickListener(@NonNull final PublicationAdapter.ViewHolder holder, final Post currPost) {
        holder.profilPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "profil picture clicked");
            }
        });

        holder.profilLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "profil layout clicked");
            }
        });
        holder.dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "dropdown clicked");
            }
        });
    }

    private void setSocialOnClickListener(@NonNull final PublicationAdapter.ViewHolder holder, final Post post) {

        final DatabaseReference dbUserUpvotes = dbManager.getDatabaseUserUpvotes(post.getUid());
        final DatabaseReference dbPostUpvotes = dbManager.getDatabasePostUpvotes(post.getPostid());
        final DatabaseReference dbUserReposts = dbManager.getDatabaseUserReposts(post.getUid());
        final DatabaseReference dbPostReposts = dbManager.getDatabasePostReposts(post.getPostid());

        final String postid = post.getPostid();
        final String uid = post.getUid();

        holder.upvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    if (!isUpvote) {
                        dbUserUpvotes.child(postid).setValue(postid);
                        dbPostUpvotes.child(uid).setValue(uid);

                        isUpvote = true;

                        holder.upvote.setBackground(mContext.getDrawable(R.drawable.ic_upvote_analogous_24dp));
                        holder.upvoteCount.setTextColor(mContext.getResources().getColor(R.color.i_analogous_g_300));

                        holder.upvote.setAlpha(SELECT_TRANSPARENCE);
                        holder.upvoteCount.setAlpha(SELECT_TRANSPARENCE);
                    }
                    else {
                        dbUserUpvotes.child(postid).removeValue();
                        dbPostUpvotes.child(uid).removeValue();

                        isUpvote = false;

                        holder.upvote.setBackground(mContext.getDrawable(R.drawable.ic_upvote_black_24dp));
                        holder.upvoteCount.setTextColor(mContext.getResources().getColor(R.color.black_900));

                        holder.upvote.setAlpha(UNSELECT_TRANSPARENCE);
                        holder.upvoteCount.setAlpha(UNSELECT_TRANSPARENCE);
                    }
                }
            }
        });
        holder.repostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "repost clicked");
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                    if (!isRepost) {

                        dbUserReposts.child(postid).setValue(postid);
                        dbPostReposts.child(uid).setValue(uid);

                        isRepost = true;

                        holder.repost.setBackground(mContext.getDrawable(R.drawable.ic_repost_secondary_24dp));
                        holder.repostCount.setTextColor(mContext.getResources().getColor(R.color.i_secondary_700));

                        holder.repost.setAlpha(SELECT_TRANSPARENCE);
                        holder.repostCount.setAlpha(SELECT_TRANSPARENCE);
                    }
                    else {
                        dbUserReposts.child(postid).removeValue();
                        dbPostReposts.child(uid).removeValue();

                        isRepost = false;

                        holder.repost.setBackground(mContext.getDrawable(R.drawable.ic_repost_black_24dp));
                        holder.repostCount.setTextColor(mContext.getResources().getColor(R.color.black_900));

                        holder.repost.setAlpha(UNSELECT_TRANSPARENCE);
                        holder.repostCount.setAlpha(UNSELECT_TRANSPARENCE);
                    }
                }
            }
        });
        holder.commentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "comment clicked");
            }
        });
        holder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Share clicked");
            }
        });
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

    private ChildEventListener checkForSocialUpdate() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void setMoreChoiceDialog(final Post currentPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        if (fbUser.getUid().equals(currentPost.getUid())) {
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
