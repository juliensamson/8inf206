package ca.uqac.lecitoyen.database;

//TODO: Store user data locally

public class User {

    //  Public info
    private String uid;

    private String name;

    private String username;

    private String biography;

    //  Private info
    private String email;

    private String phone;

    private boolean verify;

    private String sexe;


    //  Metadata
    private long creationTimestamp;

    private long lastSigninTimestamp;

    private long location;


    //  Constructor
    public User() { }

    public User(String uid, String name, String username, String email, String phone, long creationTimestamp) {
        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.verify = false;
        this.creationTimestamp = creationTimestamp;
        this.lastSigninTimestamp = 0;
        this.location = 0;
    }


    //
    //  Getter & Setter
    //


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
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

    public long getLocation() {
        return location;
    }

    public void setLocation(long location) {
        this.location = location;
    }
}
