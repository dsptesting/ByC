package com.nap.bycab.models;

import com.google.android.gms.drive.Drive;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 14/12/15.
 */
public class LoginResponse {
    @SerializedName("Response")
    private String responseId;
    @SerializedName("ResponseMsg")
    private String responseMessage;
    @SerializedName("Driver")
    private Driver driver;

    public LoginResponse() {
        //EMPTY CONSTRUCTOR
    }

    public LoginResponse(String responseId, String responseMessage, Driver driver) {
        this.responseId = responseId;
        this.responseMessage = responseMessage;
        this.driver = driver;
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

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}
