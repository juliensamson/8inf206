package ca.uqac.lecitoyen.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import ca.uqac.lecitoyen.BaseFragment;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostHistory;
import ca.uqac.lecitoyen.database.User;
import ca.uqac.lecitoyen.database.UserStorage;
import ca.uqac.lecitoyen.userUI.newsfeed.EditPostActivity;
import ca.uqac.lecitoyen.utility.CustumButton;
import ca.uqac.lecitoyen.utility.TimeUtility;
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
    private TimeUtility timeUtility;

    private User mCurrentUser;
    private String mCurrentUserId;

    private ArrayList<Post> mPostList = new ArrayList<>();
    private ArrayList<String> mPostUpvotes = new ArrayList<>();
    private ArrayList<User> mUpvoteUsers = new ArrayList<>();
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserStorage> mProfilPictureList = new ArrayList<>();

    private boolean isUpvoteByUser = false;
    private boolean isRepostByUser = false;

    private CustumButton mCustumButton;

    /*

            Constructor & Viewholder

     */

    public PublicationAdapter(Context context, FirebaseUser user, ArrayList<Post> postList) {
        Log.d(TAG, "PublicationAdapter");
        this.mContext = context;
        this.fbUser = user;
        this.mPostList = postList;
        if(fbUser != null) {
            this.mCurrentUserId = fbUser.getUid();
            this.mCurrentUser = new User(mCurrentUserId);
        }

        this.timeUtility = new TimeUtility(mContext);
    }

    public PublicationAdapter(Context context, ArrayList<Post> postList, ArrayList<String> upvotes) {
        Log.d(TAG, "PublicationAdapter2");
        this.mContext = context;
        this.mPostList = postList;
        this.mPostUpvotes = upvotes;
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

               Get the information about the post

         */
        final Post holderPost = mPostList.get(holder.getAdapterPosition());
        DatabaseReference dbPost = dbManager.getDatabasePost(holderPost.getPostid());

        /*

               Initialize views

         */
        setAdapterViews(holder, holderPost);

        /*

               OnClickListener

        */
        setMessageOnClickListener(holder, holderPost);

        setProfilOnClickListener(holder, holderPost);

        //setSocialOnClickListener(holder, currPost);

        //test
        dbPost.addValueEventListener(readPostUpdate(holder, holderPost));
    }

    /*

            Methods

     */

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    private void setAdapterViews(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost) {

        Log.d(TAG, "setAdapterViews");

        if(holderPost != null)
        {
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
                    holder.pictureLayout.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(stPosts.child(holderPost.getImages().get(0).getImageId())).into(holder.picture); //Fill ImageView
                }
            }
            holder.upvoteCount.setText(String.valueOf(holderPost.getUpvoteCount()));
            //holder.repostCount.setText(String.valueOf(post.getRepostCount()));

            DatabaseReference dbPost = dbManager.getDatabasePost(holderPost.getPostid());
            dbPost.addListenerForSingleValueEvent(initPost(holder, holderPost));

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

        if(currPost.getHistories().size() > 1) {
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

    /*


            Value Event Listener


     */

    private ValueEventListener initPost(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final DataSnapshot upvoteUsersSnapshot = dataSnapshot.child("upvoteUsers");

                //  Get upvote users
                /*final ArrayList<User> users = new ArrayList<>();
                for(DataSnapshot userSnapshot: upvoteUsersSnapshot.getChildren()) {
                    users.add(userSnapshot.getValue(User.class));
                }*/
                final Map<String, User> users = new HashMap<>();
                for(DataSnapshot userSnapshot: upvoteUsersSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(user != null)
                        users.put(user.getUid(), user);
                }

                //  Get users count
                holderPost.setUpvoteCount(users.size());

                //  Find if users upvote the post and initialize
                if(!users.isEmpty())
                {
                    if(users.containsKey(mCurrentUserId))
                        setUpvoteButtonOn(holder);
                    else
                        setUpvoteButtonOff(holder);
                } else {
                    Log.e(TAG, "No user upvoted this post");
                }

                /*if(!users.isEmpty())
                {
                    for(int i = 0; i < users.size(); i++) {
                        if(users.get(i).getUid().equals(mCurrentUserId)) {
                            setUpvoteButtonOn(holder);
                            break;
                        } else {
                            setUpvoteButtonOff(holder);
                        }
                    }
                } else {
                    Log.e(TAG, "No user upvoted this post");
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener readPostUpdate(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //TODO: Transformer en MAP pour éviter de répéter les données
                //TODO: Ajouter à utilisatueur et faire Repost

                //  Get upvote users
                final DataSnapshot upvoteUsersSnapshot = dataSnapshot.child("upvoteUsers");

                Log.e(TAG, "onDataChange" + upvoteUsersSnapshot.toString());

                /*final ArrayList<User> users = new ArrayList<>();
                for(DataSnapshot userSnapshot: upvoteUsersSnapshot.getChildren()) {
                    users.add(userSnapshot.getValue(User.class));
                }*/

                //Map<String, Object> userUpvotes = new HashMap<>();
                //post.setPostid(postid);
                //userUpvotes.put(postid, post);

                final Map<String, User> users = new HashMap<>();
                for(DataSnapshot userSnapshot: upvoteUsersSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if(user != null)
                        users.put(user.getUid(), user);
                }

                holder.upvoteLayout.setOnClickListener(setUpvoteOnClickListener(holder, holderPost, users));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private View.OnClickListener setUpvoteOnClickListener(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost, final Map<String, User> users) {

        final String currentPostId = holderPost.getPostid();
        final DatabaseReference dbPostUpvoteCount = dbManager.getDatabasePostUpvoteCount(currentPostId);
        final DatabaseReference dbPostUpvoteUsers = dbManager.getDatabasePostUpvoteUsers(currentPostId);

        //  Create User instance with current user
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isUpvoteByUser) {
                    setUpvoteButtonOn(holder);
                    //Add user to list
                    //users.add(user);
                    users.put(mCurrentUserId, mCurrentUser);
                    holderPost.setUpvoteUsers(users);
                    holderPost.setUpvoteCount(users.size());

                    //Update database (Add user to post, and add post to user
                    dbManager.writeUpvoteToPost(
                            holderPost,
                            dbPostUpvoteCount,
                            dbPostUpvoteUsers
                    );
                    //dbPostUpvoteCount.setValue(holderPost.getUpvoteCount());
                    //dbPostUpvoteUsers.setValue(holderPost.getUpvoteUsers());
                    //update user too

                    //Update UI
                } else {
                    //Remove user from list
                    setUpvoteButtonOff(holder);

                    if(users.containsKey(mCurrentUserId)) {
                        /*
                            Update data structure
                            - Remove current user from the list
                            - Update Upvote count with the new size of Users
                            - Update Upvote users with the new users
                         */
                        users.remove(mCurrentUserId);
                        holderPost.setUpvoteCount(users.size());
                        holderPost.setUpvoteUsers(users);

                        /*

                            Update fireabse with the updated structure

                         */
                        dbManager.removeUpvoteFromPost(
                                mCurrentUser,
                                holderPost,
                                dbPostUpvoteCount,
                                dbPostUpvoteUsers
                        );
                    } else
                        Log.e(TAG, mCurrentUserId + " didn't like this post");
                }
                holder.upvoteCount.setText(String.valueOf(holderPost.getUpvoteCount()));
            }
        };
    }

    private void setUpvoteOnClick(@NonNull final PublicationAdapter.ViewHolder holder, final Post post) {

        /*holder.upvoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    final String postid = post.getPostid();
                    final String uid = post.getUser().getUid();

                    if (!isUpvoteByUser)
                    {
                        //  Update by post-social interaction
                        User user = new User();
                        user.setUid(uid);

                        Map<String, Object> postUpvotes = new HashMap<>();
                        postUpvotes.put(uid, user);


                        dbPostUpvotes.updateChildren(postUpvotes);

                        //  Update by user-social interaction
                        Post post = new Post();
                        Map<String, Object> userUpvotes = new HashMap<>();
                        post.setPostid(postid);
                        userUpvotes.put(postid, post);
                        dbUserUpvotes.updateChildren(userUpvotes);

                        setUpvoteButtonOn(holder);
                    } else {
                        dbUserUpvotes.child(postid).removeValue();
                        dbPostUpvotes.child(uid).removeValue();

                        setUpvoteButtonOff(holder);
                    }
                }
            }
        });*/
    }

    /*

            Handle button color & effect

     */

    private void setUpvoteButtonOn(@NonNull final PublicationAdapter.ViewHolder holder) {
        isUpvoteByUser = true;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.upvote.setBackground(mContext.getDrawable(R.drawable.ic_upvote_analogous_24dp));
            holder.upvoteCount.setTextColor(mContext.getResources().getColor(R.color.i_analogous_g_300));
        }
        holder.upvote.setAlpha(SELECT_TRANSPARENCE);
        holder.upvoteCount.setAlpha(SELECT_TRANSPARENCE);
    }

    private void setUpvoteButtonOff(@NonNull final PublicationAdapter.ViewHolder holder) {
        isUpvoteByUser = false;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.upvote.setBackground(mContext.getDrawable(R.drawable.ic_upvote_black_24dp));
            holder.upvoteCount.setTextColor(mContext.getResources().getColor(R.color.black_900));
        }
        holder.upvote.setAlpha(UNSELECT_TRANSPARENCE);
        holder.upvoteCount.setAlpha(UNSELECT_TRANSPARENCE);
    }


    /*
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
    }*/

    private void showPostHistory(final Post currentPost) {
        RecyclerView mRecyclerView = new RecyclerView(mContext);

        RecyclerView.Adapter adapter = new PostHistoryAdapter(
                mContext,
                (ArrayList<PostHistory>) currentPost.getHistories());

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
                .child(currentPost.getUser().getUid())
                .child(currentPost.getPostid())
                .removeValue();
    }

}
