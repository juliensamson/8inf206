package ca.uqac.lecitoyen.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post {

    //  Post information
    private String postid;

    private String message;

    private ArrayList<PostHistory> histories;

    private ArrayList<Image> images;

    private String audio;

    private long date;

    private long dateInverse;

    //  User information
    private User user;

    //  Social information

    private Map<String, User> upvoteUsers;
    private long upvoteCount;

    private Map<String, User> repostUsers;
    private long repostCount;

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
        this.images = new ArrayList<>();
        this.date = date;
        this.dateInverse = 0 - date;
        this.upvoteCount = 0;
        this.repostCount = 0;
    }

    public Post(String postid, User user, String message, long date) {
        this.postid = postid;
        this.user = user;
        this.message = message;
        this.images = new ArrayList<>();
        this.date = date;
        this.dateInverse = 0 - date;
        this.upvoteCount = 0;
        this.repostCount = 0;
    }

    public Post(String postid, User user, String message, long date, ArrayList<Image> imageList, ArrayList<PostHistory> histories) {
        this.postid = postid;
        this.user = user;
        this.message = message;
        this.images = imageList;
        this.histories = histories;
        this.date = date;
        this.dateInverse = 0 - date;
        this.upvoteCount = 0;
        this.repostCount = 0;
    }

    /*

             Getter & Setter

     */


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

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
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

        return result;
    }

}
