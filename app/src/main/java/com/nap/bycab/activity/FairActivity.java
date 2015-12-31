package com.nap.bycab.activity;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.nap.bycab.R;
import com.nap.bycab.models.Order;
import com.nap.bycab.models.Ticket;
import com.nap.bycab.util.PrefUtils;

import java.text.DecimalFormat;

public class FairActivity extends BaseActivity {

    private TextView tvCustomerNameVal,tvCustomerNoVal,tvRideTimeVal,tvDisVal,tvWaitValue,tvFairValue;

    private Ticket ticket;
    private double totalAmount;
    DecimalFormat df;

    private boolean isPickupDropoff=false,isDelivery=false,isDriver=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PrefUtils.setRunningRide(null,this);

        ticket= PrefUtils.getTicketInfo(FairActivity.this);
        isPickupDropoff=true;
        tvCustomerNameVal= (TextView) findViewById(R.id.tvCustomerNameVal);
        tvCustomerNoVal= (TextView) findViewById(R.id.tvCustomerNoVal);
        tvRideTimeVal= (TextView) findViewById(R.id.tvRideTimeVal);
        tvDisVal= (TextView) findViewById(R.id.tvDisVal);
        tvWaitValue= (TextView) findViewById(R.id.tvWaitValue);
        tvFairValue= (TextView) findViewById(R.id.tvFairValue);


        df= new DecimalFormat("0.00");


        tvCustomerNameVal.setText(ticket.getUserName());
        tvCustomerNoVal.setText(ticket.getMobileNumber());
        tvRideTimeVal.setText(ticket.getDurationTime()+"");
        if(ticket.getWaitTime()>0){
            tvWaitValue.setText(ticket.getWaitTime()+"");
        } else {
            tvWaitValue.setText(0+"");
        }

        tvDisVal.setText(df.format(ticket.getDistance()));

        if(isPickupDropoff){
        if(ticket.getDistance()>2){
            double remainingDistance=ticket.getDistance()-2;
            totalAmount=(int)remainingDistance*5;
            Log.e("remaining distance",remainingDistance+"");
            Log.e("remaining distance total",totalAmount+"");
            totalAmount=totalAmount+15;
            Log.e("total",totalAmount+"");
        } else {
            totalAmount=15;
            Log.e("total",totalAmount+"");
        }

        totalAmount=totalAmount+(((double)ticket.getDurationTime()/(double)60)*(double)0.75);
            Log.e("total minute",totalAmount+"");
            if(ticket.getWaitTime()>0) {

                totalAmount = totalAmount + (((double)ticket.getWaitTime() / (double)60) * (double)2);
                Log.e("total wait time", totalAmount + "");
            }
    tvFairValue.setText("Total Rs." + df.format(totalAmount));
            Log.e("fianl total", totalAmount + "");
}
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_fair;
    }

    @Override
    protected String getToolbarTitle() {
        return "Fair Calculator";
    }

    @Override
    protected boolean isToolbarWithBack() {
        return true;
    }

    @Override
    protected int getToolbarColor() {
        return R.color.primaryColor;
    }


}
