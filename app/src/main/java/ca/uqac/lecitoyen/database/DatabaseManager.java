package ca.uqac.lecitoyen.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


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
    public void writeUpvoteToPost(User user, Post post) {
        /*

            Update "posts/post-id/" reference

         */
        DatabaseReference postRef = getDatabasePost(post.getPostid());
        postRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        postRef.child(CHILD_UPVOTE_USERS).setValue(post.getUpvoteUsers());

        /*

            Update "user-posts/user-id/post-id/ reference

         */
        DatabaseReference userPostRef = getDatabaseUserPost(user.getUid(), post.getPostid());
        userPostRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        userPostRef.child(CHILD_UPVOTE_USERS).setValue(post.getUpvoteUsers());

        /*

        Update "users/user-id/" reference

         */
        DatabaseReference userRef = getDatabaseUser(user.getUid());

        //  Create the list of upvote posts by user if doesn't exist
        Post postTemp = new Post(post.getPostid());
        Map<String, Post> posts = new HashMap<>();
        if(user.getUpvotePosts() != null) {
            if (!user.getUpvotePosts().isEmpty())
                posts = user.getUpvotePosts();
            else
                posts = new HashMap<>();
        }
        posts.put(postTemp.getPostid(), postTemp);
        user.setUpvoteCount(posts.size());
        user.setUpvotePosts(posts);

        userRef.child(CHILD_UPVOTE_COUNT).setValue(user.getUpvoteCount());
        userRef.child(CHILD_UPVOTE_POSTS).setValue(user.getUpvotePosts());
    }

    public void removeUpvoteFromPost(User user, Post post) {
        /*

            Update "posts/post-id/" reference

         */
        DatabaseReference postRef = getDatabasePost(post.getPostid());
        postRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        postRef.child(CHILD_UPVOTE_USERS).child(user.getUid()).removeValue();

        /*

            Update "user-posts/user-id/post-id/ reference

         */
        DatabaseReference userPostRef = getDatabaseUserPost(user.getUid(), post.getPostid());
        userPostRef.child(CHILD_UPVOTE_COUNT).setValue(post.getUpvoteCount());
        userPostRef.child(CHILD_UPVOTE_USERS).child(user.getUid()).removeValue();

         /*

            Update "users/user-id/" reference

         */
        DatabaseReference userRef = getDatabaseUser(user.getUid());

        Map<String, Post> posts;
        if(user.getUpvotePosts() != null)
            posts = user.getUpvotePosts();
        else
            posts = new HashMap<>();

        if(posts.containsKey(post.getPostid()))
        {
            posts.remove(post.getPostid());
            user.setUpvoteCount(posts.size());
            user.setUpvotePosts(posts);

            userRef.child(CHILD_UPVOTE_COUNT).setValue(user.getUpvoteCount());
            userRef.child(CHILD_UPVOTE_POSTS).child(post.getPostid()).removeValue();
        }
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
        DatabaseReference userPostRef = getDatabaseUserPost(user.getUid(), post.getPostid());
        userPostRef.child(CHILD_REPOST_COUNT).setValue(post.getRepostCount());
        userPostRef.child(CHILD_REPOST_USERS).setValue(post.getRepostUsers());

        /*

        Update "users/user-id/" reference

         */
        DatabaseReference userRef = getDatabaseUser(user.getUid());

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
        DatabaseReference userPostRef = getDatabaseUserPost(user.getUid(), post.getPostid());
        userPostRef.child(CHILD_REPOST_COUNT).setValue(post.getRepostCount());
        userPostRef.child(CHILD_REPOST_USERS).child(user.getUid()).removeValue();

         /*

            Update "users/user-id/" reference

         */
        DatabaseReference userRef = getDatabaseUser(user.getUid());

        Map<String, Post> posts;
        if(user.getRepostPosts() != null)
            posts = user.getRepostPosts();
        else
            posts = new HashMap<>();

        if(posts.containsKey(post.getPostid()))
        {
            posts.remove(post.getPostid());
            user.setRepostCount(posts.size());
            user.setRepostPosts(posts);

            userRef.child(CHILD_REPOST_COUNT).setValue(user.getRepostCount());
            userRef.child(CHILD_REPOST_POSTS).child(post.getPostid()).removeValue();
        }
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
