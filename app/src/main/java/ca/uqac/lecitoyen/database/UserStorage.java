package ca.uqac.lecitoyen.database;


public class UserStorage {

    private String pid;

    private String location;

    private String format;

    private String description;

    private long uploadTimestamp;

    private boolean profilPicture;

    public UserStorage() {}

    public UserStorage(String pid, String location, long uploadTimestamp) {
        this.pid = location + pid;
        this.location = location;
        this.format = "";
        this.description = "";
        this.uploadTimestamp = uploadTimestamp;
        this.profilPicture = false;
    }

    public String getPid() {
        return pid;
    }

    public void setIid(String iid) {
        this.pid = iid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(long uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public boolean isProfilPicture() {
        return profilPicture;
    }

    public void setProfilPicture(boolean profilPicture) {
        this.profilPicture = profilPicture;
    }
}
