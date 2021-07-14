package com.example.myapplication.Models;

import java.util.ArrayList;

public class Trip {
    private String tripId ,tripName,amount,startDate,endDate, location;
    private ArrayList<Checklist> myChecklist = new ArrayList<>();

    public Trip (){};

    public Trip(String tripName, String amount,String startDate, String endDate, String location){
        this.tripName =tripName;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public ArrayList<Checklist> getMyChecklist() {
        return myChecklist;
    }

    public void setMyChecklist(ArrayList<Checklist> myChecklist) {
        this.myChecklist = myChecklist;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
