package ca.uqac.lecitoyen.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post implements Parcelable {

    //  Post information
    private String postid;

    private String message;

    private ArrayList<PostHistory> histories;

    private ArrayList<Image> images;

    private Audio audio;

    private long date;

    private long dateInverse;

    //  User information
    private User user;

    //  Social information

    private Map<String, User> upvoteUsers;
    private long upvoteCount;

    private Map<String, User> repostUsers;
    private long repostCount;

    private long commentCount;

    //MAP of (User, Comment)
    //private ArrayList<User> commentUserList;
    //private long commentCount;

    //  Constructor

    public Post() {
        //  Default Constructor
    }

    public Post(String postid) {
        this.postid = postid;
    }

    public Post(User user, String message, long date) {
        this.user = user;
        this.message = message;
        this.date = date;
        this.dateInverse = 0 - date;
        this.upvoteCount = 0;
        this.repostCount = 0;
    }

    public Post(String postid, User user, String message, long date) {
        this.postid = postid;
        this.user = user;
        this.message = message;
        this.date = date;
        this.dateInverse = 0 - date;
        this.upvoteCount = 0;
        this.repostCount = 0;
    }

    public Post(String postid, User user, String message, long date, ArrayList<Image> images, ArrayList<PostHistory> histories) {
        this.postid = postid;
        this.user = user;
        this.message = message;
        this.images = images;
        this.histories = histories;
        this.date = date;
        this.dateInverse = 0 - date;
        this.upvoteCount = 0;
        this.repostCount = 0;
    }

    /*

             Getter & Setter

     */


    protected Post(Parcel in) {
        postid = in.readString();
        message = in.readString();
        date = in.readLong();
        dateInverse = in.readLong();
        upvoteCount = in.readLong();
        repostCount = in.readLong();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<PostHistory> getHistories() {
        return histories;
    }

    public void setHistories(ArrayList<PostHistory> histories) {
        this.histories = histories;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDateInverse() {
        return dateInverse;
    }

    public void setDateInverse(long dateInverse) {
        this.dateInverse = dateInverse;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    //  Upvote

    public Map<String, User> getUpvoteUsers() {
        return upvoteUsers;
    }

    public void setUpvoteUsers(Map<String, User> upvoteUsers) {
        this.upvoteUsers = upvoteUsers;
    }

    public long getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(long upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    //  Repost

    public Map<String, User> getRepostUsers() {
        return repostUsers;
    }

    public void setRepostUsers(Map<String, User> repostUsers) {
        this.repostUsers = repostUsers;
    }

    public long getRepostCount() {
        return repostCount;
    }

    public void setRepostCount(long repostCount) {
        this.repostCount = repostCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user", user);

        result.put("postid", postid);
        result.put("message", message);
        result.put("images", images);
        result.put("audio", audio);
        result.put("histories", histories);
        result.put("date", date);
        result.put("dateInverse", dateInverse);

        result.put("upvoteUsers", upvoteUsers);
        result.put("upvoteCount", upvoteCount);
        result.put("repostUsers", repostUsers);
        result.put("repostCount", repostCount);
        result.put("commentCount", commentCount);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(postid);
        parcel.writeString(message);
        parcel.writeLong(date);
        parcel.writeLong(dateInverse);
        parcel.writeLong(upvoteCount);
        parcel.writeLong(repostCount);
        parcel.writeLong(commentCount);
    }
}
