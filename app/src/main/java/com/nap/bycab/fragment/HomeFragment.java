package com.nap.bycab.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nap.bycab.R;
import com.nap.bycab.activity.BaseActivity;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.util.MapStateListener;
import com.nap.bycab.util.TouchableMapFragment;
import com.nap.bycab.util.TouchableWrapper;

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
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
//        Location location=((MainActivity)getActivity()).getCurrentLocation();
        // Updates the location and zoom of the MapView
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 10);
//        map.animateCamera(cameraUpdate);

        // Get the button view
//        locationButton = (View) view.findViewById(Integer.parseInt("2"));
//        locationButton.setVisibility(View.VISIBLE);

//        TypedValue tv = new TypedValue();
//        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//
//            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
//            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            map.setPadding(getActivity().getResources().getDimensionPixelSize(R.dimen.v15dp), actionBarHeight + getActivity().getResources().getDimensionPixelSize(R.dimen.v15dp) + getActivity().getResources().getDimensionPixelSize(R.dimen.v15dp), 0, 0);
//            locationButton.setLayoutParams(rlp);
//        }


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
