package com.nap.bycab.models;

/**
 * Created by nirav on 30/12/15.
 */
public class Ticket {

    private String userName;

    private String mobileNumber;

    private long waitTime;

    private long durationTime;

    private double distance;

    public Ticket(String userName, String mobileNumber, long waitTime, long durationTime, double distance) {
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

    public long getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(long waitTime) {
        this.waitTime = waitTime;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
