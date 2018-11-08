package ca.uqac.lecitoyen.models;

//TODO: Store user data locally

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {

    //  Public info
    private String uid;

    private String pid;

    private String name;

    private String username;

    private String biography;

    //  Private info
    private String email;

    private String phone;

    private boolean verify;

    private String gender;

    //  Metadata
    private long creationTimestamp;

    private long lastSigninTimestamp;

    private String provider;

    private String location;

    //  Social interaction
    /*private Map<String, Post> upvotePosts;
    private long upvoteCount;

    private Map<String, Post> repostPosts;
    private long repostCount;*/


    //  Constructor
    public User() { }

    public User(String uid) {
        this.uid = uid;
    }

    public User(String uid, String name, String username, String email, String phone, long creationTimestamp, String provider) {
        this.uid = uid;
        this.pid = "";
        this.name = name;
        this.username = username;
        this.biography = "";
        this.email = email;
        this.phone = phone;
        this.gender = "";
        this.verify = false;
        this.creationTimestamp = creationTimestamp;
        this.lastSigninTimestamp = 0;
        this.location = "";
        this.provider = provider;
    }

    /*

            Getter & Setter

     */

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public long getLastSigninTimestamp() {
        return lastSigninTimestamp;
    }

    public void setLastSigninTimestamp(long lastSigninTimestamp) {
        this.lastSigninTimestamp = lastSigninTimestamp;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /*public Map<String, Post> getUpvotePosts() {
        return upvotePosts;
    }

    public void setUpvotePosts(Map<String, Post> upvotePosts) {
        this.upvotePosts = upvotePosts;
    }

    public Map<String, Post> getRepostPosts() {
        return repostPosts;
    }

    public void setRepostPosts(Map<String, Post> repostPosts) {
        this.repostPosts = repostPosts;
    }

    public long getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(long upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public long getRepostCount() {
        return repostCount;
    }

    public void setRepostCount(long repostCount) {
        this.repostCount = repostCount;
    }*/
}
