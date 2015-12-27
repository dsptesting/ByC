package com.nap.bycab.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.models.CommonResponse;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileActivity extends BaseActivity {

    private Dialog alertDialog;
    private LinearLayout llProfile;
    private ProgressWheel pageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initUi();
    }

    private void initData() {

        llProfile = (LinearLayout) findViewById(R.id.llProfile);
    }

    private void initUi() {

        findViewById(R.id.ivChangePass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChangePassDialog(ProfileActivity.this);
            }
        });
    }

    private void cancelThisDialog() {

        if(alertDialog != null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    protected void showChangePassDialog(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(context);

        final View dialogView = inflater.inflate(R.layout.change_pass_dialog, null);

        builder.setCancelable(true);

        builder.setView(dialogView);

        LinearLayout llCancel= (LinearLayout) dialogView.findViewById(R.id.llCancel);
        llCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                cancelThisDialog();
            }
        });

        final EditText tvReNewPass = (EditText)dialogView.findViewById(R.id.tvReNewPass);

        pageLoader = (ProgressWheel) dialogView.findViewById(R.id.pageLoader);

        LinearLayout llChange = (LinearLayout) dialogView.findViewById(R.id.llChange);
        llChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(validateInputs(dialogView)){

                    if (tvReNewPass != null) {
                        InputMethodManager inputManager = (InputMethodManager) ProfileActivity.this.getSystemService(INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(tvReNewPass.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }

                    cancelThisDialog();

                    callChangePassService(dialogView);


                }
            }
        });

        alertDialog = builder.create();
        alertDialog.show();

    }

    private void callChangePassService(View dialogView) {


            JSONObject object = new JSONObject();
            try {
                object.put("DriverId", PrefUtils.getCurrentDriver(this).getDriverId().toString() + "");
                object.put("NewPass", ((EditText)dialogView.findViewById(R.id.tvNewPass)).getText().toString() + "");
                object.put("OldPass", ((EditText)dialogView.findViewById(R.id.tvOldPass)).getText().toString() + "");
                Log.e(AppConstants.DEBUG_TAG, "callChangePassService request: " + object.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            new PostServiceCall(AppConstants.CHANGE_PASS, object) {

                @Override
                public void response(String response) {
                    progressDialog.dismiss();
                    Log.e(AppConstants.DEBUG_TAG, "callChangePassService Response: " + response);
                    CommonResponse commonResponse = new GsonBuilder().create().fromJson(response, CommonResponse.class);

                    if (commonResponse.getResponseId().equalsIgnoreCase("0")) {
                        Snackbar snackbar = Snackbar.make(llProfile, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                        snackbar.show();

                    }
                    else {

                        Snackbar snackbar = Snackbar.make(llProfile, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                        snackbar.show();
                        // PrefUtils.setCurrentDriver(logi`nResponse.getDriver(),getActivity());

                        /*alUpcomingRides.clear();
                        alUpcomingRides.addAll(upcomingRideResponse.getAlUpcomingRides());
                        myAdapter.notifyDataSetChanged();*/
                    }
                }

                @Override
                public void error(String error) {
                    progressDialog.dismiss();
                }
            }.call();


    }

    private boolean validateInputs(View dialogView) {

        EditText tvOldPass = (EditText) dialogView.findViewById(R.id.tvOldPass);
        if(tvOldPass.getText().toString().trim().length() == 0){
            dialogView.findViewById(R.id.tvError).setVisibility(View.VISIBLE);
            ((TextView)dialogView.findViewById(R.id.tvError)).setText("Old password is blank!");
            return false;
        }
/*
        if(!tvOldPass.getText().toString().equalsIgnoreCase(""+PrefUtils.getCurrentDriver(ProfileActivity.this).getPassword())){
            dialogView.findViewById(R.id.tvError).setVisibility(View.VISIBLE);
            ((TextView)dialogView.findViewById(R.id.tvError)).setText("Old password is not matched!");
            return false;
        }*/

        EditText tvNewPass = (EditText) dialogView.findViewById(R.id.tvNewPass);
        if(tvNewPass.getText().toString().trim().length() == 0){
            dialogView.findViewById(R.id.tvError).setVisibility(View.VISIBLE);
            ((TextView)dialogView.findViewById(R.id.tvError)).setText("New password is blank!");
            return false;
        }

        EditText tvReNewPass = (EditText) dialogView.findViewById(R.id.tvReNewPass);
        if(tvReNewPass.getText().toString().trim().length() == 0){
            dialogView.findViewById(R.id.tvError).setVisibility(View.VISIBLE);
            ((TextView)dialogView.findViewById(R.id.tvError)).setText("New password is not matched!");
            return false;
        }

        dialogView.findViewById(R.id.tvError).setVisibility(View.GONE);
        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_profile;
    }

    @Override
    protected String getToolbarTitle() {
        return "Profile";
    }

    @Override
    protected boolean isToolbarWithBack() {
        return true;
    }

    @Override
    protected int getToolbarColor() {
        return R.color.primaryColor;
    }

    private void callUpdateProfileService() {

        if(validateProfileInputs()){

            JSONObject object = new JSONObject();
            try {
                object.put("MobileNo", ((EditText)findViewById(R.id.tvMobileVal)).getText().toString() + "");
                object.put("AadharNo", ((EditText)findViewById(R.id.tvAdharNoVal)).getText().toString() + "");
                object.put("LicenceNo", ((EditText)findViewById(R.id.tvDriverLicNoVal)).getText().toString() + "");
                object.put("Name", ((EditText)findViewById(R.id.tvNameVal)).getText().toString() + "");
                object.put("VehicalDesc", ((EditText)findViewById(R.id.tvVehicleDesVal)).getText().toString() + "");
                object.put("Id", PrefUtils.getCurrentDriver(ProfileActivity.this).getDriverId() + "");
                object.put("DriverStatus", PrefUtils.getCurrentDriver(ProfileActivity.this).getDriverStatus() + "");
                object.put("GCMID", PrefUtils.getCurrentDriver(ProfileActivity.this).getNotificationId() + "");
                object.put("EmailId", PrefUtils.getCurrentDriver(ProfileActivity.this).getEmailId() + "");
                object.put("Password", PrefUtils.getCurrentDriver(ProfileActivity.this).getPassword() + "");
                Log.e(AppConstants.DEBUG_TAG, "callUpdateProfileService request: " + object.toString());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            new PostServiceCall(AppConstants.UPDATE_PROFILE, object) {

                @Override
                public void response(String response) {
                    progressDialog.dismiss();
                    Log.e(AppConstants.DEBUG_TAG, "callUpdateProfileService Response: " + response);
                    CommonResponse commonResponse = new GsonBuilder().create().fromJson(response, CommonResponse.class);

                    if (commonResponse.getResponseId().equalsIgnoreCase("0")) {
                        Snackbar snackbar = Snackbar.make(llProfile, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                        snackbar.show();

                    }
                    else {

                        Snackbar snackbar = Snackbar.make(llProfile, commonResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                        snackbar.show();
                        // PrefUtils.setCurrentDriver(logi`nResponse.getDriver(),getActivity());

                        /*alUpcomingRides.clear();
                        alUpcomingRides.addAll(upcomingRideResponse.getAlUpcomingRides());
                        myAdapter.notifyDataSetChanged();*/
                    }
                }

                @Override
                public void error(String error) {
                    progressDialog.dismiss();
                }
            }.call();
        }

    }

    private boolean validateProfileInputs() {

        EditText tvNameVal = (EditText) findViewById(R.id.tvNameVal);
        if(tvNameVal.getText().toString().trim().length() == 0){
            Snackbar snackbar = Snackbar.make(llProfile, "Name is blank!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
            snackbar.show();
            return false;
        }

        EditText tvMobileVal = (EditText) findViewById(R.id.tvMobileVal);
        if(tvMobileVal.getText().toString().trim().length() == 0){
            Snackbar snackbar = Snackbar.make(llProfile, "Mobile Number is blank!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
            snackbar.show();
            return false;
        }

        EditText tvDriverLicNoVal = (EditText) findViewById(R.id.tvDriverLicNoVal);
        if(tvDriverLicNoVal.getText().toString().trim().length() == 0){
            Snackbar snackbar = Snackbar.make(llProfile, "Driver Licence Number is blank!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
            snackbar.show();
            return false;
        }

        EditText tvAdharNoVal = (EditText) findViewById(R.id.tvAdharNoVal);
        if(tvAdharNoVal.getText().toString().trim().length() == 0){
            Snackbar snackbar = Snackbar.make(llProfile, "Adhar Number is blank!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
            snackbar.show();
            return false;
        }

        EditText tvVehicleDesVal = (EditText) findViewById(R.id.tvVehicleDesVal);
        if(tvVehicleDesVal.getText().toString().trim().length() == 0){
            Snackbar snackbar = Snackbar.make(llProfile, "Vehicle Description is blank!", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
            snackbar.show();
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {

            callUpdateProfileService();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
