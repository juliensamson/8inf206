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
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lecitoyen.Interface.iToggleButton;
import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.Post;
import ca.uqac.lecitoyen.models.User;

public class RepostButton extends FrameLayout implements iToggleButton {

    private static final String TAG = RepostButton.class.getSimpleName();

    private DatabaseManager db;
    private User mCurrentUser;
    private Post mPostClicked;

    private LinearLayout mRepostOn;
    private LinearLayout mRepostOff;
    private TextView mRepostCountOn;
    private TextView mRepostCountOff;

    private boolean isRepostOn = false;

    public RepostButton(@NonNull Context context) {
        super(context);
        create(context);
    }

    public RepostButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        create(context);
    }

    public RepostButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create(context);
    }

    private void create(Context context) {

        db = DatabaseManager.getInstance();

        View rootView = inflate(context, R.layout.button_repost, this);

        mRepostOn  = rootView.findViewById(R.id.button_repost_layout_on);
        mRepostCountOn = rootView.findViewById(R.id.button_repost_count_on);

        mRepostOff = rootView.findViewById(R.id.button_repost_layout_off);
        mRepostCountOff = rootView.findViewById(R.id.button_repost_count_off);

        setButtonOff();
    }

    @Override
    public void setButtonOn() {

        if(mRepostOn != null)
            mRepostOn.setVisibility(VISIBLE);

        if(mRepostOff != null)
            mRepostOff.setVisibility(GONE);

        isRepostOn = true;

    }

    @Override
    public void setButtonOff() {

        if(mRepostOn != null)
            mRepostOn.setVisibility(GONE);

        if(mRepostOff != null)
            mRepostOff.setVisibility(VISIBLE);

        isRepostOn = false;

    }

    @Override
    public void setButtonCount(long count) {

        if(mRepostCountOn != null)
            mRepostCountOn.setText(String.valueOf(count));

        if(mRepostCountOff != null)
            mRepostCountOff.setText(String.valueOf(count));

    }

    @Override
    public boolean isButtonOn() {
        return isRepostOn;
    }

    public void setRepostOnClickListener(final RepostButton repostButton, final User currUser, Post post) {

        mCurrentUser = currUser;
        mPostClicked = post;

        db.getDatabasePostRepostUsers(mPostClicked.getPostid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final Map<String, User> users = getRepostUsers(dataSnapshot);

                        if(!users.isEmpty())
                        {
                            if(mCurrentUser != null) {
                                if (users.containsKey(currUser.getUid()))
                                    repostButton.setButtonOn();
                                else
                                    repostButton.setButtonOff();
                            }
                        }


                        repostButton.setOnClickListener(repostOnClickListener(repostButton, users));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                    }
                });
    }


    private Map<String, User> getRepostUsers(DataSnapshot usersSnapshot) {
        final Map<String, User> users = new HashMap<>();
        for(DataSnapshot userSnapshot: usersSnapshot.getChildren()) {

            User user = userSnapshot.getValue(User.class);

            if(user != null)
                users.put(user.getUid(), user);

        }
        return users;
    }

    private View.OnClickListener repostOnClickListener(final RepostButton repostButton, final Map<String, User> users) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!repostButton.isButtonOn()) {
                    /*      Update data structure       */
                    users.put(mCurrentUser.getUid(), mCurrentUser);
                    mPostClicked.setRepostUsers(users);
                    mPostClicked.setRepostCount(users.size());

                    /*      Update firebase with new data structure       */
                    db.writeRepostToPost(mCurrentUser, mPostClicked);

                    /*      Update UI       */
                    repostButton.setButtonOn();
                } else {
                    /*      Update data structure       */
                    users.remove(mCurrentUser.getUid());
                    mPostClicked.setRepostCount(users.size());
                    mPostClicked.setRepostUsers(users);

                    /*      Update firebase with new data structure       */
                    db.removeRepostFromPost(mCurrentUser, mPostClicked);

                    /*      Update UI       */
                    repostButton.setButtonOff();
                }

                /*      Update UI Count       */
                repostButton.setButtonCount(mPostClicked.getRepostCount());

            }
        };
    }

}
