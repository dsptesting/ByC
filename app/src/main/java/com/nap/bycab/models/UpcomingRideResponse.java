package com.nap.bycab.models;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by nirav on 14/12/15.
 */
public class UpcomingRideResponse {
    @SerializedName("Response")
    private String responseId;
    @SerializedName("ResponseMsg")
    private String responseMessage;
    @SerializedName("Orders")
    private ArrayList<Order> alUpcomingRides;

    public UpcomingRideResponse() {
        //EMPTY CONSTRUCTOR
    }

    public UpcomingRideResponse(String responseId, String responseMessage, ArrayList<Order> alUpcomingRides) {
        this.responseId = responseId;
        this.responseMessage = responseMessage;
        this.alUpcomingRides = alUpcomingRides;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public ArrayList<Order> getAlUpcomingRides() {
        return alUpcomingRides;
    }

    public void setAlUpcomingRides(ArrayList<Order> alUpcomingRides) {
        this.alUpcomingRides = alUpcomingRides;
    }
}
