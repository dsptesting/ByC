package com.nap.bycab.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nap.bycab.R;

public class AboutUsActivity  extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected String getToolbarTitle() {
        return "About Us";
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
