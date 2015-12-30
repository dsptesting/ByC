package com.nap.bycab.models;

import android.util.Log;

import com.nap.bycab.util.AppConstants;

import java.util.ArrayList;

/**
 * Created by Palak on 24-12-2015.
 */
public class NotificationList {

    private ArrayList<Integer> idList;

    @Override
    public String toString() {
        return "NotificationList{" +
                "idList=" + idList +
                '}';
    }

    public NotificationList() {
        idList = new ArrayList<>();

        Log.v(AppConstants.DEBUG_TAG, "Constr NotificationList");
    }

    public ArrayList<Integer> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<Integer> idList) {
        this.idList = idList;
    }
}
