package ca.uqac.lecitoyen.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
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

    ArrayList<PostTest> postList = new ArrayList<>();
    final ArrayList<User> userList = new ArrayList<>();

    final private static String currentTimeMillis = Long.toString(System.currentTimeMillis());

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
    public void writeUserInformation(DatabaseReference db, User user) {
        db.child("users").child(user.getUid().toString()).setValue(user);
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

    //TODO: FIND A WAY TO CREATE ID FOR EACH THREADS
    @Exclude
    public void writePost(DatabaseReference db, PostTest post) {
        Log.d(TAG, "writePost");
        String key = db.child("posts").push().getKey();
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-post/" + post.getUserId() + "/" + key, postValues);

        db.updateChildren(childUpdates);
    }

    //  Read data

    public ArrayList<PostTest> getPostListOrderByDate() {

        Log.d(TAG, "getPostList");

        DatabaseManager.getInstance().getReference()
                .child("posts").orderByChild("inverseDate")
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        postList.clear();
                        final long[] pendingLoadCount = { dataSnapshot.getChildrenCount() };
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            postList.add(postSnapshot.getValue(PostTest.class));

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getDetails());
                    }
                });
        return postList;
    }

    public ArrayList<PostTest> getPostListOnChildChangeOrderByDate() {

        Log.d(TAG, "getPostListChield");

        DatabaseManager.getInstance().getReference()
                .child("posts")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        postList.add(dataSnapshot.getValue(PostTest.class));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getDetails());
                    }
                });

        return postList;
    }

    public ArrayList<User> getUserList() {

        Log.d(TAG, "getUserList");
        DatabaseManager.getInstance().getReference()
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            userList.add(user);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getDetails());
                    }
                });
        Log.w(TAG, "UserSize: " + userList.size());
        return userList;
    }


}
