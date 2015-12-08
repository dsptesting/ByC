package com.nap.bycab.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.nap.bycab.R;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{

    private boolean isInternetAvailable;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int mSelectedId;
    private LinearLayout llDrawerHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInternetAvailable=isInternetAvailable();
        setNavigationDrawer(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected String getToolbarTitle() {
        return "";
    }

    @Override
    protected boolean isToolbarWithBack() {
        return false;
    }

    @Override
    protected int getToolbarColor() {
        return R.color.full_transperent;
    }

    private void setNavigationDrawer(Bundle savedInstanceState) {
        mDrawer= (NavigationView) findViewById(R.id.main_drawer);

        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //default it set first item as selected
        mSelectedId=savedInstanceState ==null ? R.id.navigation_item_1: savedInstanceState.getInt("SELECTED_ID");
        itemSelection(mSelectedId);

//        llDrawerHeader= (LinearLayout) mDrawer.findViewById(R.id.llDrawerHeader);
//        llDrawerHeader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(i);
//            }
//        });
    }

    private void itemSelection(int mSelectedId) {

        Intent i=null;
        switch(mSelectedId){

            case R.id.navigation_item_1:
                mDrawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.navigation_item_2:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                i=new Intent(MainActivity.this,MyRideActivity.class);
                startActivity(i);
                break;

            case R.id.navigation_item_3:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                i=new Intent(MainActivity.this,MyEarningActivity.class);
                startActivity(i);
                break;

            case R.id.navigation_item_4:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                i=new Intent(MainActivity.this,ContactUsActivity.class);
                startActivity(i);
                break;

            case R.id.navigation_item_5:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                i=new Intent(MainActivity.this,AboutUsActivity.class);
                startActivity(i);
                break;

            case R.id.navigation_item_6:
                mDrawerLayout.closeDrawer(GravityCompat.START);
//                i=new Intent(MainActivity.this,UpcomingRideActivity.class);
//                startActivity(i);
                break;

            case R.id.navigation_item_7:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                i=new Intent(MainActivity.this,UpcomingRideActivity.class);
                startActivity(i);
                break;

        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        menuItem.setChecked(true);
        mSelectedId=menuItem.getItemId();
        itemSelection(mSelectedId);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        //save selected item so it will remains same even after orientation change
        outState.putInt("SELECTED_ID", mSelectedId);
    }


}
