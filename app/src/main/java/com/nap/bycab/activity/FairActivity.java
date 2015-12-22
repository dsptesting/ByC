package com.nap.bycab.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.nap.bycab.R;

public class FairActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_fair;
    }

    @Override
    protected String getToolbarTitle() {
        return "Fair Calculator";
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
