package com.nap.bycab.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nap.bycab.R;

public class MyEarningActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_earning;
    }

    @Override
    protected String getToolbarTitle() {
        return "My Earnings";
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
