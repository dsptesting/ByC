package com.nap.bycab.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nap.bycab.R;

public class UpcomingRideActivity  extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_upcoming_ride;
    }

    @Override
    protected String getToolbarTitle() {
        return "Upcoming Rides";
    }

    @Override
    protected boolean isToolbarWithBack() {
        return true;
    }

    @Override
    protected int getToolbarColor() {
        return R.color.primaryColor;
    }
}
