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

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.database.DatabaseManager;
import ca.uqac.lecitoyen.database.Post;
import ca.uqac.lecitoyen.database.PostHistory;
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
    //private DatabaseReference db
    //private DatabaseReference dbPost;

    private DatabaseReference dbPostUpvotes;
    private DatabaseReference dbUserUpvotes;

    private DatabaseReference dbUserReposts;
    private DatabaseReference dbPostReposts;

    private String mCurrentUserId;
    private Post holderPost;
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
        if(fbUser != null) this.mCurrentUserId = fbUser.getUid();
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

               Get the information

         */

        holderPost = mPostList.get(holder.getAdapterPosition());
        if(mPostList.get(holder.getAdapterPosition()).getUpvoteUsers() != null)
            mUpvoteUsers = mPostList.get(holder.getAdapterPosition()).getUpvoteUsers();
        /*

               Views

         */
        setAdapterViews(holder, holderPost);

        /*

               OnClickListener

        */
        setMessageOnClickListener(holder, holderPost);

        setProfilOnClickListener(holder, holderPost);

        //setSocialOnClickListener(holder, currPost);

        //test
        updateSocialInteraction(holder, holderPost);
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
            User user = post.getUser();

            StorageReference storage = dbManager.getStorageUserProfilPicture(user.getUid());
            StorageReference stPosts = dbManager.getStoragePost(post.getPostid());

            if(user.getPid() != null && !user.getPid().isEmpty())                                   //User's profil picture
                Glide.with(mContext).load(storage.child(user.getPid())).into(holder.profilPicture);
            if(user.getName() != null && !user.getName().isEmpty())                                 //User's name
                holder.name.setText(post.getUser().getName());
            if(user.getUsername() != null && !user.getUsername().isEmpty())                         //User's username
                holder.userName.setText(user.getUsername());
            if(post.getMessage() != null && !post.getMessage().isEmpty())                                 //Post message
                holder.message.setText(post.getMessage());
            if(post.getDate() != 0)                                                                 //Time of publication
                holder.time.setText(getTimeDifference(post));
            if(post.getHistories().size() > 1)                                                  //Show if post modified
                holder.modify.setVisibility(View.VISIBLE);
            if(post.getImages() != null) {
                if (!post.getImages().get(0).getImageId().isEmpty()){
                    holder.pictureLayout.setVisibility(View.VISIBLE);
                    Glide.with(mContext).load(stPosts.child(post.getImages().get(0).getImageId())).into(holder.picture); //Fill ImageView
                }
            }
            holder.upvoteCount.setText(String.valueOf(post.getUpvoteCount()));
            //holder.repostCount.setText(String.valueOf(post.getRepostCount()));

            DatabaseReference dbPost = dbManager.getDatabasePost(post.getPostid());
            dbPost.addListenerForSingleValueEvent(readPostOnce(holder, post));

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

    private void updateSocialInteraction(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost) {
        //Access reference to databse for Upvotes
        dbUserUpvotes = dbManager.getDatabaseUserUpvotes(fbUser.getUid());
        Log.d(TAG, "Postid is " + holderPost.getPostid());

        DatabaseReference dbPost = dbManager.getDatabasePost(holderPost.getPostid());
        dbPost.addValueEventListener(readPostUpdate(holder, holderPost));

        Log.e(TAG, holderPost.getPostid());
        //dbManager.getReference().addValueEventListener(read(holder, holderPost));
        //dbUserUpvotes.addValueEventListener(readUserUpvotes(holder, holderPost));
        //dbPostUpvotes.addValueEventListener(readPostUpvotes(holder, holderPost));
        //dbManager.getReference().addValueEventListener(updateUpvoteOnClick(holder, holderPost));

        //Access reference to databse for Reposts
        //dbUserReposts = dbManager.getDatabaseUserReposts(holderPost.getUser().getUid());
        //dbPostReposts = dbManager.getDatabasePostReposts(holderPost.getPostid());
    }

    private ValueEventListener readPostOnce(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final DataSnapshot upvoteUsersSnapshot = dataSnapshot.child("upvoteUsers");

                Log.e(TAG, upvoteUsersSnapshot.toString());
                /*

                    Initialize

                 */

                //  Get upvote users
                final ArrayList<User> users = new ArrayList<>();
                for(DataSnapshot userSnapshot: upvoteUsersSnapshot.getChildren()) {
                    users.add(userSnapshot.getValue(User.class));
                }
                //  Get users count
                holderPost.setUpvoteCount(users.size());

                //  Find if users upvote the post and initialize
                if(!users.isEmpty()) {
                    for(int i = 0; i < users.size(); i++) {
                        Log.d(TAG, users.get(i).getUid());
                        if(users.get(i).getUid().equals(mCurrentUserId)) {
                            Log.d(TAG, mCurrentUserId + " liked this post " + holderPost.getMessage());
                            setUpvoteButtonOn(holder);
                            break;
                        } else {
                            Log.d(TAG, mCurrentUserId + " did not like this post " + holderPost.getMessage());
                            setUpvoteButtonOff(holder);
                        }
                    }
                } else {
                    Log.e(TAG, "No user upvoted this post");
                }
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

                 /*

                    Handle OnClick

                 */

                //  Get upvote users
                final DataSnapshot upvoteUsersSnapshot = dataSnapshot.child("upvoteUsers");

                final ArrayList<User> users = new ArrayList<>();
                for(DataSnapshot userSnapshot: upvoteUsersSnapshot.getChildren()) {
                    users.add(userSnapshot.getValue(User.class));
                }

                //  Set the user id
                final User user = new User();
                user.setUid(mCurrentUserId);

                holder.upvoteLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference postRef = dbManager.getDatabasePost(holderPost.getPostid());
                        DatabaseReference dbPostUpvoteCount = postRef.child("upvoteCount");
                        DatabaseReference dbPostUpvoteUsers = postRef.child("upvoteUsers");

                        Log.e(TAG, "POSTID ?:  " + holderPost.getPostid());
                        Log.e(TAG, mCurrentUserId + " " + String.valueOf(isUpvoteByUser));
                        if(!isUpvoteByUser) {
                            Log.e(TAG, "isUpvoteByUser " + String.valueOf(isUpvoteByUser));
                            setUpvoteButtonOn(holder);
                            //Add user to list
                            users.add(user);
                            holderPost.setUpvoteUsers(users);
                            holderPost.setUpvoteCount(users.size());

                            //Update database (Add user to post, and add post to user
                            dbPostUpvoteCount.setValue(holderPost.getUpvoteCount());
                            dbPostUpvoteUsers.setValue(holderPost.getUpvoteUsers());
                            //update user too

                            //Update UI
                        } else {
                            Log.e(TAG, "isUpvoteByUser " + String.valueOf(isUpvoteByUser));
                            //Remove user from list
                            setUpvoteButtonOff(holder);
                            Log.i(TAG, "Users Count Before" + users.size());
                            for(int i = 0; i < users.size(); i++) {
                                if (users.get(i).getUid().equals(mCurrentUserId)) {
                                    Log.d(TAG, mCurrentUserId + " liked this post " + holderPost.getMessage());
                                    users.remove(i);
                                    holderPost.setUpvoteCount(users.size());
                                    holderPost.setUpvoteUsers(users);
                                    Log.i(TAG, "Users Count After" + users.size());

                                    dbPostUpvoteCount.setValue(holderPost.getUpvoteCount());
                                    dbPostUpvoteUsers.child(String.valueOf(i)).removeValue();

                                    break;
                                }
                            }
                        }
                        holder.upvoteCount.setText(String.valueOf(holderPost.getUpvoteCount()));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener updateUpvoteOnClick(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot)
            {
                //  Check list completed list of publication & check if it correspond to the upvoted one by the use
                Log.e(TAG, String.valueOf(dataSnapshot.getChildrenCount()));
                holder.upvoteLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Upvote layout clicked");

                        String currentUserId = fbUser.getUid();
                        String currentPostId = holderPost.getPostid();

                        DatabaseReference dbUserUpvotesClicked = dbManager.getDatabaseUserUpvotes(currentUserId);
                        DatabaseReference dbPostUpvotesClicked = dbManager.getDatabasePostUpvotes(currentPostId);

                        Log.e(TAG, String.valueOf(dataSnapshot.getChildrenCount()));

                        User user1 = new User();
                        user1.setUid(currentUserId);

                        if (!isUpvoteByUser)
                        {
                            /*

                                    Update upvotes made by the user

                             */
                            mUpvoteUsers.add(user1);
                            holderPost.setUpvoteUsers(mUpvoteUsers);
                            dbManager.getReference()
                                    .child("posts")
                                    .child(currentPostId)
                                    .child("upvoteUsers")
                                    .setValue(holderPost.getUpvoteUsers());

                            Post post = new Post();
                            post.setPostid(currentPostId);
                            Map<String, Object> userUpvotes = new HashMap<>();
                            userUpvotes.put(post.getPostid(), post);
                            //  Put current post as upvoted in the user
                            dbUserUpvotesClicked.updateChildren(userUpvotes);
                            //holderPost.setUpvote(dataSnapshot.getChildrenCount());

                            /*

                                    Update the post social interaction

                             */
                            User user = new User();
                            user.setUid(fbUser.getUid());
                            Map<String, Object> postUpvotes = new HashMap<>();
                            postUpvotes.put(user.getUid(), user);
                            //  Put current user as someone who upvotes the post
                            dbPostUpvotesClicked.updateChildren(postUpvotes);

                            Log.d(TAG, "Current postid: " + post.getPostid());
                            Log.d(TAG, "Current userid: " + user.getUid());

                            setUpvoteButtonOn(holder);
                        } else {
                            //  Remove the user from the upvoted posted
                            dbUserUpvotesClicked.child(currentPostId).removeValue();
                            //  Remove the post from what the user upvoted
                            dbPostUpvotesClicked.child(currentUserId).removeValue();
                            dbManager.getReference()
                                    .child("posts")
                                    .child(currentPostId)
                                    .child(currentUserId).removeValue();
                            mUpvoteUsers.remove(user1);

                            setUpvoteButtonOff(holder);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private ValueEventListener readUserUpvotes(@NonNull final PublicationAdapter.ViewHolder holder, final Post holderPost) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e(TAG, String.valueOf(dataSnapshot.getChildrenCount()));

                for (DataSnapshot postUpvotedByUser : dataSnapshot.getChildren()) {
                    Post postUpvoted = postUpvotedByUser.getValue(Post.class);
                    if(postUpvoted != null) {
                        Log.e(TAG, postUpvoted.getPostid());

                    }
                }
                /*ArrayList<Post> listUpvoted = new ArrayList<>();
                for (DataSnapshot postUpvotedByUser : dataSnapshot.getChildren()) {
                    listUpvoted.add(postUpvotedByUser.getValue(Post.class));
                }
                Log.i(TAG, String.valueOf(mPostList.size()));
                for (int i = 0; i < mPostList.size(); i++)
                {
                    for(int j = 0; j < listUpvoted.size(); j++)
                    {
                        if (mPostList.get(i).getPostid().equals(listUpvoted.get(j).getPostid())) {
                            Log.e(TAG, String.valueOf(isUpvoteByUser));
                            Log.e(TAG, mPostList.get(i).getPostid());
                            Log.e(TAG, listUpvoted.get(j).getPostid());
                            setUpvoteButtonOn(holder);
                            break;
                        } else {
                            Log.e(TAG, String.valueOf(isUpvoteByUser));
                            setUpvoteButtonOff(holder);
                        }
                    }
                }*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        };
    }

    private ValueEventListener readPostUpvotes(@NonNull final PublicationAdapter.ViewHolder holder, Post holderPost) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private void initUpvoteInteraction(@NonNull final PublicationAdapter.ViewHolder holder, Post holderPost, Post userPost) {
        //Log.e(TAG, post.getPostid() + " " + post.getPostid());
        if (holderPost.getPostid().equals(userPost.getPostid())) {
            setUpvoteButtonOn(holder);
        } else {
            setUpvoteButtonOff(holder);
        }
    }

    private void setUpvoteOnClick(@NonNull final PublicationAdapter.ViewHolder holder, final Post post) {

        holder.upvoteLayout.setOnClickListener(new View.OnClickListener() {
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
        });
    }

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

    private ValueEventListener checkValuePostUpvotes(@NonNull final PublicationAdapter.ViewHolder holder, final Post post) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e(TAG, "# of children: " + String.valueOf(dataSnapshot.getChildrenCount()));

                holder.upvoteCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    private  ChildEventListener checkPostUpvotes(@NonNull final PublicationAdapter.ViewHolder holder, final Post post) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.e(TAG, "# of children: " + String.valueOf(dataSnapshot.getChildrenCount()));
                mPostUpvotes.clear();
                if(dataSnapshot.exists()) {
                    mPostUpvotes.add(dataSnapshot.getValue().toString());
                    Log.e(TAG, "Child added " + String.valueOf(mPostUpvotes.size()));
                    holder.upvoteCount.setText(String.valueOf(mPostUpvotes.size()));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e(TAG, "Child changed");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    Log.e(TAG, "# of children: " + String.valueOf(dataSnapshot.getChildrenCount()));
                    mPostUpvotes.remove(dataSnapshot.getValue().toString());
                    Log.e(TAG, "Removed " + String.valueOf(mPostUpvotes.size()));
                    holder.upvoteCount.setText(String.valueOf(mPostUpvotes.size()));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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
