package com.example.myapplication.Models;

public class Trip {
    private String tripId ,tripName,amount,startDate,endDate;

    public Trip (){};

    public Trip(String tripId,String tripName, String amount,String startDate, String endDate){
        this.tripId = tripId;
        this.tripName =tripName;
        this.amount = amount;
        this. startDate = startDate;
        this.endDate = endDate;

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
