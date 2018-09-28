package ca.uqac.lecitoyen.database;

public class User {

    //  User ID
    private String userID;

    //  Nom & Prenom d'utilisateur
    private String realName;

    //  Identifiant
    private String userName;

    //  Date naissance
    private long createAccountTime;

    //  Email
    private String email;

    //  Location
    private long location;

    //  Password
    private String password;


    //  Constructor
    public User() { }

    public User(String userID, String realName, String userName, String email, long createAccountTime) {
        this.userID = userID;
        this.realName = realName;
        this.userName = userName;
        this.email = email;
        this.createAccountTime = createAccountTime;
    }

    //
    //  Getter & Setter
    //

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }




    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getCreateAccountTime() {
        return createAccountTime;
    }

    public void setCreateAccountTime(long createAccountTime) {
        this.createAccountTime = createAccountTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getLocation() {
        return location;
    }

    public void setLocation(long location) {
        this.location = location;
    }
}
