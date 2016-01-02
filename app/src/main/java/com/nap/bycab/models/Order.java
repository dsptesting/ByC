package com.nap.bycab.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Palak on 14-12-2015.
 */
public class Order {

    @SerializedName("Amount")
    private String Amount;
    @SerializedName("CustMobile")
    private String CustMobile;
    @SerializedName("CustName")
    private String CustName;
    @SerializedName("DropLocation")
    private String DropLocation;
    @SerializedName("JournyTime")
    private String JournyTime;
    @SerializedName("KM")
    private String KM;
    @SerializedName("OrderDate")
    private String OrderDate;
    @SerializedName("OrderId")
    private String OrderId;
    @SerializedName("OrderStatus")
    private String OrderStatus;
    @SerializedName("PickUpLocation")
    private String PickUpLocation;
    @SerializedName("Time")
    private String Time;
    @SerializedName("Latitude")
    private String Latitude;

    @SerializedName("Longitude")
    private String Longitude;

    @SerializedName("DLatitude")
    private String DLatitude;

    @SerializedName("DLongitude")
    private String DLongitude;

    @SerializedName("WaitingTime")
    private String WaitingTime;

    public Order() {
    }

    public String getWaitingTime() {
        return WaitingTime;
    }

    public void setWaitingTime(String waitingTime) {
        WaitingTime = waitingTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "Amount='" + Amount + '\'' +
                ", CustMobile='" + CustMobile + '\'' +
                ", CustName='" + CustName + '\'' +
                ", DropLocation='" + DropLocation + '\'' +
                ", JournyTime='" + JournyTime + '\'' +
                ", KM='" + KM + '\'' +
                ", OrderDate='" + OrderDate + '\'' +
                ", OrderId='" + OrderId + '\'' +
                ", OrderStatus='" + OrderStatus + '\'' +
                ", PickUpLocation='" + PickUpLocation + '\'' +
                ", Time='" + Time + '\'' +
                '}';
    }

    public String getDLongitude() {
        return DLongitude;
    }

    public void setDLongitude(String DLongitude) {
        this.DLongitude = DLongitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getDLatitude() {
        return DLatitude;
    }

    public void setDLatitude(String DLatitude) {
        this.DLatitude = DLatitude;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getCustMobile() {
        return CustMobile;
    }

    public void setCustMobile(String custMobile) {
        CustMobile = custMobile;
    }

    public String getCustName() {
        return CustName;
    }

    public void setCustName(String custName) {
        CustName = custName;
    }

    public String getDropLocation() {
        return DropLocation;
    }

    public void setDropLocation(String dropLocation) {
        DropLocation = dropLocation;
    }

    public String getJournyTime() {
        return JournyTime;
    }

    public void setJournyTime(String journyTime) {
        JournyTime = journyTime;
    }

    public String getKM() {
        return KM;
    }

    public void setKM(String KM) {
        this.KM = KM;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getPickUpLocation() {
        return PickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        PickUpLocation = pickUpLocation;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
