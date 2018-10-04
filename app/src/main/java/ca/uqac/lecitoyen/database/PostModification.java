package ca.uqac.lecitoyen.database;

/**
 * Created by jul_samson on 18-10-04.
 */

public class PostModification {

    private int modifcationNumber;

    private String post;

    private long modificationTimestamp;

    public PostModification() {
    }

    public PostModification(int modifcationNumber, String post, long modificationTimestamp) {
        this.modifcationNumber = modifcationNumber;
        this.post = post;
        this.modificationTimestamp = modificationTimestamp;
    }

    public int getModifcationNumber() {
        return modifcationNumber;
    }

    public void setModifcationNumber(int modifcationNumber) {
        this.modifcationNumber = modifcationNumber;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public long getModificationTimestamp() {
        return modificationTimestamp;
    }

    public void setModificationTimestamp(long modificationTimestamp) {
        this.modificationTimestamp = modificationTimestamp;
    }
}
