package ca.uqac.lecitoyen.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

import ca.uqac.lecitoyen.R;
import ca.uqac.lecitoyen.activities.MainUserActivity;
import ca.uqac.lecitoyen.fragments.userUI.UserProfileFragment;
import ca.uqac.lecitoyen.models.DatabaseManager;
import ca.uqac.lecitoyen.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private final static String TAG = SearchUserAdapter.class.getSimpleName();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mainLayout;
        CircleImageView profileImage;
        TextView name, username;

        public ViewHolder(View itemView) {
            super(itemView);

            mainLayout = itemView.findViewById(R.id.adapter_search_user_profile_main_layout);
            profileImage = itemView.findViewById(R.id.adapter_search_user_profile_image_view);
            name = itemView.findViewById(R.id.adapter_search_user_profile_name);
            username = itemView.findViewById(R.id.adapter_search_user_profile_username);

        }

    }


    private DatabaseManager dbManager;
    private MainUserActivity mUserActivity;

    //  Data
    private ArrayList<User> mUsersList = new ArrayList<>();

    public SearchUserAdapter() {
    }

    public SearchUserAdapter(MainUserActivity activity, ArrayList<User> users) {
        this.dbManager = DatabaseManager.getInstance();
        this.mUserActivity = activity;
        this.mUsersList = users;

        //if(user != null) {
        //    this.mCurrentUserId = user.getUid();
        //    this.mCurrentUser = new User(mCurrentUserId);
        //}
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.adapter_search_user, parent, false);
        return new SearchUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final User holderUser = mUsersList.get(holder.getAdapterPosition());

        if(holderUser.getPid() != null && !holderUser.getPid().isEmpty()) {
            StorageReference profileImage = dbManager.getStorageUserProfilPicture(
                    holderUser.getUid(),
                    holderUser.getPid());
            Glide.with(mUserActivity).load(profileImage).into(holder.profileImage);
        }
        else
            Glide.with(mUserActivity).load(R.color.black_200).into(holder.profileImage);

        if(holderUser.getName() != null && !holderUser.getName().isEmpty())
            holder.name.setText(holderUser.getName());

        if(holderUser.getUsername() != null && !holderUser.getUsername().isEmpty())
            holder.username.setText(holderUser.getUsername());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment fragment = UserProfileFragment.newInstance(holderUser);
                mUserActivity.doUserProfileTransaction(fragment, MainUserActivity.SELECT_USER);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }
}
