package com.example.myapplication.Models;

public class ThirdPartyService {
    private String thirdPartyServiceName, email, location;

    public ThirdPartyService(String thirdPartyServiceName, String email) {
        this.thirdPartyServiceName = thirdPartyServiceName;
        this.email = email;
    }

    public String getThirdPartyServiceName() {
        return thirdPartyServiceName;
    }

    public void setThirdPartyServiceName(String thirdPartyServiceName) {
        this.thirdPartyServiceName = thirdPartyServiceName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
