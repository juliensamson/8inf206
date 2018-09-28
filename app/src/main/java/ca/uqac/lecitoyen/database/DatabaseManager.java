package ca.uqac.lecitoyen.database;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class DatabaseManager  {

    private static String TAG = "DatabaseManager";

    private static DatabaseManager mInstance = null;
    private String mUserId;
    private DatabaseReference mDbRef;

    final private static String currentTimeMillis = Long.toString(System.currentTimeMillis());

    public static synchronized DatabaseManager getInstance()
    {
        if(mInstance == null)
            mInstance = new DatabaseManager();
        return mInstance;
    }

    private DatabaseManager() {
        mDbRef = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getReference() {
        return mDbRef;
    }

    @Exclude
    public void writeUserInformation(DatabaseReference db, User user) {
        db.child("users").child(user.getUserID().toString()).setValue(user);
    }

    //TODO: FIND A WAY TO CREATE ID FOR EACH THREADS
    @Exclude
    public void writePostMessage(DatabaseReference db, Post post) {
        Log.d(TAG, "writePostMessage");
        //db.child("threads").child(currentTimeMillis).setValue(postData);
        String key = db.child("threads").push().getKey();
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/threads/" + key, postValues);
        childUpdates.put("/user-post/" + post.getUserId() + "/" + key, postValues);

        db.updateChildren(childUpdates);
    }

}
