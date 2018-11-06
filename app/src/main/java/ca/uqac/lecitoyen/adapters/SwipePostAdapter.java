package ca.uqac.lecitoyen.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.ExpandPostActivity;
import ca.uqac.lecitoyen.activities.ExpandPostMediaActivity;
import ca.uqac.lecitoyen.buttons.RepostButton;
import ca.uqac.lecitoyen.dialogs.DeletePostDialog;
import ca.uqac.lecitoyen.dialogs.PostHistoryDialog;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.MultimediaView;
import ca.uqac.lecitoyen.buttons.UpvoteButton;
import ca.uqac.lecitoyen.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;


public class SwipePostAdapter extends RecyclerSwipeAdapter<SwipePostAdapter.ViewHolder> {

    private static String TAG = SwipePostAdapter.class.getSimpleName();
    private static float SELECT_TRANSPARENCE = 0.89f;
    private static float UNSELECT_TRANSPARENCE = 0.54f;

    private static int PHOTO_CODE = 100;
    private static int AUDIO_CODE = 101;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        SwipeLayout swipeLayout;
        CoordinatorLayout mainLayout;

        FrameLayout historyButton, editButton, deleteButton;

        LinearLayout profilLayout;
        CircleImageView profilPicture;
        TextView name, userName, time, modify, message;

        //ImageView pictureFrame;
        MultimediaView multimediaView;

        UpvoteButton upvoteButton;
        RepostButton repostButton;

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

            upvoteButton = itemView.findViewById(R.id.post_upvote_button);
            repostButton = itemView.findViewById(R.id.post_repost_button);

            //  Multimedia
            multimediaView = itemView.findViewById(R.id.swipe_post_multimedia);

            mainLayout = itemView.findViewById(R.id.swipe_post_main_layout);

            //  Social button
            //upvoteLayout = itemView.findViewById(R.id.publication_social_upvote_layout);
            //repostLayout = itemView.findViewById(R.id.publication_social_repost_layout);
            commentLayout = itemView.findViewById(R.id.publication_social_comment_layout);
            shareLayout = itemView.findViewById(R.id.publication_social_share_layout);

            //upvote = itemView.findViewById(R.id.publication_social_upvote);
            //repost = itemView.findViewById(R.id.publication_social_repost);
            comment = itemView.findViewById(R.id.publication_social_comment);
            share = itemView.findViewById(R.id.publication_social_share);

