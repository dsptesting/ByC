package com.nap.bycab.models;

/**
 * Created by nirav on 30/12/15.
 */
public class Ticket {

    private String userName;

    private String mobileNumber;

    private String waitTime;

    private String durationTime;

    private String distance;

    public Ticket(String userName, String mobileNumber, String waitTime, String durationTime, String distance) {
        this.userName = userName;
        this.mobileNumber = mobileNumber;
        this.waitTime = waitTime;
        this.durationTime = durationTime;
        this.distance = distance;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public String getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(String durationTime) {
        this.durationTime = durationTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
