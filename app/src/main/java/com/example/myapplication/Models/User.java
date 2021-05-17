package com.example.myapplication.Models;

public class User {
    String profilePic, userName, email;

    public User(String profilePic, String userName, String email) {
        this.profilePic = profilePic;
        this.userName = userName;
        this.email = email;

    }

    public User(){}

    //SignUp Constructor
    public User(String userName, String email) {
        this.userName = userName;
        this.email = email;

    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
