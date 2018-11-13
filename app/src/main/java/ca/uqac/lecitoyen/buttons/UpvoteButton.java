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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.Interface.iToggleButton;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.adapters.SwipePostAdapter;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;

public class UpvoteButton extends FrameLayout implements iToggleButton {

    private static final String TAG = UpvoteButton.class.getSimpleName();

    private DatabaseManager db;
    private User mCurrentUser;
    private Post mPostClicked;

    private View rootView;

    private LinearLayout mUpvoteOn;
    private LinearLayout mUpvoteOff;
    private TextView mUpvoteCountOn;
    private TextView mUpvoteCountOff;

    private boolean isUpvoteOn = false;

    public UpvoteButton(@NonNull Context context) {
        super(context);
        create(context);
    }

    public UpvoteButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        create(context);
    }

    public UpvoteButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create(context);
    }

    public void create(Context context) {

        db = DatabaseManager.getInstance();

        rootView = inflate(context, R.layout.button_upvote, this);

        mUpvoteOn  = rootView.findViewById(R.id.button_upvote_layout_on);
        mUpvoteCountOn = rootView.findViewById(R.id.button_upvote_count_on);

        mUpvoteOff = rootView.findViewById(R.id.button_upvote_layout_off);
        mUpvoteCountOff = rootView.findViewById(R.id.button_upvote_count_off);

        setButtonOff();
    }

    @Override
    public void setButtonOn() {

        if(mUpvoteOn != null)
            mUpvoteOn.setVisibility(VISIBLE);

        if(mUpvoteOff != null)
            mUpvoteOff.setVisibility(GONE);

        isUpvoteOn = true;

    }

    @Override
    public void setButtonOff() {

        if(mUpvoteOn != null)
            mUpvoteOn.setVisibility(GONE);

        if(mUpvoteOff != null)
            mUpvoteOff.setVisibility(VISIBLE);

        isUpvoteOn = false;

    }

    @Override
    public void setButtonCount(long count) {
        if(mUpvoteCountOn != null)
            mUpvoteCountOn.setText(String.valueOf(count));

        if(mUpvoteCountOff != null)
            mUpvoteCountOff.setText(String.valueOf(count));

    }

    @Override
    public boolean isButtonOn() {
        return isUpvoteOn;
    }


    public void setUpvoteOnClickListener(final UpvoteButton upvoteButton, User currUser, Post post) {

        mCurrentUser = currUser;
        mPostClicked = post;

        db.getDatabasePostUpvoteUsers(mPostClicked.getPostid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final Map<String, User> users = getUpvoteUsers(dataSnapshot);

                        upvoteButton.setOnClickListener(upvoteOnClickListener(upvoteButton, users));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                    }
                });
    }

    private Map<String, User> getUpvoteUsers(DataSnapshot usersSnapshot) {
        final Map<String, User> users = new HashMap<>();
        for(DataSnapshot userSnapshot: usersSnapshot.getChildren()) {

            User user = userSnapshot.getValue(User.class);

            if(user != null)
                users.put(user.getUid(), user);

        }
        return users;
    }

    private View.OnClickListener upvoteOnClickListener(final UpvoteButton upvoteButton, final Map<String, User> users) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!upvoteButton.isButtonOn()) {
                    /*      Update data structure       */
                    users.put(mCurrentUser.getUid(), mCurrentUser);
                    mPostClicked.setUpvoteUsers(users);
                    mPostClicked.setUpvoteCount(users.size());

                    /*      Update firebase with new data structure       */
                    db.writeUpvoteToPost(mCurrentUser, mPostClicked);

                    /*      Update UI       */
                    upvoteButton.setButtonOn();
                } else {
                    /*      Update data structure       */
                    users.remove(mCurrentUser.getUid());
                    mPostClicked.setUpvoteCount(users.size());
                    mPostClicked.setUpvoteUsers(users);

                    /*      Update firebase with new data structure       */
                    db.removeUpvoteFromPost(mCurrentUser, mPostClicked);

                    /*      Update UI       */
                    upvoteButton.setButtonOff();
                }

                /*      Update UI Count       */
                upvoteButton.setButtonCount(mPostClicked.getUpvoteCount());

            }
        };
    }
}