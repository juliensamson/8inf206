package ca.uqac.lecitoyen.database;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {

    //  Data
    private String postid;

    private String uid;

    private long date;

    private long inverseDate;

    private String post;

    //  metadata
    private List<PostModification> modifications;

    //  Constructor

    public Post() {
        //  Default Constructor
    }

    public Post(String uid, String post, long date) {
        this.uid = uid;
        this.post = post;
        this.date = date;
        this.inverseDate = 0 - date;
    }

    //  Getter & Setter


    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public List<PostModification> getModifications() {
        return modifications;
    }

    public void setModifications(List<PostModification> modifications) {
        this.modifications = modifications;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postid", postid);
        result.put("uid", uid);
        result.put("post", post);
        result.put("date", date);
        result.put("inverseDate", inverseDate);
        result.put("modifications", modifications);
        return result;
    }

}
