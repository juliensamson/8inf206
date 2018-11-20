package ca.uqac.lecitoyen.models;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Image implements Serializable, Parcelable{


    private String imageid;

    private String name;

    private String path;

    private long sizebytes;

    private long creationTimestamp;

    private long updatedTimestamp;

    private String contentType;

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("imageid", imageid);

        result.put("name", name);
        result.put("path", path);
        result.put("sizebytes", sizebytes);
        result.put("creationTimestamp", creationTimestamp);
        result.put("updatedTimestamp", updatedTimestamp);
        result.put("contentType", contentType);

        return result;
    }

    public Image(){}


    protected Image(Parcel in) {
        imageid = in.readString();
        name = in.readString();
        path = in.readString();
        sizebytes = in.readLong();
        creationTimestamp = in.readLong();
        updatedTimestamp = in.readLong();
        contentType = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSizebytes() {
        return sizebytes;
    }

    public void setSizebytes(long sizebytes) {
        this.sizebytes = sizebytes;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageid);
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeLong(sizebytes);
        parcel.writeLong(creationTimestamp);
        parcel.writeLong(updatedTimestamp);
        parcel.writeString(contentType);
    }
}
