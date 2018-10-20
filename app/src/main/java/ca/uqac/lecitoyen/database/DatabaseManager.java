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

    public static String CHILD_USERS = "users";
    public static String CHILD_USER_PROFIL_PICTURE = "user-profil-picture";
    public static String CHILD_POSTS = "posts";
    public static String CHILD_USER_POST = "user-post";
    public static String CHILD_POST_STORAGE = "post-storage";
    public static String ORDER_BY_DATE = "inverseDate";

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

    public Query getDatabasePostsOrderByDate() {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_POSTS)
                .orderByChild(ORDER_BY_DATE);
    }

    public DatabaseReference getDatabaseUserPost(String uid) {
        return FirebaseDatabase.getInstance().getReference()
                .child(CHILD_USER_POST)
                .child(uid);
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

    @Exclude
    public void writePost(DatabaseReference db, Post post) {
        Log.d(TAG, "writePost");

        String key = db.child("posts").push().getKey();
        post.setPostid(key);
        Map<String, Object> postValues = post.toMap();

        //  write on firebase
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-post/" + post.getUid() + "/" + key, postValues);

        db.updateChildren(childUpdates);
    }
}
