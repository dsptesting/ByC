package com.nap.bycab.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.nap.bycab.R;
import com.nap.bycab.fragment.AboutUsFragment;
import com.nap.bycab.fragment.ContactUsFragment;
import com.nap.bycab.fragment.HomeFragment;
import com.nap.bycab.fragment.MyRidesFragment;
import com.nap.bycab.fragment.MyearningFragment;
import com.nap.bycab.fragment.UpComingRidesFragment;

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
        View headerView = mDrawer.inflateHeaderView(R.layout.drawer_header);
        llDrawerHeader= (LinearLayout) headerView.findViewById(R.id.llDrawerHeader);
        llDrawerHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });
    }

    public void openCloseDrawer(){

        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        else if(mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }


    private void itemSelection(int mSelectedId) {

        Intent i=null;
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch(mSelectedId){

            case R.id.navigation_item_1:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                toolbar.setTitle("");
                toolbar.setBackgroundColor(getResources().getColor(R.color.full_transperent));
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                HomeFragment homeFragment = HomeFragment.newInstance("","");
                fragmentTransaction.replace(R.id.main_container, homeFragment);
                fragmentTransaction.commit();
                break;

            case R.id.navigation_item_2:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                toolbar.setTitle("My Ride");
                toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                MyRidesFragment myRidesFragment = MyRidesFragment.newInstance("","");
                fragmentTransaction.replace(R.id.main_container, myRidesFragment);
                fragmentTransaction.commit();
                break;

            case R.id.navigation_item_3:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                toolbar.setTitle("My Earning");
                toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                MyearningFragment myearningFragment = MyearningFragment.newInstance("","");
                fragmentTransaction.replace(R.id.main_container, myearningFragment);
                fragmentTransaction.commit();
                break;

            case R.id.navigation_item_4:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                toolbar.setTitle("Contact Us");
                toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                ContactUsFragment contactUsFragment = ContactUsFragment.newInstance("","");
                fragmentTransaction.replace(R.id.main_container, contactUsFragment);
                fragmentTransaction.commit();
                break;

            case R.id.navigation_item_5:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                toolbar.setTitle("About Us");
                toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                AboutUsFragment aboutUsFragment = AboutUsFragment.newInstance("","");
                fragmentTransaction.replace(R.id.main_container, aboutUsFragment);
                fragmentTransaction.commit();
                break;

            case R.id.navigation_item_6:
                mDrawerLayout.closeDrawer(GravityCompat.START);

                finish();
                break;

            case R.id.navigation_item_7:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                toolbar.setTitle("Upcoming Ride");
                toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                UpComingRidesFragment upComingRidesFragment = UpComingRidesFragment.newInstance("","");
                fragmentTransaction.replace(R.id.main_container, upComingRidesFragment);
                fragmentTransaction.commit();
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
