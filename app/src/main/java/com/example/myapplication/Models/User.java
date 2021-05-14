package com.example.myapplication.Models;

public class User {
    String profilePic, userName, email, password;

    public User(String profilePic, String userName, String email, String password) {
        this.profilePic = profilePic;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public User(){}

    //SignUp Constructor
    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
