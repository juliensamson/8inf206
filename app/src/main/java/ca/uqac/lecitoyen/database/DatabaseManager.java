package ca.uqac.lecitoyen.database;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DatabaseManager  {

    private static String TAG = "DatabaseManager";

    private static DatabaseManager mInstance = null;
    private String mUserId;
    private DatabaseReference mRootRef;


    public static synchronized DatabaseManager getInstance()
    {
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

    @Exclude
    public void writeUserInformation(DatabaseReference db, String uid, User userdata) {
        db.child("users").child(uid).setValue(userdata);
    }

    @Exclude
    public void writePost(DatabaseReference db, Post post) {
        Log.d(TAG, "writePost");

        String key = db.child("posts").push().getKey();
        Map<String, Object> postValues = post.toMap();

        //  write on firebase
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-post/" + post.getUserId() + "/" + key, postValues);

        db.updateChildren(childUpdates);
    }

}
