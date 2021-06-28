package com.example.myapplication.Models;

import android.net.Uri;

import java.util.ArrayList;

public class UserPost {
    private String postId, caption, uploadDate, location, likeCount;
    private ArrayList<String> imageURL = new ArrayList<>();

    public UserPost(){}

    //constructor to get all the post data
    public UserPost(String postId, String caption, String uploadDate, ArrayList<String> imageURL) {
        this.postId = postId;
        this.caption = caption;
        this.uploadDate = uploadDate;
        this.imageURL = imageURL;
    }

    //constructor to get only images from user gallery or camera
    public UserPost(ArrayList<String> imageURL) {
        this.imageURL = imageURL;
    }

    public UserPost(String caption, String uploadDate) {
        this.caption = caption;
        this.uploadDate = uploadDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getImageURL() {
        return imageURL;
    }

    public void setImageURL(ArrayList<String> imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
