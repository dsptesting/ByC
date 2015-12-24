package com.nap.bycab.models;

import java.util.ArrayList;

/**
 * Created by Palak on 24-12-2015.
 */
public class NotificationList {

    private ArrayList<Integer> idList;

    public NotificationList() {
        idList = new ArrayList<>();
    }

    public ArrayList<Integer> getIdList() {
        return idList;
    }

    public void setIdList(ArrayList<Integer> idList) {
        this.idList = idList;
    }
}
