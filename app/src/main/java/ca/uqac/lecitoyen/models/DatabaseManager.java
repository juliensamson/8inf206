package ca.uqac.lecitoyen.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class DatabaseManager  {

    private static String TAG = "DatabaseManager";

    private final static String CHILD_USERS = "users";
    private final static String CHILD_UPVOTE_POSTS = "upvotePosts";
    private final static String CHILD_REPOST_POSTS = "repostPosts";
    private final static String CHILD_USER_SOCIAL = "user-social";
    private final static String CHILD_USER_PROFIL_PICTURE = "user-profil-picture";


    private final static String CHILD_USER_POSTS = "user-posts";
    private final static String CHILD_POSTS = "posts";
    private final static String CHILD_POST_SOCIAL = "post-social";
    private final static String CHILD_UPVOTE_USERS = "upvoteUsers";
    private final static String CHILD_UPVOTE_COUNT = "upvoteCount";
    private final static String CHILD_REPOST_USERS = "repostUsers";
    private final static String CHILD_REPOST_COUNT = "repostCount";

    public final static String CHILD_UPVOTES = "upvotes";
    public final static String CHILD_REPOSTS = "reposts";
    public final static String CHILD_COMMENTS = "comments";

    private final static String CHILD_POST_STORAGE = "post-storage";

    private static DatabaseManager mInstance = null;
    private String mUserId;
    private DatabaseReference mRootRef;


    public static synchronized DatabaseManager getInstance() {
        if(mInstance == null)
            mInstance = new DatabaseManager();
        return mInstance;
    }

    private DatabaseManager() {
        mRootRef = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getReference() {
        return mRootRef;
    }

    /*

            Database reference

     */

    public DatabaseReference getDatabaseUser(String uid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(DatabaseManager.CHILD_USERS)
                .child(uid);
    }

    public DatabaseReference getDatabaseUsers() {
        return FirebaseDatabase.getInstance().getReference()
                .child(DatabaseManager.CHILD_USERS);
    }

    public DatabaseReference getDatabaseUserProfilPicture(String uid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_USER_PROFIL_PICTURE)
                .child(uid);
    }

    public DatabaseReference getDatabaseUserUpvotes(String uid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_USER_SOCIAL)
                .child(uid)
                .child(CHILD_UPVOTES);
    }

    public DatabaseReference getDatabaseUserReposts(String uid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_USER_SOCIAL)
                .child(uid)
                .child(CHILD_REPOSTS);
    }

    public DatabaseReference getDatabaseUserComments(String uid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_USER_SOCIAL)
                .child(uid)
                .child(CHILD_COMMENTS);
    }

    /*

            Handle a post reference

     */

    public DatabaseReference getDatabasePosts() {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS);
    }

    public DatabaseReference getDatabasePost(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS)
                .child(postid);
    }

    public DatabaseReference getDatabasePostUpvoteCount(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS)
                .child(postid)
                .child(CHILD_UPVOTE_COUNT);
    }

    public DatabaseReference getDatabasePostUpvoteUsers(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS)
                .child(postid)
                .child(CHILD_UPVOTE_USERS);
    }

    public DatabaseReference getDatabasePostRepostCount(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS)
                .child(postid)
                .child(CHILD_REPOST_COUNT);
    }

    public DatabaseReference getDatabasePostRepostUsers(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS)
                .child(postid)
                .child(CHILD_REPOST_USERS);
    }

    /*

            Handle a user reference

     */

    public DatabaseReference getDatabaseUserPost(String uid, String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_USER_POSTS)
                .child(uid)
                .child(postid);
    }

    public DatabaseReference getDatabaseUserPosts(String uid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_USER_POSTS)
                .child(uid);
    }

    public DatabaseReference getDatabasePostUpvotes(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POST_SOCIAL)
                .child(postid)
                .child(CHILD_UPVOTES);
    }

    public DatabaseReference getDatabasePostReposts(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POST_SOCIAL)
                .child(postid)
                .child(CHILD_REPOSTS);
    }

    public DatabaseReference getDatabasePostComments(String postid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POST_SOCIAL)
                .child(postid)
                .child(CHILD_COMMENTS);
    }

    /*

            Storage reference

     */

    public StorageReference getStoragePost(String postId) {
        return FirebaseStorage.getInstance().getReference()
                .child(CHILD_POST_STORAGE)
                .child(postId);
    }

    public StorageReference getStorageUserProfilPicture(String uid) {
        return FirebaseStorage.getInstance().getReference()
                .child(CHILD_USER_PROFIL_PICTURE)
                .child(uid);
    }

    /*

            Write data into firebase

     */

    public void deleteHolderPost(Post holderPost) {
        getDatabasePost(holderPost.getPostid()).removeValue();
        getDatabaseUserPost(holderPost.getUser().getUid(), holderPost.getPostid()).removeValue();
        getStoragePost(holderPost.getPostid()).delete();
        //Delete Repost & Like of user of post is deleted
    }

    public void writeUpvoteToPost(final User user, final Post post) {

        /*

           Update "posts/post-id/" reference

        */
        DatabaseReference postRef = getDatabasePost(post.getPostid());
        postRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        postRef.child(CHILD_UPVOTE_USERS).setValue(post.getUpvoteUsers());

        /*

            Update "user-posts/user-id/post-id/ reference

        */
        DatabaseReference userPostRef = getDatabaseUserPost(post.getUser().getUid(), post.getPostid());
        userPostRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        userPostRef.child(CHILD_UPVOTE_USERS).setValue(post.getUpvoteUsers());


        /*final DatabaseReference userRef = getDatabaseUser(user.getUid());
        User userTemps
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userTemps = dataSnapshot.getValue(User.class);
                if (userTemps != null) {


                    Update "users/user-id/" reference


                    Map<String, Post> posts;
                    Post postTemps = new Post(post.getPostid());
                    if(userTemps.getUpvotePosts() != null)
                        posts = userTemps.getUpvotePosts();
                    else
                        posts = new HashMap<>();

                    posts.put(postTemps.getPostid(), postTemps);
                    userTemps.setUpvotePosts(posts);
                    userTemps.setUpvoteCount(posts.size());

                    //DatabaseReference userRef = getDatabaseUser(user.getUid());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef.child(CHILD_UPVOTE_COUNT).setValue(userTemps.getUpvoteCount());
        userRef.child(CHILD_UPVOTE_POSTS).setValue(userTemps.getUpvotePosts());
        */
    }

    public void updateUserdata(final User user) {

        // Update User
        //  Update User
        DatabaseReference userData = getDatabaseUser(user.getUid());
        userData.setValue(user);

        // Update user-post
        final DatabaseReference dbUserPosts = getDatabaseUserPosts(user.getUid());
        dbUserPosts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userPostSnapshot: dataSnapshot.getChildren()) {
                    final Post userPost = userPostSnapshot.getValue(Post.class);
                    if(userPost != null) {
                        userPost.setUser(user);
                        dbUserPosts.child(userPost.getPostid()).setValue(userPost);

                        final DatabaseReference dbPosts = getDatabasePosts();
                        dbPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    Post post = postSnapshot.getValue(Post.class);
                                    if(post != null) {
                                        if (post.getPostid().equals(userPost.getPostid())) {
                                            post.setUser(user);
                                            dbPosts.child(post.getPostid()).setValue(post);
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });

        // Update post
        //DatabaseReference post = getDatabaseUser(user.getUid());
        //userData.setValue(user);

    }

    public void removeUpvoteFromPost(final User user, final Post post) {

        /*

            Update "posts/post-id/" reference

         */
        DatabaseReference postRef = getDatabasePost(post.getPostid());
        postRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        postRef.child(CHILD_UPVOTE_USERS).child(user.getUid()).removeValue();

        /*

            Update "user-posts/user-id/post-id/ reference

         */
        DatabaseReference userPostRef = getDatabaseUserPost(post.getUser().getUid(), post.getPostid());
        userPostRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        userPostRef.child(CHILD_UPVOTE_USERS).child(user.getUid()).removeValue();


        /*
        final DatabaseReference userRef = getDatabaseUser(user.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userTemps = dataSnapshot.getValue(User.class);
                if (userTemps != null) {

                    Map<String, Post> posts;
                    if (userTemps.getUpvotePosts() != null)
                        posts = userTemps.getUpvotePosts();
                    else
                        posts = new HashMap<>();

                    posts.remove(post.getPostid());
                    userTemps.setUpvoteCount(posts.size());
                    userTemps.setUpvotePosts(posts);

                    userRef.child(CHILD_UPVOTE_COUNT).setValue(userTemps.getUpvoteCount());
                    userRef.child(CHILD_UPVOTE_POSTS).child(post.getPostid()).removeValue();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
    }

    public void writeRepostToPost(User user, Post post) {
/*

            Update "posts/post-id/" reference

         */
        DatabaseReference postRef = getDatabasePost(post.getPostid());
        postRef.child(CHILD_REPOST_COUNT).setValue(post.getRepostCount());
        postRef.child(CHILD_REPOST_USERS).setValue(post.getRepostUsers());

        /*

            Update "user-posts/user-id/post-id/ reference

         */
        DatabaseReference userPostRef = getDatabaseUserPost(post.getUser().getUid(), post.getPostid());
        userPostRef.child(CHILD_REPOST_COUNT).setValue(post.getRepostCount());
        userPostRef.child(CHILD_REPOST_USERS).setValue(post.getRepostUsers());


        //TODO: DON'T WORK BECAUSE the User only contain the userid
        /*

        Update "users/user-id/" reference

         */

        /*DatabaseReference userRef = getDatabaseUser(user.getUid());

        //  Create the list of Repost posts by user if doesn't exist
        Post postTemp = new Post(post.getPostid());
        Map<String, Post> posts = new HashMap<>();
        if(user.getRepostPosts() != null) {
            if (!user.getRepostPosts().isEmpty())
                posts = user.getRepostPosts();
            else
                posts = new HashMap<>();
        }
        posts.put(postTemp.getPostid(), postTemp);
        user.setRepostCount(posts.size());
        user.setRepostPosts(posts);

        userRef.child(CHILD_REPOST_COUNT).setValue(user.getRepostCount());
        userRef.child(CHILD_REPOST_POSTS).setValue(user.getRepostPosts());
        */
    }

    public void removeRepostFromPost(User user, Post post) {
/*

            Update "posts/post-id/" reference

         */
        DatabaseReference postRef = getDatabasePost(post.getPostid());
        postRef.child(CHILD_REPOST_COUNT).setValue(post.getRepostCount());
        postRef.child(CHILD_REPOST_USERS).child(user.getUid()).removeValue();

        /*

            Update "user-posts/user-id/post-id/ reference

         */
        DatabaseReference userPostRef = getDatabaseUserPost(post.getUser().getUid(), post.getPostid());
        userPostRef.child(CHILD_REPOST_COUNT).setValue(post.getRepostCount());
        userPostRef.child(CHILD_REPOST_USERS).child(user.getUid()).removeValue();

         /*

            Update "users/user-id/" reference

         */
         /*
        DatabaseReference userRef = getDatabaseUser(user.getUid());

        Map<String, Post> posts;
        if(user.getRepostPosts() != null)
            posts = user.getRepostPosts();
        else
            posts = new HashMap<>();

        //if(posts.containsKey(post.getPostid()))
        //{
            posts.remove(post.getPostid());
            user.setRepostCount(posts.size());
            user.setRepostPosts(posts);

            userRef.child(CHILD_REPOST_COUNT).setValue(user.getRepostCount());
            userRef.child(CHILD_REPOST_POSTS).child(post.getPostid()).removeValue();
        //}
        */
    }

    @Exclude
    public void writeUserInformation(DatabaseReference db, User userdata) {
        if(!userdata.getUid().equals("") && userdata.getUid() != null)
            db.child("users").child(userdata.getUid()).setValue(userdata);
        else {
            String key = db.child("users").push().getKey();
            db.child("users").child(key).setValue(userdata);
        }
    }
}
