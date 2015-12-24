package com.nap.bycab.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.activity.BaseActivity;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.models.Driver;
import com.nap.bycab.models.Order;
import com.nap.bycab.models.RideResponse;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.MapStateListener;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;
import com.nap.bycab.util.TouchableMapFragment;
import com.nap.bycab.util.TouchableWrapper;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private MapView mapView;
    public static GoogleMap map;
    private View view;
    private LocationManager manager;
    private View locationButton;
    private ImageView tvGPS,imgCall;
    private TextView tvTimeLeft,tvAccept;
    private boolean isAccepted,isStarted;
    public LinearLayout lvCustomerCall;
    private RideResponse rideResponse;
    private RelativeLayout rootLayout;
    private Order currentOrder;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_home, container, false);
        tvGPS= (ImageView) view.findViewById(R.id.imgGPS);
        tvTimeLeft= (TextView) view.findViewById(R.id.tvTimeLeft);
        tvAccept= (TextView) view.findViewById(R.id.tvAccept);
        lvCustomerCall= (LinearLayout) view.findViewById(R.id.lvCustomerCall);
        rootLayout= (RelativeLayout) view.findViewById(R.id.rootLayout);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                callCurrentRideService();
            }
        }.start();
        tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAccepted){
                    if(isStarted){
                        //stop button operation
                      //  callStopService();
                        tvAccept.setText("Done");
                    } else {
                        isStarted=true;
                        //start button operation
//                        callStartService();
                        tvAccept.setText("Stop");
                    }
                } else {
                    isAccepted=true;
                    //accept button operation
//                    callAcceptService();
                    tvAccept.setText("Start");
                }
            }
        });
        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimeLeft.setText( millisUntilFinished / 1000+" sec left" );
            }

            public void onFinish() {

            }
        }.start();
        imgCall= (ImageView) view.findViewById(R.id.imgCall);
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:9033701373"));
                startActivity(callIntent);
            }
        });


        tvGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("gps...","clicked");
                Location mCurrentLocation = ((MainActivity) getActivity()).getCurrentLocation();
                double latitude = mCurrentLocation.getLatitude();
                double longitude = mCurrentLocation.getLongitude();
                try {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
                    HomeFragment.map.animateCamera(cameraUpdate);
                    HomeFragment.map.clear();

                    HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).snippet("Me"));
                    Toast.makeText(getActivity(), mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
//            callLocationUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        ((MainActivity)getActivity()).getSupportActionBar().hide();

        if (!isGooglePlayServicesAvailable()) {
            getActivity().finish();
        }



        initUi();

        try {
            initilizeMap(savedInstanceState);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void callCurrentRideService() {

        final JSONObject object=new JSONObject();
        try {
            object.put("Id",PrefUtils.getCurrentDriver(getActivity()).getDriverId()+"");

            Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService " + object);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.CURRENT_RIDE_INFO,object){

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "call current rides resp " + response);
                RideResponse rideResponse=new GsonBuilder().create().fromJson(response,RideResponse.class);

                if(rideResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(rootLayout, rideResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                } else {
                    PrefUtils.setCurrentRideList(rideResponse, getActivity());
                    FragmentManager fragmentManager = getFragmentManager();
                    HomeFragment currentFragment = (HomeFragment) fragmentManager.findFragmentByTag("home_fragment");
                    currentFragment.lvCustomerCall.setVisibility(View.VISIBLE);
                    rideResponse=PrefUtils.getCurrentRideList(getActivity());
                    currentOrder=rideResponse.getAlUpcomingRides().get(0);
                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();

    }

    private void callStopService() {

    }

   

    private void callStartService() {
        final JSONObject object=new JSONObject();
        try {
            object.put("DriverId", PrefUtils.getCurrentDriver(getActivity()).getDriverId()+"");
            object.put("OrderId",""+currentOrder.getOrderId());
            object.put("Status","Start");
            Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService " + object);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.UPDATE_ORDER_STATUS,object){

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService resp " + response);
                CommonResponse commonResponse=new GsonBuilder().create().fromJson(response,CommonResponse.class);

                if(commonResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(rootLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                } else {
                    Snackbar snackbar=Snackbar.make(rootLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                    tvAccept.setText("Stop");

                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();
    }

    private void callAcceptService() {

        final JSONObject object=new JSONObject();
        try {
            object.put("DriverId", PrefUtils.getCurrentDriver(getActivity()).getDriverId()+"");
            object.put("OrderId",""+currentOrder.getOrderId());
            object.put("Status","Accept");
            Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService " + object);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.UPDATE_ORDER_STATUS,object){

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService resp " + response);
                CommonResponse commonResponse=new GsonBuilder().create().fromJson(response,CommonResponse.class);

                if(commonResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(rootLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                } else {
                    Snackbar snackbar=Snackbar.make(rootLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                    tvAccept.setText("Start");

                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();
    }


    private void initUi() {

        view.findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).openCloseDrawer();
            }
        });
    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap(Bundle savedInstanceState) {



        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setMyLocationEnabled(false);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());
         //This goes up to 21
        LatLng latLng=new LatLng(map.getMyLocation().getLatitude(),map.getMyLocation().getLongitude());



    }

    @Override
    public void onResume() {

        if(mapView != null) mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mapView != null) mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mapView != null) mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mapView != null) mapView.onLowMemory();
    }



    private boolean isGooglePlayServicesAvailable() {

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }

}
