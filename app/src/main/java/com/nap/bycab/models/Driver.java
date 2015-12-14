package com.nap.bycab.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 14/12/15.
 */
public class Driver {

    @SerializedName("AadharNo")
    private String adharNo;
    @SerializedName("EmailId")
    private String emailId;
    @SerializedName("GCMID")
    private String notificationId;
    @SerializedName("Id")
    private String driverId;
    @SerializedName("LicenceNo")
    private String licenceNo;
    @SerializedName("MobileNo")
    private String mobileNo;
    @SerializedName("Name")
    private String name;
    @SerializedName("Password")
    private String password;
    @SerializedName("VehicalDesc")
    private String vehicleDescription;


    public Driver() {
        // empty constructor
    }

    public Driver(String adharNo, String emailId, String notificationId, String driverId, String licenceNo, String mobileNo, String name, String password, String vehicleDescription) {
        this.adharNo = adharNo;
        this.emailId = emailId;
        this.notificationId = notificationId;
        this.driverId = driverId;
        this.licenceNo = licenceNo;
        this.mobileNo = mobileNo;
        this.name = name;
        this.password = password;
        this.vehicleDescription = vehicleDescription;
    }

    public String getAdharNo() {
        return adharNo;
    }

    public void setAdharNo(String adharNo) {
        this.adharNo = adharNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getLicenceNo() {
        return licenceNo;
    }

    public void setLicenceNo(String licenceNo) {
        this.licenceNo = licenceNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public void setVehicleDescription(String vehicleDescription) {
        this.vehicleDescription = vehicleDescription;
    }
}
