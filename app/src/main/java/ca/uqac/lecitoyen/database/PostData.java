package ca.uqac.lecitoyen.database;



//TODO: Handle different type of post other than txt

public class PostData {

    private String postId;

    private String userId;

    private boolean isPrivate;

    private long timePostCreated;

    private int countLike;

    private String postMessage;



    public PostData() { }

    public PostData(String postId, String userId, boolean isPrivate, long timePostCreated, int countLike, String postMessage) {
        this.postId = postId;
        this.userId = userId;
        this.isPrivate = isPrivate;
        this.timePostCreated = timePostCreated;
        this.countLike = countLike;
        this.postMessage = postMessage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public long getTimePostCreated() {
        return timePostCreated;
    }

    public void setTimePostCreated(long timePostCreated) {
        this.timePostCreated = timePostCreated;
    }

    public int getCountLike() {
        return countLike;
    }

    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

    public String getPostMessage() {
        return postMessage;
    }

    public void setPostMessage(String postMessage) {
        this.postMessage = postMessage;
    }
}
