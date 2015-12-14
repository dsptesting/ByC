package com.nap.bycab.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 14/12/15.
 */
public class CommonResponse {



    @SerializedName("Response")
    private String responseId;
    @SerializedName("ResponseMsg")
    private String responseMessage;

    public CommonResponse() {
    }

    public CommonResponse(String responseId, String responseMessage) {
        this.responseId = responseId;
        this.responseMessage = responseMessage;
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
}
