package ca.uqac.lecitoyen.database;

import java.util.Date;

public class UserData {

    //  User ID
    private String userID;

    //  Nom & Prenom d'utilisateur
    private String realName;

    //  Identifiant
    private String userName;

    //  Date naissance
    private Date birthDate;

    //  Email
    private String email;

    //  Password
    private String password;


    //  Constructor
    public UserData() {
    }

    public UserData(String userID, String realName, String userName, String email) {
        this.userID = userID;
        this.realName = realName;
        this.userName = userName;
        this.email = email;
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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
