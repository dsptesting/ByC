package com.nap.bycab.activity;

import android.app.ProgressDialog;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.fragment.HomeFragment;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.models.Order;
import com.nap.bycab.models.Ticket;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class FairActivity extends BaseActivity {

    private TextView tvCustomerNameVal, tvCustomerNoVal, tvRideTimeVal, tvDisVal, tvWaitValue, tvFairValue;

    private Ticket ticket;
    private double totalAmount;
    DecimalFormat df;
    private LinearLayout llFair;

    private boolean isPickupDropoff = false, isDelivery = false, isDriver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeFragment.isFromFairActivity = true;
        ticket = PrefUtils.getTicketInfo(FairActivity.this);
        isPickupDropoff = true;
        tvCustomerNameVal = (TextView) findViewById(R.id.tvCustomerNameVal);
        tvCustomerNoVal = (TextView) findViewById(R.id.tvCustomerNoVal);
        tvRideTimeVal = (TextView) findViewById(R.id.tvRideTimeVal);
        tvDisVal = (TextView) findViewById(R.id.tvDisVal);
        tvWaitValue = (TextView) findViewById(R.id.tvWaitValue);
        tvFairValue = (TextView) findViewById(R.id.tvFairValue);
        llFair = (LinearLayout) findViewById(R.id.llFair);

        df = new DecimalFormat("0.00");

        tvCustomerNameVal.setText(ticket.getUserName());
        tvCustomerNoVal.setText(ticket.getMobileNumber());
        tvRideTimeVal.setText(ticket.getDurationTime() + "");
        if (ticket.getWaitTime() > 0) {
            tvWaitValue.setText(ticket.getWaitTime() + "");
        }
        else {
            tvWaitValue.setText(0 + "");
        }

        tvDisVal.setText(df.format(ticket.getDistance()));

        if (isPickupDropoff) {
            if (ticket.getDistance() > 2) {
                double remainingDistance = ticket.getDistance() - 2;
                totalAmount = (int) remainingDistance * 5;
                Log.e("remaining distance", remainingDistance + "");
                Log.e("remaining dis total", totalAmount + "");
                totalAmount = totalAmount + 15;
                Log.e("total", totalAmount + "");
            }
            else {
                totalAmount = 15;
                Log.e("total", totalAmount + "");
            }

            totalAmount = totalAmount + (((double) ticket.getDurationTime() / (double) 60) * (double) 0.75);
            Log.e("total minute", totalAmount + "");
            if (ticket.getWaitTime() > 0) {

                totalAmount = totalAmount + (((double) ticket.getWaitTime() / (double) 60) * (double) 2);
                Log.e("total wait time", totalAmount + "");
            }
            tvFairValue.setText("Total Rs." + df.format(totalAmount));
            Log.e("fianl total", totalAmount + "");


        }
        callCompleteOrderService();
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

    private void callCompleteOrderService() {
        final JSONObject object = new JSONObject();
        try {
            object.put("DriverId", PrefUtils.getCurrentDriver(FairActivity.this).getDriverId() + "");
            object.put("OrderId", "" + PrefUtils.getRunningRide(FairActivity.this).getOrderId() + "");
            object.put("Amount", df.format(totalAmount) + "");
            object.put("JournyTime", PrefUtils.getTicketInfo(FairActivity.this).getDurationTime() + "");
            object.put("KM", PrefUtils.getTicketInfo(FairActivity.this).getDistance() + "");
            object.put("ServiceType", "1");
            object.put("WaitingTime", PrefUtils.getTicketInfo(FairActivity.this).getWaitTime() + "");
            object.put("Weight", "");

            Log.e(AppConstants.DEBUG_TAG, "callCompliteOrderStatus " + object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = new ProgressDialog(FairActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.COMPLETE_ORDER, object) {

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e(AppConstants.DEBUG_TAG, "callDriverStatusService resp " + response);
                CommonResponse commonResponse = new GsonBuilder().create().fromJson(response, CommonResponse.class);

                if (commonResponse.getResponseId().equalsIgnoreCase("0")) {
                    Snackbar snackbar = Snackbar.make(llFair, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                } else {
                    Snackbar snackbar = Snackbar.make(llFair, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PrefUtils.clearRunningRide(FairActivity.this);
        Log.v(AppConstants.DEBUG_TAG, "Cleared Running ride: " + PrefUtils.getRunningRide(FairActivity.this));
    }
}
