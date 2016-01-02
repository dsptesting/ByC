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
import com.nap.bycab.models.Order;
import com.nap.bycab.models.RideResponse;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyRidesFragment extends Fragment {

    private ListView lvUpcomingRides;
    private ArrayList<Order> alRides;
    private MyAdapter myAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;
    private FrameLayout flMyRides;

    private String mParam1;
    private String mParam2;

    private TextView tvMyRideEmpty;

    public static MyRidesFragment newInstance(String param1, String param2) {

        MyRidesFragment fragment = new MyRidesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyRidesFragment() {
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

        view = inflater.inflate(R.layout.fragment_my_rides, container, false);


        ((MainActivity)getActivity()).getSupportActionBar().show();

        initData();

        initUi();

        callRidesService();

        return view;
    }

    private void callRidesService() {


        JSONObject object = new JSONObject();
        try {
            object.put("Id", PrefUtils.getCurrentDriver(getActivity()).getDriverId() + "");
            Log.e(AppConstants.DEBUG_TAG, "callRidesService request: " + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.MY_RIDES, object) {

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "callUpcomingRidesService Response: " + response);
                RideResponse rideResponse = new GsonBuilder().create().fromJson(response, RideResponse.class);

                if (rideResponse.getResponseId().equalsIgnoreCase("0")) {
                    Snackbar snackbar = Snackbar.make(flMyRides, rideResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                } else {
                    // PrefUtils.setCurrentDriver(loginResponse.getDriver(),getActivity());

                    alRides.clear();
                    alRides.addAll(rideResponse.getAlUpcomingRides());
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();
    }


    private void initData() {

        alRides = new ArrayList<>();

        flMyRides = (FrameLayout) view.findViewById(R.id.flMyRides);
        /*Ride ride1= new Ride();
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

        lvUpcomingRides = (ListView) view.findViewById(R.id.lvUpcomingRides);
        tvMyRideEmpty= (TextView) view.findViewById(R.id.tvMyRideEmpty);
        lvUpcomingRides.setEmptyView(tvMyRideEmpty);
        myAdapter = new MyAdapter(getActivity(), alRides);

        lvUpcomingRides.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if(convertView == null){

                convertView = inflater.inflate(R.layout.upcoming_rides_item, parent, false);

                viewHolder = new ViewHolder();

                viewHolder.rlActions = (RelativeLayout) convertView.findViewById(R.id.rlActions);
                viewHolder.tvCustomerMobile = (TextView) convertView.findViewById(R.id.tvCustomerMobile);
                viewHolder.tvCustomerName = (TextView) convertView.findViewById(R.id.tvCustomerName);
                viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
                viewHolder.tvMinute = (TextView) convertView.findViewById(R.id.tvMinute);
                viewHolder.tvWaitMinute = (TextView) convertView.findViewById(R.id.tvWaitMinute);
                viewHolder.tvSrcValue = (TextView) convertView.findViewById(R.id.tvSrcValue);
                viewHolder.tvDesValue = (TextView) convertView.findViewById(R.id.tvDesValue);
                viewHolder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
                viewHolder.tvKms = (TextView) convertView.findViewById(R.id.tvKms);
                viewHolder.tvSrc = (TextView) convertView.findViewById(R.id.tvSrc);
                viewHolder.tvDes = (TextView) convertView.findViewById(R.id.tvDes);

                convertView.setTag(viewHolder);
            }
            else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.rlActions.setVisibility(View.GONE);
            viewHolder.tvSrcValue.setVisibility(View.VISIBLE);
            viewHolder.tvDesValue.setVisibility(View.VISIBLE);
            viewHolder.tvSrc.setVisibility(View.VISIBLE);
            viewHolder.tvDes.setVisibility(View.VISIBLE);

            viewHolder.tvCustomerMobile.setText("" + al.get(position).getCustMobile());
            viewHolder.tvCustomerName.setText("" + al.get(position).getCustName());
            viewHolder.tvPrice.setText("Total " + al.get(position).getAmount()+"\u20B9");
            viewHolder.tvKms.setText(""+ al.get(position).getKM()+" kms");
            viewHolder.tvSrcValue.setText(""+ al.get(position).getPickUpLocation());
            viewHolder.tvDesValue.setText(""+ al.get(position).getDropLocation());
            viewHolder.tvDate.setText(al.get(position).getOrderDate()+" "+al.get(position).getTime() );
            viewHolder.tvMinute.setText(al.get(position).getJournyTime()+ " min");
            viewHolder.tvWaitMinute.setText(al.get(position).getWaitingTime()+ " min");
            return convertView;
        }


        private class ViewHolder{

            RelativeLayout rlActions;
            TextView tvCustomerName;
            TextView tvCustomerMobile;
            TextView tvDate;
            TextView tvTime;
            TextView tvSrc;
            TextView tvSrcValue;
            TextView tvDes;
            TextView tvDesValue;
            TextView tvKms;
            TextView tvPrice,tvMinute,tvWaitMinute;
        }

    }

}
