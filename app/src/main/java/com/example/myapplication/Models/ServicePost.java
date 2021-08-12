package com.example.myapplication.Models;

import java.util.ArrayList;

public class ServicePost {
    private String serviceId, serviceName, serviceType, serviceDescription, serviceLocation, uploadDate,
            userId;
    private int servicePrice;
    private double rating;
    private ArrayList<String> imageURL = new ArrayList<>();

    public ServicePost(){}

    public ServicePost(String serviceId, String serviceName, String serviceType, String serviceDescription,
                       String serviceLocation, int servicePrice, double rating, ArrayList<String> imageURL,
                       String uploadDate, String userId) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.serviceDescription = serviceDescription;
        this.serviceLocation = serviceLocation;
        this.servicePrice = servicePrice;
        this.rating = rating;
        this.imageURL = imageURL;
        this.uploadDate = uploadDate;
        this.userId = userId;
    }

    public ServicePost(String userId, String serviceName, String uploadDate, String serviceLocation,
                       String serviceType, String serviceDescription, int price){
        this.userId = userId;
        this.serviceName = serviceName;
        this.uploadDate = uploadDate;
        this.serviceLocation = serviceLocation;
        this.serviceType = serviceType;
        this.serviceDescription = serviceDescription;
        this.servicePrice = price;
    }

    public ServicePost(ArrayList<String> imageURL) {
        this.imageURL = imageURL;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceLocation() {
        return serviceLocation;
    }

    public void setServiceLocation(String serviceLocation) {
        this.serviceLocation = serviceLocation;
    }

    public int getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(int servicePrice) {
        this.servicePrice = servicePrice;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<String> getImageURL() {
        return imageURL;
    }

    public void setImageURL(ArrayList<String> imageURL) {
        this.imageURL = imageURL;
    }
}
