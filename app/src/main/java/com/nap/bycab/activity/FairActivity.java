package com.nap.bycab.activity;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.nap.bycab.R;
import com.nap.bycab.models.Ticket;
import com.nap.bycab.util.PrefUtils;

public class FairActivity extends BaseActivity {

    private TextView tvCustomerNameVal,tvCustomerNoVal,tvRideTimeVal,tvDisVal,tvWaitValue,tvFairValue;

    private Ticket ticket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ticket= PrefUtils.getTicketInfo(FairActivity.this);

        tvCustomerNameVal= (TextView) findViewById(R.id.tvCustomerNameVal);
        tvCustomerNoVal= (TextView) findViewById(R.id.tvCustomerNoVal);
        tvRideTimeVal= (TextView) findViewById(R.id.tvRideTimeVal);
        tvDisVal= (TextView) findViewById(R.id.tvDisVal);
        tvWaitValue= (TextView) findViewById(R.id.tvWaitValue);
        tvFairValue= (TextView) findViewById(R.id.tvFairValue);
        Toast.makeText(FairActivity.this, "time " +ticket.getWaitTime()+ " seconds \n wait time " +ticket.getDurationTime()+ " seconds", Toast.LENGTH_LONG).show();

        tvCustomerNameVal.setText(ticket.getUserName());
        tvCustomerNoVal.setText(ticket.getMobileNumber());
        tvRideTimeVal.setText(ticket.getDurationTime());
        tvWaitValue.setText(ticket.getWaitTime());
        tvDisVal.setText(ticket.getDistance());




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
