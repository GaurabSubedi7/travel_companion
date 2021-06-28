package com.example.myapplication.Models;

public class User {
    String profilePic, userName, email, fullName, accountType;

    public User(String profilePic, String userName, String email, String fullName) {
        this.fullName = fullName;
        this.profilePic = profilePic;
        this.userName = userName;
        this.email = email;
    }

    public User(){}

    //SignUp Constructor
    public User(String userName, String email, String accountType) {
        this.userName = userName;
        this.email = email;
        this.accountType = accountType;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
