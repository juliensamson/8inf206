package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.userUI.ExpandPostActivity;
import ca.uqac.lecitoyen.utility.MultimediaView;
import ca.uqac.lecitoyen.utility.TimeUtility;
import de.hdodenhof.circleimageview.CircleImageView;
import nl.changer.audiowife.AudioWife;



public class SwipePostAdapter extends RecyclerSwipeAdapter<SwipePostAdapter.ViewHolder> {

    private static String TAG = "SwipePostAdapter";
    private static float SELECT_TRANSPARENCE = 0.89f;
    private static float UNSELECT_TRANSPARENCE = 0.54f;

    private static int PHOTO_CODE = 100;
    private static int AUDIO_CODE = 101;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        SwipeLayout swipeLayout;

        FrameLayout historyButton, editButton, deleteButton;

        LinearLayout profilLayout;
        CircleImageView profilPicture;
        TextView name, userName, time, modify, message;

        //ImageView pictureFrame;
        MultimediaView multimediaView;

        LinearLayout upvoteLayout, repostLayout, commentLayout, shareLayout;
        TextView upvote, repost, comment, share;
        TextView upvoteCount, repostCount, commentCount;


        public ViewHolder(View itemView) {
            super(itemView);

            swipeLayout = itemView.findViewById(R.id.swipe_post_swipe_layout);
            historyButton = itemView.findViewById(R.id.swipe_post_button_history);
            editButton = itemView.findViewById(R.id.swipe_post_button_modify);
            deleteButton = itemView.findViewById(R.id.swipe_post_button_delete);

            profilLayout = itemView.findViewById(R.id.swipe_post_profil_layout);
            profilPicture = itemView.findViewById(R.id.swipe_post_profil_picture);
            name = itemView.findViewById(R.id.swipe_post_name);
            userName = itemView.findViewById(R.id.swipe_post_username);
            time = itemView.findViewById(R.id.swipe_post_publish_time);
            modify = itemView.findViewById(R.id.swipe_post_is_modify);
            message = itemView.findViewById(R.id.swipe_post_message);

            //  Multimedia
            multimediaView = itemView.findViewById(R.id.swipe_post_multimedia);

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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
            //  View
            /*profilPicture = itemView.findViewById(R.id.publication_profil_picture);
            name = itemView.findViewById(R.id.publication_profil_name);
            userName = itemView.findViewById(R.id.publication_profil_username);
            message  = itemView.findViewById(R.id.publication_message);
            time  = itemView.findViewById(R.id.publication_publish_time);
            modify = itemView.findViewById(R.id.publication_is_modify);
            picture = itemView.findViewById(R.id.publication_picture);
            playerLayout = itemView.findViewById(R.id.publication_audio_layout);
            mainLayout = itemView.findViewById(R.id.publication_main_layout);
            cardLayout = itemView.findViewById(R.id.publication_card_layout);

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
            shareLayout.setAlpha(UNSELECT_TRANSPARENCE);*/
        }
    }

    private DatabaseManager dbManager;
    private Context mContext;
    private TimeUtility timeUtility;


    private User mCurrentUser;
    private String mCurrentUserId;

    private ArrayList<Post> mPostList = new ArrayList<>();

    private boolean isUpvoteByUser;
    private boolean isRepostByUser;

    public SwipePostAdapter() {}

    public SwipePostAdapter(Context context, FirebaseUser user, ArrayList<Post> posts) {
        this.dbManager = DatabaseManager.getInstance();
        this.mContext = context;
        this.mPostList = posts;

        if(user != null) {
            this.mCurrentUserId = user.getUid();
            this.mCurrentUser = new User(mCurrentUserId);
        }
        this.timeUtility = new TimeUtility(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_swipe_post, parent,false);
        return new SwipePostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        DatabaseReference dbPost = dbManager.getDatabasePost(
                mPostList.get(holder.getAdapterPosition()).getPostid());

        setAdapterViewHolder(holder);
        dbPost.addValueEventListener(setUserSocialInteraction(holder));

        setOnSwipeListener(holder);
        setOnClickListener(holder);
        setOnDoubleClickListener(holder);

        //holder.textViewPosition.setText((position + 1) + ".");
        //holder.textViewData.setText(holderPost.getMessage());

    }

    /*
     *
     *      Views
     *
     */

    private void setAdapterViewHolder(@NonNull final ViewHolder holder) {
        final Post holderPost = mPostList.get(holder.getAdapterPosition());
        if(holderPost != null)
        {
            setHolderBottomLayout(holder);
            User user = holderPost.getUser();

            StorageReference storage = dbManager.getStorageUserProfilPicture(user.getUid());
            StorageReference stPosts = dbManager.getStoragePost(holderPost.getPostid());

            if(user.getPid() != null && !user.getPid().isEmpty())                                   //User's profil picture
                Glide.with(mContext).load(storage.child(user.getPid())).into(holder.profilPicture);
            if(user.getName() != null && !user.getName().isEmpty())                                 //User's name
                holder.name.setText(holderPost.getUser().getName());
            if(user.getUsername() != null && !user.getUsername().isEmpty())                         //User's username
                holder.userName.setText(user.getUsername());
            if(holderPost.getMessage() != null && !holderPost.getMessage().isEmpty())                                 //Post message
                holder.message.setText(holderPost.getMessage());
            if(holderPost.getDate() != 0)                                                                 //Time of publication
                holder.time.setText(timeUtility.getTimeDifference(holderPost));
            if(holderPost.getHistories().size() > 1)                                                  //Show if post modified
                holder.modify.setVisibility(View.VISIBLE);

            if(holderPost.getImages() != null) {
                if (!holderPost.getImages().get(0).getImageId().isEmpty()){
                    holder.multimediaView.setMultimediaImage(stPosts
                            .child(holderPost.getImages().get(0).getImageId()));
                }
            }
            if(holderPost.getAudio() != null) {
                if (!holderPost.getAudio().isEmpty()) {
                    Log.d(TAG, holderPost.getAudio());
                    holder.multimediaView.setMultimediaMusic(holderPost.getAudio());
                }
            }
            holder.upvoteCount.setText(String.valueOf(holderPost.getUpvoteCount()));
            holder.repostCount.setText(String.valueOf(holderPost.getRepostCount()));
        } else {
            //TODO: Create blank canvas
        }
    }

    private void setHolderBottomLayout(@NonNull final ViewHolder holder) {
        Post holderPost = mPostList.get(holder.getAdapterPosition());
        if(!mCurrentUserId.equals(holderPost.getUser().getUid())) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        }
    }

    private ValueEventListener setUserSocialInteraction(@NonNull final ViewHolder holder) {
        final Post holderPost = mPostList.get(holder.getAdapterPosition());
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final Map<String, User> upvoteUsers = getUsers(dataSnapshot.child("upvoteUsers"));
                final Map<String, User> repostUsers = getUsers(dataSnapshot.child("repostUsers"));

                //  Get users count
                holderPost.setUpvoteCount(upvoteUsers.size());
                holderPost.setRepostCount(repostUsers.size());

                //  Find if users upvote the post and initialize
                if(!upvoteUsers.isEmpty())
                {
                    if(upvoteUsers.containsKey(mCurrentUserId))
                        setUpvoteButtonOn(holder);
                    else
                        setUpvoteButtonOff(holder);
                } else {
                    Log.e(TAG, "No user upvoted this post");
                }

                if(!repostUsers.isEmpty())
                {
                    if(repostUsers.containsKey(mCurrentUserId))
                        setRepostButtonOn(holder);
                    else
                        setRepostButtonOff(holder);
                } else {
                    Log.e(TAG, "No user repost this post");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    /*
     *
     *      Listener
     *
     */

    private void setOnSwipeListener(@NonNull final ViewHolder holder) {
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                layout.findViewById(R.id.swipe_post_inside_layout);
            }
        });
    }

    private void setOnDoubleClickListener(@NonNull final ViewHolder holder) {
        holder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListener(@NonNull final ViewHolder holder) {
        final Post holderPost = mPostList.get(holder.getAdapterPosition());
        holder.multimediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if(holder.multimediaView.isPhoto()) {
                    Intent intent = new Intent(mContext, ExpandPostActivity.class);
                    intent.putExtra(TAG, holderPost.getPostid());
                    intent.putExtra(TAG, PHOTO_CODE);
                    mContext.startActivity(intent);
                }
                if(holder.multimediaView.isAudio()) {
                    Intent intent = new Intent(mContext, ExpandPostActivity.class);
                    intent.putExtra(TAG, holderPost.getPostid());
                    intent.putExtra(TAG, AUDIO_CODE);
                    mContext.startActivity(intent);
                }
                if(holder.multimediaView.isLink()) {

                }
            }
        });
        holder.historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "History " + holderPost.getMessage(), Toast.LENGTH_SHORT).show();
                showPostHistory(holderPost);
            }
        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Edit " + holderPost.getMessage(), Toast.LENGTH_SHORT).show();
                //Start activity edit
            }
        });
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmation(holder, holderPost);
            }
        });
    }

    /**
     *
     *      Methods
     *
     **/

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe_post_main_layout;
    }

    private Map<String, User> getUsers(DataSnapshot snapshot) {
        final Map<String, User> users = new HashMap<>();
        for(DataSnapshot userSnapshot: snapshot.getChildren()) {
            User user = userSnapshot.getValue(User.class);
            if(user != null) {
                users.put(user.getUid(), user);
            }
        }
        return users;
    }

    private void showPostHistory(final Post holderPost) {
        RecyclerView mRecyclerView = new RecyclerView(mContext);

        RecyclerView.Adapter adapter = new PostHistoryAdapter(
                mContext,
                holderPost.getHistories()
        );

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

    private void showDeleteConfirmation(@NonNull final ViewHolder holder, final Post holderPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Supprimer")
                .setIcon(R.drawable.ic_delete_forever_black_24dp)
                .setMessage("Êtes-vous certain de vouloir supprimer cette publication?" +
                        "\nTout commentaire, upvote, et repost seront supprimer aussi.")
                .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(mContext, "Delete " + holderPost.getMessage(), Toast.LENGTH_SHORT).show();
                        dbManager.deleteHolderPost(holderPost);
                        mPostList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), mPostList.size());
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    /**
     *
     *         Handle button color & effect
     *
     **/

    private void setUpvoteButtonOn(@NonNull final ViewHolder holder) {
        isUpvoteByUser = true;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.upvote.setBackground(mContext.getDrawable(R.drawable.ic_upvote_analogous_24dp));
            holder.upvoteCount.setTextColor(mContext.getResources().getColor(R.color.i_analogous_g_300));
        }
        holder.upvote.setAlpha(SELECT_TRANSPARENCE);
        holder.upvoteCount.setAlpha(SELECT_TRANSPARENCE);
    }

    private void setUpvoteButtonOff(@NonNull final ViewHolder holder) {
        isUpvoteByUser = false;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.upvote.setBackground(mContext.getDrawable(R.drawable.ic_upvote_black_24dp));
            holder.upvoteCount.setTextColor(mContext.getResources().getColor(R.color.black_900));
        }
        holder.upvote.setAlpha(UNSELECT_TRANSPARENCE);
        holder.upvoteCount.setAlpha(UNSELECT_TRANSPARENCE);
    }

    private void setRepostButtonOn(@NonNull final ViewHolder holder) {
        isRepostByUser = true;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.repost.setBackground(mContext.getDrawable(R.drawable.ic_repost_secondary_24dp));
            holder.repostCount.setTextColor(mContext.getResources().getColor(R.color.i_secondary_700));
        }
        holder.repost.setAlpha(SELECT_TRANSPARENCE);
        holder.repostCount.setAlpha(SELECT_TRANSPARENCE);
    }

    private void setRepostButtonOff(@NonNull final ViewHolder holder) {
        isRepostByUser = false;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.repost.setBackground(mContext.getDrawable(R.drawable.ic_repost_black_24dp));
            holder.repostCount.setTextColor(mContext.getResources().getColor(R.color.black_900));
        }
        holder.repost.setAlpha(UNSELECT_TRANSPARENCE);
        holder.repostCount.setAlpha(UNSELECT_TRANSPARENCE);
    }


}
