package com.example.myapplication.Models;

public class Post {
    String postId, imageUrl, caption;

    public Post(String postId, String imageUrl, String caption) {
        this.postId = postId;
        this.imageUrl = imageUrl;
        this.caption = caption;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
