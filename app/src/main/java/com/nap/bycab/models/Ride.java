package com.nap.bycab.models;

/**
 * Created by Palak on 12-12-2015.
 */
public class Ride {

    private String id;
    private String customerName;
    private String customerMobile;
    private String kms;
    private String fare;
    private long dateTime;
    private String src;
    private String des;

    @Override
    public String toString() {
        return "Ride{" +
                "id='" + id + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerMobile='" + customerMobile + '\'' +
                ", kms='" + kms + '\'' +
                ", fare='" + fare + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", src='" + src + '\'' +
                ", des='" + des + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getKms() {
        return kms;
    }

    public void setKms(String kms) {
        this.kms = kms;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
