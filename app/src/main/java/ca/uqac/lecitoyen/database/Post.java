package ca.uqac.lecitoyen.database;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post {

    //  Data

    private String userId;

    private long date;

    private long inverseDate;

    private String post;

    //  Constructor

    public Post() {
        //  Default Constructor
    }

    public Post(String userId, String post, long date) {
        this.userId = userId;
        this.post = post;
        this.date = date;
        this.inverseDate = 0 - date;
    }

    //  Getter & Setter

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getInverseDate() {
        return inverseDate;
    }

    public void setInverseDate(long inverseDate) {
        this.inverseDate = inverseDate;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("post", post);
        result.put("date", date);
        result.put("inverseDate", inverseDate);
        return result;
    }

}
