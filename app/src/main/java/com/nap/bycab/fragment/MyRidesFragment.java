package com.nap.bycab.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nap.bycab.R;
import com.nap.bycab.activity.BaseActivity;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.models.Ride;

import java.util.ArrayList;
import java.util.Date;

public class MyRidesFragment extends Fragment {

    private ListView lvUpcomingRides;
    private ArrayList<Ride> alUpcomingRides;
    private MyAdapter myAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View view;

    private String mParam1;
    private String mParam2;

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

        return view;
    }

    private void initData() {

        alUpcomingRides = new ArrayList<>();

        Ride ride1= new Ride();
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
        alUpcomingRides.add(ride2);
    }

    private void initUi() {

        lvUpcomingRides = (ListView) view.findViewById(R.id.lvUpcomingRides);

        myAdapter = new MyAdapter(getActivity(), alUpcomingRides);

        lvUpcomingRides.setAdapter(myAdapter);
    }

    private class MyAdapter extends BaseAdapter {

        Context context;
        ArrayList<Ride> al;
        LayoutInflater inflater;

        MyAdapter(Context context, ArrayList<Ride> al){

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
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
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
            viewHolder.tvSrcValue.setVisibility(View.GONE);
            viewHolder.tvDesValue.setVisibility(View.GONE);
            viewHolder.tvSrc.setVisibility(View.GONE);
            viewHolder.tvDes.setVisibility(View.GONE);

            viewHolder.tvCustomerMobile.setText(""+ al.get(position).getCustomerMobile());
            viewHolder.tvCustomerName.setText(""+ al.get(position).getCustomerName());
            viewHolder.tvPrice.setText(""+ al.get(position).getFare());
            viewHolder.tvKms.setText(""+ al.get(position).getKms());

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
            TextView tvPrice;
        }

    }

}