            //upvoteCount = itemView.findViewById(R.id.publication_social_upvote_count);
            //repostCount = itemView.findViewById(R.id.publication_social_repost_count);
            commentCount = itemView.findViewById(R.id.publication_social_comment_count);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }

    private DatabaseManager dbManager;
    private Context mContext;

    private UpvoteButton mUpvoteButton;

    private User mCurrentUser;
    private String mCurrentUserId;

    private ArrayList<Post> mPostList = new ArrayList<>();

    //private boolean isUpvoteByUser;
    //private boolean isRepostByUser;

    public SwipePostAdapter() {}

    public SwipePostAdapter(Context context, User user, ArrayList<Post> posts) {
        this.dbManager = DatabaseManager.getInstance();
        this.mContext = context;
        this.mPostList = posts;

        if(user != null) {
            this.mCurrentUserId = user.getUid();
            this.mCurrentUser = new User(mCurrentUserId);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_swipe_post, parent,false);
        return new SwipePostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        DatabaseReference dbPost = dbManager.getDatabasePost(
                mPostList.get(holder.getAdapterPosition()).getPostid()
        );

        setAdapterViewHolder(holder);
        dbPost.addListenerForSingleValueEvent(setUserSocialInteraction(holder));


        dbPost.addValueEventListener(readPostUpdate(holder));

        setOnSwipeListener(holder);
        setOnClickListener(holder);
        setOnDoubleClickListener(holder);
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
                holder.time.setText(Util.setDisplayTime(mContext, holderPost.getDate()));

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

            holder.upvoteButton.setButtonCount(holderPost.getUpvoteCount());

            holder.repostButton.setButtonCount(holderPost.getRepostCount());

        } else {
            //TODO: Create blank canvas
        }
    }

    private void setHolderBottomLayout(@NonNull final ViewHolder holder) {
        /*Post holderPost = mPostList.get(holder.getAdapterPosition());
        if(!mCurrentUserId.equals(holderPost.getUser().getUid())) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
        }*/
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
                mContext.startActivity(new Intent(mContext, ExpandPostActivity.class));
            }
        });
    }

    private void setOnClickListener(@NonNull final ViewHolder holder) {

        final Post holderPost = mPostList.get(holder.getAdapterPosition());

        holder.multimediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("postid", holderPost.getPostid());
                if(holder.multimediaView.isPhoto()) {

                    bundle.putInt("code", PHOTO_CODE);

                    Intent intent = new Intent(mContext, ExpandPostMediaActivity.class);
                    intent.putExtras(bundle);

                    mContext.startActivity(intent);
                }
                if(holder.multimediaView.isAudio()) {

                    bundle.putInt("code", AUDIO_CODE);

                    Intent intent = new Intent(mContext, ExpandPostMediaActivity.class);
                    intent.putExtras(bundle);

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
                PostHistoryDialog postHistoryDialog = new PostHistoryDialog(mContext, holderPost);
                postHistoryDialog.show();
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
                DeletePostDialog deletePostDialog = new DeletePostDialog(mContext);
                deletePostDialog.delete(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbManager.deleteHolderPost(holderPost);
                        mPostList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(), mPostList.size());
                        dialogInterface.dismiss();
                    }
                }).cancel(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
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

    /**
     *
     *      Value Event Listener
     *
     **/

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
                        holder.upvoteButton.setButtonOn();
                    else
                        holder.upvoteButton.setButtonOff();
                }

                if(!repostUsers.isEmpty())
                {
                    if(repostUsers.containsKey(mCurrentUserId))
                        holder.repostButton.setButtonOn();
                    else
                        holder.repostButton.setButtonOff();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        };
    }

    private ValueEventListener readPostUpdate(@NonNull final ViewHolder holder) {

        final Post holderPost = mPostList.get(holder.getAdapterPosition());

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //TODO: Ajouter à utilisatueur et faire Repost

                //  Get upvote users
                final DataSnapshot upvoteUsersSnapshot = dataSnapshot.child("upvoteUsers");

                final Map<String, User> upvoteUsers = new HashMap<>();
                final Map<String, User> repostUsers = new HashMap<>();
                for(DataSnapshot userSnapshot: upvoteUsersSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(user != null) {
                        upvoteUsers.put(user.getUid(), user);
                        repostUsers.put(user.getUid(), user);
                    }
                }
                holder.upvoteButton.setOnClickListener(setUpvoteOnClickListener(holder, holderPost, upvoteUsers));
                //holder.upvoteLayout.setOnClickListener(setUpvoteOnClickListener(holder, holderPost, upvoteUsers));
                holder.repostButton.setOnClickListener(setRepostOnClickListener(holder, holderPost, repostUsers));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

    }

    private View.OnClickListener setUpvoteOnClickListener(@NonNull final ViewHolder holder,
                                                          final Post holderPost, final Map<String, User> users) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!holder.upvoteButton.isButtonOn()) {
                    /*
                      Update data structure
                      - Add current user to the list
                      - Update Upvote count with the new size of Users
                      - Update Upvote users with the new users
                    */

                    users.put(mCurrentUserId, mCurrentUser);
                    holderPost.setUpvoteUsers(users);
                    holderPost.setUpvoteCount(users.size());

                    //  Update firebase
                    dbManager.writeUpvoteToPost(
                            mCurrentUser,
                            holderPost
                    );
                    //notifyItemRemoved(holder.getAdapterPosition());
                    //notifyItemRangeChanged(holder.getAdapterPosition(), mPostList.size());
                    //notifyDataSetChanged();
                    //notifyItemChanged(holder.getAdapterPosition());
                    //dbManager.writeUpvoteToUser(mCurrentUser);

                    //  Update UI
                    holder.upvoteButton.setButtonOn();
                    //etUpvoteButtonOn(holder);
                } else {
                    /*
                      Update data structure
                      - Remove current user from the list
                      - Update Upvote count with the new size of Users
                      - Update Upvote users with the new users
                    */
                    users.remove(mCurrentUserId);
                    holderPost.setUpvoteCount(users.size());
                    holderPost.setUpvoteUsers(users);

                    //  Update firebase
                    dbManager.removeUpvoteFromPost(
                            mCurrentUser,
                            holderPost
                    );

                    //Upddate UI of button
                    holder.upvoteButton.setButtonOff();
                }
                //  Update UI Count
                holder.upvoteButton.setButtonCount(holderPost.getUpvoteCount());
                //holder.upvoteCount.setText(String.valueOf(holderPost.getUpvoteCount()));
            }
        };
    }

    private View.OnClickListener setRepostOnClickListener(@NonNull final ViewHolder holder,
                                                          final Post holderPost, final Map<String, User> users) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.repostButton.isButtonOn()) {
                    /*
                      Update data structure
                      - Add current user to the list
                      - Update Repost count with the new size of Users
                      - Update Repost users with the new users
                    */

                    users.put(mCurrentUserId, mCurrentUser);
                    holderPost.setRepostUsers(users);
                    holderPost.setRepostCount(users.size());

                    //  Update firebase
                    dbManager.writeRepostToPost(
                            mCurrentUser,
                            holderPost
                    );

                    //  Update UI
                    holder.repostButton.setButtonOn();
                } else {
                    /*
                      Update data structure
                      - Remove current user from the list
                      - Update Repost count with the new size of Users
                      - Update Repost users with the new users
                    */
                    users.remove(mCurrentUserId);
                    holderPost.setRepostCount(users.size());
                    holderPost.setRepostUsers(users);

                    //  Update firebase
                    dbManager.removeRepostFromPost(
                            mCurrentUser,
                            holderPost
                    );

                    //Upddate UI of button
                    holder.repostButton.setButtonOff();
                }
                //  Update UI Count
                holder.repostButton.setButtonCount(holderPost.getRepostCount());
            }
        };
    }




}
