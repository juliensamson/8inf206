package ca.uqac.lecitoyen.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.Interface.iHandleFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.ExpandPostActivity;
import ca.uqac.lecitoyen.activities.ExpandPostMediaActivity;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.buttons.RepostButton;
import ca.uqac.lecitoyen.dialogs.DeletePostDialog;
import ca.uqac.lecitoyen.dialogs.ExpandMediaDialog;
import ca.uqac.lecitoyen.dialogs.PostHistoryDialog;
import ca.uqac.lecitoyen.fragments.userUI.ProfilFragment;
import ca.uqac.lecitoyen.fragments.userUI.UserProfileFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;
import ca.uqac.lecitoyen.util.Constants;
import ca.uqac.lecitoyen.util.MultimediaView;
import ca.uqac.lecitoyen.buttons.UpvoteButton;
import ca.uqac.lecitoyen.util.Util;
import de.hdodenhof.circleimageview.CircleImageView;


public class SwipePostAdapter extends RecyclerSwipeAdapter<SwipePostAdapter.ViewHolder> implements Serializable {

    private static String TAG = SwipePostAdapter.class.getSimpleName();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mainLayout;
        SwipeLayout swipeLayout;

        FrameLayout historyButton, editButton, deleteButton;


        LinearLayout profilLayout;
        CircleImageView profilPicture;
        TextView name, userName, time, modify, message;
        MultimediaView multimediaView;

        UpvoteButton upvoteButton;
        RepostButton repostButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.swipe_post_main_layout);
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
            multimediaView = itemView.findViewById(R.id.swipe_post_multimedia);

            upvoteButton = itemView.findViewById(R.id.post_upvote_button);
            repostButton = itemView.findViewById(R.id.post_repost_button);

            //  Multimedia



        }
    }

    private DatabaseManager dbManager;
    private Context mContext;
    private MainUserActivity mUserActivity;
    private iHandleFragment mHandleFragment;

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

    public SwipePostAdapter(MainUserActivity userActivity, User user, ArrayList<Post> posts) {
        this.dbManager = DatabaseManager.getInstance();
        this.mContext = userActivity;
        this.mPostList = posts;
        this.mUserActivity = userActivity;
        this.mHandleFragment = userActivity;
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

            StorageReference storage = dbManager.getStorageUserProfilPicture(user.getUid(), user.getPid());
            StorageReference stPosts = dbManager.getStoragePost(holderPost.getPostid());

            if(user.getPid() != null && !user.getPid().isEmpty())                                   //User's profil picture
                Glide.with(mContext).load(storage).into(holder.profilPicture);

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


            if(holderPost.getImages() != null && !holderPost.getImages().isEmpty()) {



                if (!holderPost.getImages().get(0).getImageid().isEmpty()){
                    holder.multimediaView
                            .loadImages(stPosts.child(holderPost.getImages().get(0).getImageid()))
                            .setMinimumHeight();


                    //holder.multimediaView.setMultimediaImage(stPosts
                     //       .child(holderPost.getImages().get(0).getImageId()));
                } else
                    Log.e(TAG, "nothing");
            }

            if(holderPost.getAudio() != null) {
                holder.multimediaView.loadAudio(holderPost.getAudio());
            }

            holder.upvoteButton.setButtonCount(holderPost.getUpvoteCount());

            holder.repostButton.setButtonCount(holderPost.getRepostCount());

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

        final Post holderPost = mPostList.get(holder.getAdapterPosition());

        holder.swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Bundle bundle = new Bundle();
                bundle.putString("postid", holderPost.getPostid());
                Intent intent = new Intent(mContext, ExpandPostActivity.class);
                intent.putExtras(bundle);
                //intent.putExtra(TAG, holderPost);
                mContext.startActivity(intent);
                //mainUserActivity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
    }

    private void setOnClickListener(@NonNull final ViewHolder holder) {

        final Post holderPost = mPostList.get(holder.getAdapterPosition());
        final StorageReference stPost = dbManager.getStoragePost(holderPost.getPostid());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("postid", holderPost.getPostid());
                Intent intent = new Intent(mContext, ExpandPostActivity.class);
                intent.putExtras(bundle);
                //intent.putExtra(TAG, holderPost);
                mContext.startActivity(intent);
            }
        });

        holder.upvoteButton.setUpvoteOnClickListener(
                holder.upvoteButton,
                mCurrentUser,
                mPostList.get(holder.getAdapterPosition())
        );

        holder.repostButton.setRepostOnClickListener(
                holder.repostButton,
                mCurrentUser,
                mPostList.get(holder.getAdapterPosition())
        );

        holder.profilPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment fragment = UserProfileFragment.newInstance(mCurrentUserId, holderPost.getUser());
                mUserActivity.doUserProfileTransaction(fragment, MainUserActivity.SELECT_USER);
            }
        });

        holder.multimediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.multimediaView.isPhoto()) {
                    ExpandMediaDialog dialog = new ExpandMediaDialog(mUserActivity);
                    dialog.create()
                            .withImage(stPost.child(holderPost.getImages().get(0).getImageid()))
                            .show();
                }
                if(holder.multimediaView.isAudio()) {

                    ExpandMediaDialog dialog = new ExpandMediaDialog(mUserActivity);
                    dialog.create()
                            .withAudio(stPost.child(holderPost.getAudio().getAid()))
                            .show();

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
                        Log.e(TAG, holder.getAdapterPosition() + " " + holderPost.getMessage());
                        dbManager.deletePost(holderPost);
                        mPostList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        //notifyItemRangeChanged(holder.getAdapterPosition(), mPostList.size());
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

}
