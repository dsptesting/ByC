package com.nap.bycab.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.models.Order;
import com.nap.bycab.models.RideResponse;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpComingRidesFragment extends Fragment {

    private ListView lvUpcomingRides;
    private ArrayList<Order> alUpcomingRides;
    private MyAdapter myAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FrameLayout rootUpcomingRides;

    private String mParam1;
    private String mParam2;
    private View view;

    private TextView tvUpcomingRideEmpty;


    public static UpComingRidesFragment newInstance(String param1, String param2) {
        UpComingRidesFragment fragment = new UpComingRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UpComingRidesFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_upcoming_rides, container, false);



        ((MainActivity)getActivity()).getSupportActionBar().show();

        initData();

        initUi();

        callUpcomingRidesService();

        return view;
    }

    private void initData() {

        alUpcomingRides = new ArrayList<>();

       /* Ride ride1= new Ride();
        ride1.setCustomerMobile("09898989898");
        ride1.setCustomerName("Darji Palak Suresh");
        ride1.setDateTime(new Date().getTime());
        ride1.setId("1");
        ride1.setKms("4.6 kms");
        ride1.setFare("Rs 64 - Rs 78");
        ride1.setSrc("163, Ajit nagar, Urmi char rasta, Akota, Vadodara -390020");
        ride1.setDes("Pujer complex, Subhanpura, Vadodara -390020");
        alUpcomingRides.add(ride1);

        Ride ride2= new Ride();
        ride2.setCustomerMobile("09898989898");
        ride2.setCustomerName("Darji Palak Suresh");
        ride2.setDateTime(new Date().getTime());
        ride2.setId("2");
        ride2.setKms("4.6 kms");
        ride2.setFare("Rs 64 - Rs 78");
        ride2.setSrc("163, Ajit nagar, Urmi char rasta, Akota, Vadodara -390020");
        ride2.setDes("Pujer complex, Subhanpura, Vadodara -390020");
        alUpcomingRides.add(ride2);*/
    }

    private void initUi() {

        rootUpcomingRides = (FrameLayout) view.findViewById(R.id.rootUpcomingRides);

        lvUpcomingRides = (ListView) view.findViewById(R.id.lvUpcomingRides);
        tvUpcomingRideEmpty= (TextView) view.findViewById(R.id.tvUpcomingRideEmpty);
        lvUpcomingRides.setEmptyView(tvUpcomingRideEmpty);
        myAdapter = new MyAdapter(getActivity(), alUpcomingRides);

        lvUpcomingRides.setAdapter(myAdapter);
    }

    private void callUpcomingRidesService() {


        JSONObject object=new JSONObject();
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            Date date = sdf.parse(currentDateandTime);

            Calendar pastTime = Calendar.getInstance();
            pastTime.setTime(date);


            object.put("FromDate",sdf.format(pastTime.getTime()));
            object.put("Id",PrefUtils.getCurrentDriver(getActivity()).getDriverId()+"");
            object.put("ToDate","2099-12-12 12:12:12");



            Log.e(AppConstants.DEBUG_TAG,"callUpcomingRidesService request: "+ object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.UPCOMING_RIDES,object){

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "callUpcomingRidesService Response: "+response);
                RideResponse rideResponse =new GsonBuilder().create().fromJson(response,RideResponse.class);

                if(rideResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(rootUpcomingRides, rideResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                }
                else {
                   // PrefUtils.setCurrentDriver(loginResponse.getDriver(),getActivity());

                    alUpcomingRides.clear();
                    alUpcomingRides.addAll(rideResponse.getAlUpcomingRides());
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();


    }
    private class MyAdapter extends BaseAdapter{

        Context context;
        ArrayList<Order> al;
        LayoutInflater inflater;

        MyAdapter(Context context, ArrayList<Order> al){

            this.context = context;
            inflater = LayoutInflater.from(this.context);
            this.al = al;
        }

        @Override
        public int getCount() {
            return al.size();
        }

        @Override
        public Object getItem(int position) {
            return al.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if(convertView == null){

                convertView = inflater.inflate(R.layout.upcoming_rides_item, parent, false);

                viewHolder = new ViewHolder();

                viewHolder.tvCustomerMobile = (TextView) convertView.findViewById(R.id.tvCustomerMobile);
                viewHolder.tvCustomerName = (TextView) convertView.findViewById(R.id.tvCustomerName);
                viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                viewHolder.tvSrcValue = (TextView) convertView.findViewById(R.id.tvSrcValue);
                viewHolder.tvDesValue = (TextView) convertView.findViewById(R.id.tvDesValue);
                viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
                viewHolder.tvKms = (TextView) convertView.findViewById(R.id.tvKms);
                viewHolder.tvPlus2 = (TextView) convertView.findViewById(R.id.tvPlus2);
                viewHolder.tvWaitTime = (TextView) convertView.findViewById(R.id.tvWaitMinute);
                viewHolder.tvAccept= (TextView) convertView.findViewById(R.id.tvAccept);
                viewHolder.tvCancel= (TextView) convertView.findViewById(R.id.tvCancel);
                viewHolder.tvStatus= (TextView) convertView.findViewById(R.id.tvStatus);
                viewHolder.rlActions= (RelativeLayout) convertView.findViewById(R.id.rlActions);
                viewHolder.tvMinute= (TextView) convertView.findViewById(R.id.tvMinute);

                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvDate.setText(al.get(position).getOrderDate()+" "+al.get(position).getTime());

            viewHolder.tvMinute.setText(""+ al.get(position).getJournyTime()+" min");
            viewHolder.tvCustomerMobile.setText(""+ al.get(position).getCustMobile());
            viewHolder.tvCustomerName.setText(""+ al.get(position).getCustName());
            viewHolder.tvSrcValue.setText(""+ al.get(position).getPickUpLocation());
            viewHolder.tvDesValue.setText(""+ al.get(position).getDropLocation());
            viewHolder.tvPrice.setText("approx " + al.get(position).getAmount() + "\u20B9");
            viewHolder.tvKms.setText("" + al.get(position).getKM() + " kms");
            viewHolder.tvPlus2.setVisibility(View.GONE);
            viewHolder.tvWaitTime.setVisibility(View.GONE);

            if(al.get(position).getOrderStatus().toString().equalsIgnoreCase(AppConstants.ORDER_STATUS_CANCEL+"")){
                viewHolder.tvStatus.setText("Status: Canceled");
                viewHolder.rlActions.setVisibility(View.GONE);
            } else if(al.get(position).getOrderStatus().toString().equalsIgnoreCase(AppConstants.ORDER_STATUS_ACCEPT+"")) {
                viewHolder.tvStatus.setText("Status: Accepted");
                viewHolder.rlActions.setVisibility(View.GONE);
            } else {
                viewHolder.tvStatus.setText("Status: Pending");
                viewHolder.rlActions.setVisibility(View.VISIBLE);
            }
            viewHolder.tvAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callStatusService(true,al.get(position).getOrderId());
                }
            });

            viewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callStatusService(false,al.get(position).getOrderId());
                }
            });


            return convertView;
        }


        private class ViewHolder{

            TextView tvCustomerName;
            TextView tvCustomerMobile;
            TextView tvDate;
            TextView tvTime;
            TextView tvSrcValue;
            TextView tvDesValue;
            TextView tvKms;
            TextView tvPrice;
            TextView tvPlus2;
            TextView tvWaitTime,tvAccept,tvCancel,tvStatus,tvMinute;
            RelativeLayout rlActions;
        }

    }



    private void callStatusService(boolean isAccept,String orderId) {

        final JSONObject object=new JSONObject();
        try {
            object.put("DriverId", PrefUtils.getCurrentDriver(getActivity()).getDriverId() + "");
            object.put("OrderId", orderId);
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
                    Snackbar snackbar=Snackbar.make(rootUpcomingRides, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                } else {
                    Snackbar snackbar=Snackbar.make(rootUpcomingRides, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                    //tvAccept.setText("Start");
                    callUpcomingRidesService();
                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();
    }

}
