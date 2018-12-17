package ca.uqac.lecitoyen.buttons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.Interface.iToggleButton;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.User;

public class FollowButton extends FrameLayout implements iToggleButton {

    private static final String TAG = FollowButton.class.getSimpleName();

    private DatabaseManager dbManager;
    private String mUserAuthId;
    private User mUserSelect;

    private FrameLayout mFollowOn;
    private FrameLayout mFollowOff;
    private int mFollowersCount = 0;
    private int mFollowingsCount = 0;

    private boolean isFollowOn = false;

    public FollowButton(@NonNull Context context) {
        super(context);
        create(context);
    }

    public FollowButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        create(context);
    }

    public FollowButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create(context);
    }

    private void create(Context context) {
        dbManager = DatabaseManager.getInstance();
        View rootView = inflate(context, R.layout.button_follow, this);
        mFollowOn = rootView.findViewById(R.id.button_follow_layout_on);
        mFollowOff = rootView.findViewById(R.id.button_follow_layout_off);
        setButtonOff();
    }

    @Override
    public void setButtonOn() {
        if(mFollowOn != null)
            mFollowOn.setVisibility(VISIBLE);
        if(mFollowOff != null)
            mFollowOff.setVisibility(GONE);
        isFollowOn = true;
    }

    @Override
    public void setButtonOff() {
        if(mFollowOn != null)
            mFollowOn.setVisibility(GONE);
        if(mFollowOff != null)
            mFollowOff.setVisibility(VISIBLE);
        isFollowOn = false;
    }

    @Override
    public void setButtonCount(long count) {

    }

    @Override
    public boolean isButtonOn() {
        return isFollowOn;
    }

    public int getUserSelectFollowersCount() {
        return mFollowersCount;
    }

    public int getUserSelectFollowingsCount() {
        return mFollowingsCount;
    }

    public void setFollowOnClickListener(final FollowButton followButton, String userAuth, User userSelect) {

        Log.i(TAG, "setFollowOnClickListener");

        mUserAuthId = userAuth;
        mUserSelect = userSelect;

        dbManager.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange");

                DataSnapshot userSelectFollowersSnap = dataSnapshot
                        .child(DatabaseManager.CHILD_USER_FOLLOWERS)
                        .child(mUserSelect.getUid())
                        .child("is-followed-by");
                final Map<String, String> userSelectFollowers = getUsers(userSelectFollowersSnap);
                mFollowersCount = userSelectFollowers.size();

                DataSnapshot userAuthFollowingsSnap = dataSnapshot
                        .child(DatabaseManager.CHILD_USER_FOLLOWINGS)
                        .child(mUserAuthId)
                        .child("is-following");
                final Map<String, String> userAuthFollowings = getUsers(userAuthFollowingsSnap);
                mFollowingsCount = userAuthFollowings.size();

                if(!userAuthFollowings.isEmpty()) {


                    if(userAuthFollowings.containsKey(mUserSelect.getUid())) {
                        followButton.setButtonOn();
                    } else {
                        followButton.setButtonOff();
                    }

                } else {
                    Log.e(TAG, "empty");
                }

                followButton.setOnClickListener(follow(followButton, userSelectFollowers, userAuthFollowings));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private View.OnClickListener follow(final FollowButton followButton,
                                        final Map<String, String> userSelectFollowers,
                                        final Map<String, String> userAuthFollowings) {
        return new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!followButton.isButtonOn()) {

                    userSelectFollowers.put(mUserAuthId, mUserAuthId);
                    DatabaseReference userSelectFollowersRef = dbManager.getDatabaseUserFollowers(mUserSelect.getUid());
                    //userSelectFollowersRef.setValue(userSelectFollowers.size());
                    userSelectFollowersRef.child("is-followed-by").setValue(userSelectFollowers);


                    userAuthFollowings.put(mUserSelect.getUid(), mUserSelect.getUid());
                    DatabaseReference userAuthFollowingsRef = dbManager.getDatabaseUserFollowings(mUserAuthId);
                    //userAuthFollowingsRef.setValue(userAuthFollowings.size());
                    userAuthFollowingsRef.child("is-following").setValue(userAuthFollowings);

                    followButton.setButtonOn();
                } else {

                    userSelectFollowers.remove(mUserAuthId);
                    DatabaseReference userSelectFollowersRef = dbManager.getDatabaseUserFollowers(mUserSelect.getUid());
                    //userSelectFollowersRef.setValue(userSelectFollowers.size());
                    userSelectFollowersRef.child("is-followed-by")
                            .child(mUserAuthId)
                            .removeValue();


                    userAuthFollowings.remove(mUserSelect.getUid());
                    DatabaseReference userAuthFollowingsRef = dbManager.getDatabaseUserFollowings(mUserAuthId);
                    //userAuthFollowingsRef.setValue(userAuthFollowings.size());
                    userAuthFollowingsRef.child("is-following")
                            .child(mUserSelect.getUid())
                            .removeValue();

                    followButton.setButtonOff();
                }
            }
        };
    }

    private Map<String, String> getUsers(DataSnapshot snapshot) {

        final Map<String, String> users = new HashMap<>();

        for(DataSnapshot userSnapshot: snapshot.getChildren()) {

            String userid = userSnapshot.getValue(String.class);

            if(userid != null) {
                users.put(userid, userid);

            } else {
                Log.e(TAG, "userid is null");
            }

        }
        return users;
    }

    private Map<String, String> getFollowersFromUserSelected(DataSnapshot usersSnapshot, final FollowButton followButton) {
        final Map<String, String> users = new HashMap<>();
        for(DataSnapshot userSnapshot: usersSnapshot.getChildren()) {

            String userid = userSnapshot.child("is-followed-by").getValue(String.class);

            if(userid != null) {
                users.put(userid, userid);


            } else {
                Log.e(TAG, "userid is null");
            }



        }
        return users;
    }

    private Map<String, String> getFollowingsFromUserAuth(DataSnapshot usersSnapshot, FollowButton followButton) {
        final Map<String, String> users = new HashMap<>();

        for(DataSnapshot userSnapshot: usersSnapshot.getChildren()) {
            String userid = userSnapshot.child("is-following").getValue(String.class);

            Log.i(TAG, "userid: " + userid);

            if(userid != null) {
                users.put(userid, userid);

                Log.e(TAG, "User auth is following " + mUserAuthId);
                if(userid.equals(mUserSelect.getUid())) {
                    Log.e(TAG, "User auth is following " + mUserAuthId);
                    followButton.setButtonOn();
                }

            }

        }
        return users;
    }
}
