package ca.uqac.lecitoyen.database;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class PostTest {

    //  Data

    private String userId;

    private boolean isPrivate;

    private long date;

    private long inverseDate;

    private int countLike;

    private String post;

    //  Constructor

    public PostTest() {
        //  Default Constructor
    }

    public PostTest(String userId, String post, long date, int countLike) {
        this.userId = userId;
        this.post = post;
        this.date = date;
        this.inverseDate = 0 - date;
        this.isPrivate = false;
        this.countLike = countLike;
    }

    //  Getter & Setter

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
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

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
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
        result.put("isPrivate", isPrivate);
        result.put("date", date);
        result.put("inverseDate", inverseDate);
        result.put("countLike", countLike);
        return result;
    }

}
