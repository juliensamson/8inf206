package ca.uqac.lecitoyen.database;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {

    //  post data
    private String postid;

    private long date;

    private long inverseDate;

    private String post;

    private String pictureId;

    //  User information
    private String uid;

    private String pid;

    private String name;

    private String username;

    //  metadata
    private List<PostModification> modifications;

    //  Constructor

    public Post() {
        //  Default Constructor
    }

    public Post(User user, String post, long date) {
        this.uid = user.getUid();
        this.pid = user.getPid();
        this.name = user.getName();
        this.username = user.getUsername();
        this.post = post;
        this.date = date;
        this.inverseDate = 0 - date;
        this.pictureId = "";
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

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getInverseDate() {
        return inverseDate;
    }

    public void setInverseDate(long inverseDate) {
        this.inverseDate = inverseDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("postid", postid);
        result.put("uid", uid);
        result.put("pid", pid);
        result.put("name", name);
        result.put("username", username);
        result.put("post", post);
        result.put("date", date);
        result.put("inverseDate", inverseDate);
        result.put("modifications", modifications);
        result.put("pictureId", pictureId);
        return result;
    }

}
