package ca.uqac.lecitoyen.database;

import android.util.Log;

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
    private final static String CHILD_USER_SOCIAL = "user-social";
    private final static String CHILD_USER_PROFIL_PICTURE = "user-profil-picture";

    private final static String CHILD_USER_POSTS = "user-posts";
    private final static String CHILD_POSTS = "posts";
    private final static String CHILD_POST_SOCIAL = "post-social";

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

    public DatabaseReference getDatabasePosts() {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS);
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
