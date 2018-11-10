package ca.uqac.lecitoyen.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by jul_samson on 18-10-04.
 */

public class PostHistory implements Parcelable {

    private int modifcationNumber;

    private String post;

    private long modificationTimestamp;

    public PostHistory() {
    }

    public PostHistory(int modifcationNumber, String post, long modificationTimestamp) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
