package com.nap.bycab.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.fragment.AboutUsFragment;
import com.nap.bycab.fragment.ContactUsFragment;
import com.nap.bycab.fragment.HomeFragment;
import com.nap.bycab.fragment.MyRidesFragment;
import com.nap.bycab.fragment.MyearningFragment;
import com.nap.bycab.fragment.UpComingRidesFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.models.Driver;
import com.nap.bycab.models.LoginResponse;
import com.nap.bycab.models.NotificationList;
import com.nap.bycab.models.RideResponse;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.LocationBackgroundService;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;
import com.nap.bycab.util.ServiceCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,ConnectionCallbacks, OnConnectionFailedListener, LocationListener, ServiceCallback {

    private boolean isInternetAvailable;
    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private int mSelectedId;
    private TextView tvEditProfile;
    private boolean isNotificationLocation;
    private Driver driver;
    private SwitchCompat switchDriverStatus;
    private boolean isForCurrentRide;

    //location update
    protected static final String TAG = "location-updates-sample";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 30000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates;
    private Intent serviceIntent;
    public LocationBackgroundService myService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("ServiceConnection","connected");
            LocationBackgroundService.MyBinder b = (LocationBackgroundService.MyBinder) service;
            myService = b.getService();
            myService.setCallback(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("ServiceConnection","disconnected");
            myService = null;
        }
    };;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isInternetAvailable=isInternetAvailable();

        handleNotification(getIntent());

        driver=PrefUtils.getCurrentDriver(MainActivity.this);

        setNavigationDrawer(savedInstanceState);

        LocationManager  manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            buildAlertMessageNoGps();
        }

        /*//location update
        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        mGoogleApiClient.connect();


        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(mGoogleApiClient.isConnected()) startUpdatesButtonHandler();

            }
        }.start();*/

//        if(isForCurrentRide){
//            callCurrentRideService();
//        }



        serviceIntent = new Intent(MainActivity.this, LocationBackgroundService.class);
        //startService(serviceIntent);

        // To call onServiceConnected() if the service already started
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);


    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        //stopService(serviceIntent);
        super.onDestroy();
        unbindService(serviceConnection);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.v(AppConstants.DEBUG_TAG, "onNewIntent");

        handleNotification(intent);
    }

    private void handleNotification(Intent intent){

        isNotificationLocation = intent.getBooleanExtra("isNotificationLocation", false);
        isForCurrentRide = intent.getBooleanExtra("IsCurrentRide", false);

        Log.v(AppConstants.DEBUG_TAG, "Noti Id: " + intent.getIntExtra("notification_id", 0));

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(isNotificationLocation){

            // handle direct implementation of current ride popup
            notificationManager.cancelAll();


        }
        else{
            if(isForCurrentRide){

                NotificationList notificationList = PrefUtils.getCurrentNotificationIdList(this);
                for(int i = 0;i<notificationList.getIdList().size();i++){
                    notificationManager.cancel(notificationList.getIdList().get(i));
                }
                PrefUtils.clearCurrentNotificationIdList(this);
            }
            else{

                NotificationList notificationList = PrefUtils.getUpcomingNotificationIdList(this);
                for(int i = 0;i<notificationList.getIdList().size();i++){
                    notificationManager.cancel(notificationList.getIdList().get(i));
                }
                PrefUtils.clearUpcomingNotificationIdList(this);
            }
        }


        /*if(intent.getIntExtra("notification_id", 0) != 0){

            notificationManager.cancel(intent.getIntExtra("notification_id", 0));
        }*/
    }

    private void callDriverStatusService(boolean isChecked) {

        final JSONObject object=new JSONObject();
        try {
            object.put("Id",PrefUtils.getCurrentDriver(MainActivity.this).getDriverId()+"");
            object.put("Status",""+isChecked);
            Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService " + object);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        /*final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();*/
        new PostServiceCall(AppConstants.UPDATE_DRIVER_STATUS,object){

            @Override
            public void response(String response) {
                //progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService resp " + response);
                CommonResponse commonResponse=new GsonBuilder().create().fromJson(response,CommonResponse.class);

                if(commonResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(mDrawerLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                } else {

                    Driver mDriver = PrefUtils.getCurrentDriver(MainActivity.this);
                    try {
                        mDriver.setDriverStatus(object.getString("Status"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PrefUtils.setCurrentDriver(mDriver, MainActivity.this);

                }
            }

            @Override
            public void error(String error) {
               // progressDialog.dismiss();
            }
        }.call();
    }


    private void buildAlertMessageNoGps() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Your GPS seems to be disabled, enable it to continue!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                       finish();
                    }
                });
        final AlertDialog alert = builder.create();

        alert.show();
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
        tvEditProfile= (TextView) headerView.findViewById(R.id.tvEditProfile);
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        ((TextView)headerView.findViewById(R.id.tvDriverName)).setText("" + driver.getName());
        ((TextView)headerView.findViewById(R.id.tvDriverMobile)).setText("" + driver.getMobileNo());

        switchDriverStatus = (SwitchCompat) headerView.findViewById(R.id.switchDriverStatus);
        switchDriverStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                callDriverStatusService(isChecked);
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

                fragmentTransaction.replace(R.id.main_container, homeFragment,"home_fragment");
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
                /*toolbar.setTitle("About Us");
                toolbar.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                fragmentManager = getFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                AboutUsFragment aboutUsFragment = AboutUsFragment.newInstance("","");
                fragmentTransaction.replace(R.id.main_container, aboutUsFragment);
                fragmentTransaction.commit();*/

                Intent iii=new Intent(MainActivity.this,FairActivity.class);
                startActivity(iii);

                break;

            case R.id.navigation_item_6:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                PrefUtils.clearCurrentDriver(MainActivity.this);
                Intent ii=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(ii);
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



    //Location Update Code
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void startUpdatesButtonHandler() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }


    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }


    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }*/

    }



    @Override
    protected void onStop() {
       // mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation == null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                final double latitude = mCurrentLocation.getLatitude();
                final double longitude = mCurrentLocation.getLongitude();
                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        try {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
                            HomeFragment.map.animateCamera(cameraUpdate);
                            HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).snippet("Me"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }catch ( Exception e){
                e.printStackTrace();
            }
        }
//        Toast.makeText(this, mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        double latitude = mCurrentLocation.getLatitude();
        double longitude = mCurrentLocation.getLongitude();
        try {
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15);
//            HomeFragment.map.animateCamera(cameraUpdate);
            HomeFragment.map.clear();

            HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).snippet("Me"));
                Toast.makeText(this, mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//            callLocationUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Location getCurrentLocation(){
        return  mCurrentLocation;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    private void callLocationUpdate() {


        JSONObject object=new JSONObject();
        try {
            object.put("Id", driver.getDriverId()+"");
            object.put("Latitude",mCurrentLocation.getLatitude()+"");
            object.put("Longitude",mCurrentLocation.getLongitude()+"");

            Log.e("location update :",object+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.UPDATE_LOCATION,object){

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e("login Response: ",response+"");
                CommonResponse commonResponse=new GsonBuilder().create().fromJson(response,CommonResponse.class);

                if(commonResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(mDrawerLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                }  else {
                    Snackbar snackbar=Snackbar.make(mDrawerLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();


    }

    @Override
    public void updateDistance(double distance) {

        HomeFragment fragment = (HomeFragment) getFragmentManager().findFragmentByTag("home_fragment");
        if(fragment != null)fragment.updateHomeFragmentDistance(distance);

    }
}
