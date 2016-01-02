package com.nap.bycab.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.CompoundButton;
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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.activity.FairActivity;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.models.Driver;
import com.nap.bycab.models.Order;
import com.nap.bycab.models.RideResponse;
import com.nap.bycab.models.Ticket;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.BottomViewPager;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<Order> alCurrentRidesVp;
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
    private boolean isAccepted/*,isStarted*/;
    public LinearLayout lvCustomerCall;
    private RideResponse rideResponse;
    private RelativeLayout rootLayout;
    private TextView tvStartStop;
    private TextView etKmVal;
    private Chronometer etWaitTimeVal,etTimeVal;
    private SwitchCompat switchWait;
    private long mLastStopTime;
    private VpCurrentRideAdapter vpCurrentRideAdapter;

    public double finalDistance;
    private Driver driver;
    Ticket ticket;
    public  static boolean isFromFairActivity=false;
    private Timer timerCallCurrentRideService;

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
        alCurrentRidesVp = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        long  waitMinute = (120 % 3600) / 60;
        long waitSecond = 120 % 60;

        long  fairMinute = (250 % 3600) / 60;
        long fairSeconds = 250 % 60;

        String waitTime = String.format("%02d:%02d", waitMinute, waitSecond);
        String fairTime = String.format("%02d:%02d",  fairMinute, fairSeconds);
        Log.e("time value...", waitTime + " " + fairTime + "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState){

        view = inflater.inflate(R.layout.fragment_home, container, false);
        tvGPS= (ImageView) view.findViewById(R.id.imgGPS);

        driver=PrefUtils.getCurrentDriver(getActivity());

        lvCustomerCall= (LinearLayout) view.findViewById(R.id.lvCustomerCall);
        rootLayout= (RelativeLayout) view.findViewById(R.id.rootLayout);

        timerCallCurrentRideService = new Timer();
        timerCallCurrentRideService.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Log.v(AppConstants.DEBUG_TAG, "getRunningRide : " + PrefUtils.getRunningRide(getActivity()));

                if (PrefUtils.getRunningRide(getActivity()) == null) {
                    callCurrentRideService();
                }

            }
        }, 10000, 60000);

        /*new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                callCurrentRideService();
            }
        }.start();*/

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
                Log.e("gps...", "clicked");
                Location mCurrentLocation = ((MainActivity) getActivity()).myService.getCurrentLocation();
                if (mCurrentLocation != null) {
                    double latitude = mCurrentLocation.getLatitude();
                    double longitude = mCurrentLocation.getLongitude();
                    try {
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15);
                        HomeFragment.map.animateCamera(cameraUpdate);
                        HomeFragment.map.clear();

                        HomeFragment.map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker)).snippet("Me"));
                        //     Toast.makeText(getActivity(), mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
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

    public void callCurrentRideService() {

        final JSONObject object=new JSONObject();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            Date date = sdf.parse(currentDateandTime);
            Calendar futureTime = Calendar.getInstance();
            futureTime.setTime(date);
            futureTime.add(Calendar.MINUTE, 45);



            Calendar pastTime = Calendar.getInstance();
            pastTime.setTime(date);
            pastTime.add(Calendar.MINUTE, -45);



            object.put("FromDate",sdf.format(pastTime.getTime()));
            object.put("Id",PrefUtils.getCurrentDriver(getActivity()).getDriverId()+"");
            object.put("ToDate",sdf.format(futureTime.getTime()));

            Log.e(AppConstants.DEBUG_TAG, "callCurrentRideService " + object);
        }
        catch (Exception e) {
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
                }
                else {
                    PrefUtils.setCurrentRideList(rideResponse, getActivity());
                    alCurrentRidesVp.clear();
                    ArrayList<Order> temp = PrefUtils.getCurrentRideList(getActivity()).getAlUpcomingRides();

                    Log.v(AppConstants.DEBUG_TAG,"temp filled :"+temp.toString());
                    if(temp != null){
                        for(int i=0;i<temp.size();i++){

                            Log.v(AppConstants.DEBUG_TAG,i+" temp status :"+temp.get(i).getOrderStatus());
                            if(temp.get(i).getOrderStatus() != null && temp.get(i).getOrderStatus().equalsIgnoreCase(""+AppConstants.ORDER_STATUS_PENDING)){
                                alCurrentRidesVp.add(temp.get(i));
                            }
                        }
                        Log.v(AppConstants.DEBUG_TAG,"alCurrentRidesVp filled :"+alCurrentRidesVp.toString());
                        vpCurrentRideAdapter.notifyDataSetChanged();
                    }

                    /*FragmentManager fragmentManager = getFragmentManager();
                    HomeFragment currentFragment = (HomeFragment) fragmentManager.findFragmentByTag("home_fragment");
                    currentFragment.lvCustomerCall.setVisibility(View.VISIBLE);
                    rideResponse=PrefUtils.getCurrentRideList(getActivity());
                    currentOrder=rideResponse.getAlUpcomingRides().get(0);*/
                }
            }

            @Override
            public void error(String error) {
               // progressDialog.dismiss();
            }
        }.call();

    }

    private void callCancelStatusService(String orderId) {

        final JSONObject object=new JSONObject();
        try {
            object.put("DriverId", PrefUtils.getCurrentDriver(getActivity()).getDriverId() + "");
            object.put("OrderId", "" + orderId);
            object.put("Status", AppConstants.ORDER_STATUS_CANCEL);

            Log.e(AppConstants.DEBUG_TAG, "callCancelStatusService " + object);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

       /* final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();*/
        new PostServiceCall(AppConstants.UPDATE_ORDER_STATUS,object){

            @Override
            public void response(String response) {
               // progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "callCancelStatusService resp " + response);
                CommonResponse commonResponse=new GsonBuilder().create().fromJson(response,CommonResponse.class);

                if(commonResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(rootLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                }
                else {
                    /*Snackbar snackbar=Snackbar.make(rootLayout, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();*/

                }
            }

            @Override
            public void error(String error) {
                Toast.makeText(getActivity(),"Something went wrong!",Toast.LENGTH_SHORT).show();
               //progressDialog.dismiss();
            }
        }.call();
    }


    private void callStatusService(boolean isAccept) {

        final JSONObject object=new JSONObject();
        try {
            object.put("DriverId", PrefUtils.getCurrentDriver(getActivity()).getDriverId() + "");
            object.put("OrderId", "" + PrefUtils.getRunningRide(getActivity()).getOrderId());
            if(isAccept) {
                object.put("Status", AppConstants.ORDER_STATUS_ACCEPT);
            } else {
                object.put("Status", AppConstants.    ORDER_STATUS_DRIVING);
            }

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
    }


    private void initUi() {

        view.findViewById(R.id.ivMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).openCloseDrawer();
            }
        });

        vpCurrentRideAdapter = new VpCurrentRideAdapter(getActivity(),alCurrentRidesVp);

        cvCurrentRideDetails = (CardView) view.findViewById(R.id.cvCurrentRideDetails);
        pager=(BottomViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(vpCurrentRideAdapter);
        pager.setOffscreenPageLimit(alCurrentRidesVp.size());
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

        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println(df.format(distance));
        finalDistance=distance;
        if(etKmVal != null) etKmVal.setText(""+df.format(distance)+ " kms");
    }

    public void loadRunningRide() {

        if(PrefUtils.getRunningRide(getActivity()) != null){

            pager.setVisibility(View.INVISIBLE);
            cvCurrentRideDetails.setVisibility(View.VISIBLE);
            loadRunningRideData();
        }
    }

    private void loadRunningRideData() {

        final Order order= PrefUtils.getRunningRide(getActivity());

        ((TextView) cvCurrentRideDetails.findViewById(R.id.etNameVal)).setText("" +order.getCustName());
        ((TextView) cvCurrentRideDetails.findViewById(R.id.etPhoneVal)).setText("" +order.getCustMobile());
        //TODO set this ((Chronometer) cvCurrentRideDetails.findViewById(R.id.etTimeVal)).set...
        ((TextView) cvCurrentRideDetails.findViewById(R.id.etKmVal)).setText("0 Km");

        etKmVal= (TextView) cvCurrentRideDetails.findViewById(R.id.etKmVal);
        tvStartStop= (TextView) cvCurrentRideDetails.findViewById(R.id.tvStartStop);

//        new Timer().scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                long time = (SystemClock.elapsedRealtime()-((MainActivity) getActivity()).myService.getTimerValues());
//                Log.e("time", time + "");
//
////                Toast.makeText(getActivity(), time + "", Toast.LENGTH_LONG).show();
//            }
//        }, 0, 1000);

        etTimeVal= (Chronometer) cvCurrentRideDetails.findViewById(R.id.etTimeVal);
        etWaitTimeVal= (Chronometer) cvCurrentRideDetails.findViewById(R.id.etWaitTimeVal);

        switchWait= (SwitchCompat) cvCurrentRideDetails.findViewById(R.id.switchWait);
        switchWait.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // on first start
                    if (mLastStopTime == 0)
                        etWaitTimeVal.setBase(SystemClock.elapsedRealtime());
                        // on resume after pause
                    else {
                        long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
                        etWaitTimeVal.setBase(etWaitTimeVal.getBase() + intervalOnPause);
                    }


                    etWaitTimeVal.start();
                } else {

                    etWaitTimeVal.stop();
                    mLastStopTime = SystemClock.elapsedRealtime();
                }
            }
        });

        ((TextView) cvCurrentRideDetails.findViewById(R.id.etKmVal)).setVisibility(View.INVISIBLE);
        etTimeVal.setVisibility(View.INVISIBLE);
        etWaitTimeVal.setVisibility(View.INVISIBLE);
        switchWait.setVisibility(View.INVISIBLE);

        tvStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (order.getOrderStatus().equalsIgnoreCase(""+AppConstants.ORDER_STATUS_DRIVING)) {
                    //stop button operation


                    ((MainActivity) getActivity()).myService.canRecordDistance(false);
                    PrefUtils.setServiceRunningInBackground(false, getActivity());
                    //TODO enable bellow line in future
//                    ((MainActivity) getActivity()).myService.completeNotification();
//                    long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
//                    etWaitTimeVal.setBase(etWaitTimeVal.getBase() + intervalOnPause);

                    // TODO.. suspicious code below commented..
                /*
                    try {
                        PrefUtils.clearCurrentDriver(getActivity());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                  */

                    if (switchWait.isChecked()) {
                        ticket = new Ticket(driver.getName(), driver.getMobileNo(), (SystemClock.elapsedRealtime() - etWaitTimeVal.getBase()) / 1000, (SystemClock.elapsedRealtime() - etTimeVal.getBase()) / 1000, finalDistance);
                        Toast.makeText(getActivity(), "time " + (SystemClock.elapsedRealtime() - etTimeVal.getBase()) / 1000 + " seconds \n wait time " + (SystemClock.elapsedRealtime() - etWaitTimeVal.getBase()) / 1000 + " seconds", Toast.LENGTH_LONG).show();
                    } else {
                        ticket = new Ticket(driver.getName(), driver.getMobileNo(), (mLastStopTime - etWaitTimeVal.getBase()) / 1000, (SystemClock.elapsedRealtime() - etTimeVal.getBase()) / 1000, finalDistance);
                        Toast.makeText(getActivity(), "time " + (SystemClock.elapsedRealtime() - etTimeVal.getBase()) / 1000 + " seconds \n wait time " + (mLastStopTime - etWaitTimeVal.getBase()) / 1000 + " seconds", Toast.LENGTH_LONG).show();
                    }

                    etTimeVal.setBase(SystemClock.elapsedRealtime());
                    etTimeVal.stop();

//                    Intent i=new Intent(getActivity(), FairActivity.class);
//                    startActivity(i);


//                    Toast.makeText(getActivity(), "time " +ticket.getWaitTime()+ " seconds \n wait time " +ticket.getDurationTime()+ " seconds", Toast.LENGTH_LONG).show();

                    PrefUtils.setTicketInfo(ticket, getActivity());

                    Intent i = new Intent(getActivity(), FairActivity.class);
                    startActivity(i);

                }
                else if(order.getOrderStatus().equalsIgnoreCase(""+AppConstants.ORDER_STATUS_ACCEPT)) {

                    //call start service (Start button operation)
                    callStatusService(false);
                    etWaitTimeVal.setBase(SystemClock.elapsedRealtime());
                    etTimeVal.setBase(SystemClock.elapsedRealtime());
                    etTimeVal.start();
                    order.setOrderStatus(AppConstants.ORDER_STATUS_DRIVING + "");
                    PrefUtils.setRunningRide(order,getActivity());
                    //start button operation
                    //updateOrderService(AppConstants.ORDER_STATUS_DRIVING);
                    //callStartService();

                    tvStartStop.setText("STOP");
                    ((TextView) cvCurrentRideDetails.findViewById(R.id.etKmVal)).setVisibility(View.VISIBLE);
                    etTimeVal.setVisibility(View.VISIBLE);
                    etWaitTimeVal.setVisibility(View.VISIBLE);
                    switchWait.setVisibility(View.VISIBLE);


                    ((MainActivity) getActivity()).myService.canRecordDistance(true);
                    PrefUtils.setServiceRunningInBackground(true, getActivity());
                    //TODO enable bellow line in future
//                    ((MainActivity) getActivity()).myService.createNotification(etTimeVal.getBase());
                }
            }
        });

        /*((TextView) cvCurrentRideDetails.findViewById(R.id.tvStartStop)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((TextView) cvCurrentRideDetails.findViewById(R.id.tvStartStop)).setText("DONE");

                ((MainActivity)getActivity()).myService.canRecordDistance(false);
                PrefUtils.setServiceRunningInBackground(false, getActivity());
                ((MainActivity)getActivity()).myService.completeNotification();
//                    long intervalOnPause = (SystemClock.elapsedRealtime() - mLastStopTime);
//                    etWaitTimeVal.setBase(etWaitTimeVal.getBase() + intervalOnPause);

                if(switchWait.isChecked()){
                    ticket=new Ticket(driver.getName(),driver.getMobileNo(),(SystemClock.elapsedRealtime()-etWaitTimeVal.getBase())/1000,(SystemClock.elapsedRealtime()-etTimeVal.getBase())/1000,finalDistance);
                    Toast.makeText(getActivity(),"time "+(SystemClock.elapsedRealtime()-etTimeVal.getBase())/1000+" seconds \n wait time "+(SystemClock.elapsedRealtime()-etWaitTimeVal.getBase())/1000+" seconds",Toast.LENGTH_LONG).show();
                }
                else {
                    ticket =new Ticket(driver.getName(),driver.getMobileNo(),(mLastStopTime-etWaitTimeVal.getBase())/1000,(SystemClock.elapsedRealtime()-etTimeVal.getBase())/1000,finalDistance);
                    Toast.makeText(getActivity(),"time "+(SystemClock.elapsedRealtime()-etTimeVal.getBase())/1000+" seconds \n wait time "+(mLastStopTime-etWaitTimeVal.getBase())/1000+" seconds",Toast.LENGTH_LONG).show();
                }

                etTimeVal.setBase(SystemClock.elapsedRealtime());
                etTimeVal.stop();

//                    Intent i=new Intent(getActivity(), FairActivity.class);
//                    startActivity(i);


//                    Toast.makeText(getActivity(), "time " +ticket.getWaitTime()+ " seconds \n wait time " +ticket.getDurationTime()+ " seconds", Toast.LENGTH_LONG).show();

                PrefUtils.setTicketInfo(ticket, getActivity());
                Intent i=new Intent(getActivity(), FairActivity.class);
                startActivity(i);
            }
        });*/
        //TODO set this ((Chronometer) cvCurrentRideDetails.findViewById(R.id.etWaitTimeVal)).set...
    }



    private class VpCurrentRideAdapter extends PagerAdapter{

        Context context;
        LayoutInflater inflater;
        Animation animDown;
        Animation animUp;
        boolean isUpEnded;
        public ArrayList<Order> alCurrentRides;
        CountDownTimer countDownTimer;

        VpCurrentRideAdapter(Context context, ArrayList<Order> alCurrentRides){

            this.alCurrentRides = alCurrentRides;
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
                    loadRunningRideData();
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
        public Object instantiateItem(ViewGroup container, final int position) {

            Log.v(AppConstants.DEBUG_TAG, "instantiateItem : " + alCurrentRides.get(position) + ", position: " + position);

            final View page = inflater.inflate(R.layout.ride_noti_layout, container, false);
            ((TextView)page.findViewById(R.id.tvSrcValue)).setText("" + alCurrentRides.get(position).getPickUpLocation());
            ((TextView)page.findViewById(R.id.tvDesValue)).setText("" + alCurrentRides.get(position).getDropLocation());

            final TextView tvCancel=(TextView)page.findViewById(R.id.tvCancel);
            tvCancel.setTag(""+position);

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(countDownTimer!= null) countDownTimer.cancel();
                    int p = Integer.parseInt(tvCancel.getTag().toString());
                    cancelOrder(alCurrentRides.get(p).getOrderId(),p);
                }
            });

            final TextView tvTimer=(TextView)page.findViewById(R.id.tvTimer);
            tvTimer.setTag(""+position);

            countDownTimer = new CountDownTimer(120*1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(""+millisUntilFinished/1000);
                }

                public void onFinish() {

                    if(countDownTimer!= null) countDownTimer.cancel();
                    int p = Integer.parseInt(tvCancel.getTag().toString());
                    cancelOrder(alCurrentRides.get(p).getOrderId(),p);
                }
            };
            countDownTimer.start();

            final TextView tvAccept=(TextView)page.findViewById(R.id.tvAccept);
            tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(countDownTimer!= null) countDownTimer.cancel();
                    pager.startAnimation(animDown);

                    if(!isAccepted){

                        Order acceptedOrder = alCurrentRides.get(position);
                        acceptedOrder.setOrderStatus(""+AppConstants.ORDER_STATUS_ACCEPT);
                        PrefUtils.setRunningRide(acceptedOrder, getActivity());
                        isAccepted=true;
                        //accept button operation
                        //updateOrderService(AppConstants.ORDER_STATUS_ACCEPT);
                        //tvAccept.setText("START");
                        callStatusService(true);

                    }

                }
            });

            container.addView(page);

            return(page);
        }

        public int removeViewFromPager (ViewPager pager, int position)
        {
            // ViewPager doesn't have a delete method; the closest is to set the adapter
            // again.  When doing so, it deletes all its views.  Then we can delete the view
            // from from the adapter and finally set the adapter to the pager again.  Note
            // that we set the adapter to null before removing the view from "views" - that's
            // because while ViewPager deletes all its views, it will call destroyItem which
            // will in turn cause a null pointer ref.
            pager.setAdapter(null);
            alCurrentRides.remove(position);
            pager.setAdapter(this);

            return position;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            if(countDownTimer!= null) countDownTimer.cancel();
        }

        @Override
        public int getCount() {
            return(alCurrentRides.size());
        }
        public int getItemPosition(Object object) {
            return POSITION_UNCHANGED;
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

    private void cancelOrder(String orderId,int pos) {

        // call cancel webservice..
        callCancelStatusService(orderId);
        vpCurrentRideAdapter.removeViewFromPager(pager, pos);
    }

    @Override
    public void onResume() {

        if(mapView != null) mapView.onResume();
        super.onResume();

        if(isFromFairActivity) {
            cvCurrentRideDetails.setVisibility(View.INVISIBLE);
        }
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
                   // Toast.makeText(getActivity(), mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
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
        if(timerCallCurrentRideService != null) timerCallCurrentRideService.cancel();
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
