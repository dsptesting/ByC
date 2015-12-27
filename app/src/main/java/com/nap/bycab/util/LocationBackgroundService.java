package com.nap.bycab.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nap.bycab.R;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.fragment.HomeFragment;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by Palak on 27-12-2015.
 */
public class LocationBackgroundService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Location prevLocation;
    private DistanceCalculator distanceCalculator;
    private double distance = 0;
    int notif_id = 16;
    private ServiceCallback serviceCallback;

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    protected GoogleApiClient mGoogleApiClient;
    protected Boolean mRequestingLocationUpdates;
    protected Location mCurrentLocation;
    protected LocationRequest mLocationRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 8000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private final IBinder mBinder = new MyBinder();
    private boolean recordDistance;

    public LocationBackgroundService() {

        distanceCalculator = new DistanceCalculator();
    }

    @Override
    public void onCreate() {
        super.onCreate();

//location update
        mRequestingLocationUpdates = false;

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(mGoogleApiClient.isConnected()) startUpdatesButtonHandler();

            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    public void canRecordDistance(boolean b) {
        recordDistance = b;
    }

    public class MyBinder extends Binder {
        public LocationBackgroundService getService() {
            return LocationBackgroundService.this;
        }
    }

    public void createNotification(){

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |   Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("isNotificationLocation", true);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 6, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(LocationBackgroundService.this);
        mBuilder.setContentTitle("byKab is running")
                .setContentText("in progress..")
                .setSmallIcon(R.drawable.ic_about_us)
                .addAction(0, "STOP", contentIntent);

        startForeground(notif_id, mBuilder.getNotification());
    }

    public void setCallback(ServiceCallback callbacks) {
        serviceCallback = callbacks;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //Location Update Code
    protected synchronized void buildGoogleApiClient() {
        Log.i(AppConstants.DEBUG_TAG, "Building GoogleApiClient");
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

    /** calculates the distance between two locations in meters */
    private float compareLocation(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        //Log.v(AppConstants.DEBUG_TAG,"compareLocation dist :"+dist);
        return dist; // output distance, in meters
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        BigDecimal num = new BigDecimal(compareLocation(prevLocation.getLatitude(),prevLocation.getLongitude(),location.getLatitude(),location.getLongitude()));
        String numWithNoExponents = num.toPlainString();

        Log.d(AppConstants.DEBUG_TAG, "meter numWithNoExponents " + numWithNoExponents);

        if(recordDistance && num.floatValue() > 1){

            distance = distance + distanceCalculator.distance(prevLocation.getLatitude(),prevLocation.getLongitude(), location.getLatitude(),location.getLongitude(),"K");


            Log.v(AppConstants.DEBUG_TAG,"onLocationChanged distance : "+ distance);

            serviceCallback.updateDistance(distance);
        }

        prevLocation = location;

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
        Log.i(AppConstants.DEBUG_TAG, "Connection suspended");
        if(mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(AppConstants.DEBUG_TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(AppConstants.DEBUG_TAG, "Connected to GoogleApiClient");
        if (mCurrentLocation == null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                prevLocation = mCurrentLocation;
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
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();

        stopSelf();

    }

    public void completeNotification(){

        stopForeground(true);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(false);
        mNotifyManager.notify(notif_id, mBuilder.getNotification());
        mNotifyManager.cancel(notif_id);
    }
}
