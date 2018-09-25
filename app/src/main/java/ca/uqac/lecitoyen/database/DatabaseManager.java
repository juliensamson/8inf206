package ca.uqac.lecitoyen.database;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;


public class DatabaseManager  {

    private static DatabaseManager mInstance = null;
    private String mUserId;
    private DatabaseReference mDbRef;

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
    public void writeUserInformation(DatabaseReference db, UserData userData) {
        db.child("users").child(userData.getUserID().toString()).setValue(userData);
    }

}
