package com.example.myapplication.Models;

import android.net.Uri;

import java.util.ArrayList;

public class Image {
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private static Image instance;
    
    public static Image getInstance(){
        if (instance == null){
            instance = new Image();
        }
        return instance;
    }

    public ArrayList<Uri> getImageUris() {
        return imageUris;
    }

    public void setImageUris(ArrayList<Uri> imageUris) {
        this.imageUris = imageUris;
    }
}
