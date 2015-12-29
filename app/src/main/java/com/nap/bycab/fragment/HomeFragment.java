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
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.CompoundButton;
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
import com.nap.bycab.activity.FairActivity;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.models.Driver;
import com.nap.bycab.models.Order;
import com.nap.bycab.models.RideResponse;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.BottomViewPager;
import com.nap.bycab.util.MapStateListener;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;
import com.nap.bycab.util.TouchableMapFragment;
import com.nap.bycab.util.TouchableWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private BottomViewPager pager=null;
    private CardView cvCurrentRideDetails;
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
    private TextView tvStartStop;
    private TextView etKmVal;
    private Chronometer etWaitTimeVal,etTimeVal;
    private SwitchCompat switchWait;
    private long mLastStopTime;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_home, container, false);
        tvGPS= (ImageView) view.findViewById(R.id.imgGPS);
        etKmVal= (TextView) view.findViewById(R.id.etKmVal);
        tvStartStop= (TextView) view.findViewById(R.id.tvStartStop);

        etTimeVal= (Chronometer) view.findViewById(R.id.etTimeVal);
        etWaitTimeVal= (Chronometer) view.findViewById(R.id.etWaitTimeVal);


        switchWait= (SwitchCompat) view.findViewById(R.id.switchWait);
        switchWait.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    // on first start
                    if ( mLastStopTime == 0 )
                        etWaitTimeVal.setBase( SystemClock.elapsedRealtime() );
                        // on resume after pause
                    else
                    {
                        long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
                        etWaitTimeVal.setBase( etWaitTimeVal.getBase() + intervalOnPause );
                    }

                    etWaitTimeVal.start();
                    etWaitTimeVal.start();
                } else {

                    etWaitTimeVal.stop();
                    mLastStopTime = SystemClock.elapsedRealtime();
                }
            }
        });

        tvStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(isStarted){
                    //stop button operation
                    //updateOrderService(AppConstants.ORDER_STATUS_COMPLETE);
                    tvStartStop.setText("DONE");

                    ((MainActivity)getActivity()).myService.canRecordDistance(false);
                    ((MainActivity)getActivity()).myService.completeNotification();

                    Toast.makeText(getActivity(),"time "+(SystemClock.elapsedRealtime()-etTimeVal.getBase())/1000+" seconds \n wait time "+(SystemClock.elapsedRealtime()-etWaitTimeVal.getBase())/1000+" seconds",Toast.LENGTH_LONG).show();

                    etTimeVal.setBase(SystemClock.elapsedRealtime());
                    etTimeVal.stop();

                    Intent i=new Intent(getActivity(), FairActivity.class);
                    startActivity(i);
                }
                else
                {

                    etWaitTimeVal.setBase(SystemClock.elapsedRealtime());
                    etTimeVal.setBase(SystemClock.elapsedRealtime());
                    etTimeVal.start();
                    isStarted=true;
                    //start button operation
                    //updateOrderService(AppConstants.ORDER_STATUS_DRIVING);
                    //callStartService();

                    tvStartStop.setText("STOP");
                    ((MainActivity)getActivity()).myService.canRecordDistance(true);
                    ((MainActivity)getActivity()).myService.createNotification();
                }
            }
        });

        lvCustomerCall= (LinearLayout) view.findViewById(R.id.lvCustomerCall);
        rootLayout= (RelativeLayout) view.findViewById(R.id.rootLayout);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                callCurrentRideService();
            }
        }.start();

        /*
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
        });*/



        tvGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("gps...","clicked");
                Location mCurrentLocation = ((MainActivity) getActivity()).myService.getCurrentLocation();
                if(mCurrentLocation != null){
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

        /*final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();*/
        new PostServiceCall(AppConstants.CURRENT_RIDE_INFO,object){

            @Override
            public void response(String response) {
                //progressDialog.dismiss();
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
               // progressDialog.dismiss();
            }
        }.call();

    }

    private void updateOrderService(int statusCode) {

        final JSONObject object=new JSONObject();
        try {
            object.put("DriverId", PrefUtils.getCurrentDriver(getActivity()).getDriverId()+"");
            object.put("OrderId",""+currentOrder.getOrderId());
            object.put("Status",statusCode);
            Log.e(AppConstants.DEBUG_TAG, "updateOrderService " + object);
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
                   // tvAccept.setText("Stop");

                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();
    }

    /*private void callAcceptService() {

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
                    //tvAccept.setText("Start");

                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();
    }*/


    private void initUi() {

        view.findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).openCloseDrawer();
            }
        });


        cvCurrentRideDetails = (CardView) view.findViewById(R.id.cvCurrentRideDetails);
        pager=(BottomViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(new SampleAdapter(getActivity()));
        pager.setOffscreenPageLimit(3);
        int margin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30*2,     getResources().getDisplayMetrics());
        pager.setPageMargin(-margin);
        pager.setPadding(30, 0, 30, 0);
        pager.setClipToPadding(false);



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

    public void updateHomeFragmentDistance(double distance) {

        DecimalFormat df = new DecimalFormat("0.000");
        System.out.println(df.format(distance));

        if(etKmVal != null) etKmVal.setText(""+df.format(distance)+ " kms");
    }

    /*
   * Inspired by
   * https://gist.github.com/8cbe094bb7a783e37ad1
   */
    private class SampleAdapter extends PagerAdapter{

        Context context;
        LayoutInflater inflater;
        Animation animDown;
        Animation animUp;
        boolean isUpEnded;

        SampleAdapter(Context context){

            this.context = context;
            inflater =  LayoutInflater.from(context);
            animDown = AnimationUtils.loadAnimation(context,R.anim.slidedown);
            animDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animDown.cancel();
                    pager.setAnimation(null);
                    pager.setVisibility(View.GONE);
                    cvCurrentRideDetails.setVisibility(View.VISIBLE);
                    cvCurrentRideDetails.startAnimation(animUp);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });

            animUp = AnimationUtils.loadAnimation(context, R.anim.slideup);
            animUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animUp.cancel();
                    cvCurrentRideDetails.setAnimation(null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            final View page = inflater.inflate(R.layout.ride_noti_layout, container, false);

            final TextView tvAccept=(TextView)page.findViewById(R.id.tvAccept);
            tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    pager.startAnimation(animDown);

                    /*if(isAccepted){

                        if(isStarted){
                            //stop button operation
                            //updateOrderService(AppConstants.ORDER_STATUS_COMPLETE);
                            tvAccept.setText("DONE");
                        }
                        else {
                            isStarted=true;
                            //start button operation
                            //updateOrderService(AppConstants.ORDER_STATUS_DRIVING);
                            //callStartService();
                            tvAccept.setText("STOP");
                        }
                    }
                    else {
                        isAccepted=true;
                        //accept button operation
                        //callAcceptService();
                        //updateOrderService(AppConstants.ORDER_STATUS_ACCEPT);
                        tvAccept.setText("START");
                    }*/

                    if(!isAccepted){

                        isAccepted=true;
                        //accept button operation
                        //updateOrderService(AppConstants.ORDER_STATUS_ACCEPT);
                        tvAccept.setText("START");
                    }

                }
            });

            container.addView(page);

            return(page);
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return(3);
        }

        /*@Override
        public float getPageWidth(int position) {
            return(1f);
        }
*/
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return(view == object);
        }
    }

    @Override
    public void onResume() {

        if(mapView != null) mapView.onResume();
        super.onResume();
        try {
            Location mCurrentLocation = ((MainActivity) getActivity()).myService.getCurrentLocation();
            if (mCurrentLocation != null) {
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
        }catch (Exception e){
            e.printStackTrace();
        }
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
