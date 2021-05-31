package com.example.myapplication.Models;

public class UserPost {
    private String imageURL, status, uploadDate;

    public UserPost(){}

    public UserPost(String imageURL, String status, String uploadDate) {
        this.imageURL = imageURL;
        this.status = status;
        this.uploadDate = uploadDate;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
