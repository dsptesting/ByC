package com.nap.bycab.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.GsonBuilder;
import com.nap.bycab.R;
import com.nap.bycab.models.LoginResponse;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.PostServiceCall;
import com.nap.bycab.util.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private Spinner spinner1;
    private ArrayList<String> list;
    private int pos;
    private RelativeLayout mRootLayout;
    private EditText tvMob,tvPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected String getToolbarTitle() {
        return "Login";
    }

    @Override
    protected boolean isToolbarWithBack() {
        return true;
    }

    @Override
    protected int getToolbarColor() {
        return android.R.color.transparent;
    }

    private void initUi() {

        list = new ArrayList<>();
        list.add("Select Service");
        list.add("Pickup/Dropoff");
        list.add("Delivery");
        list.add("Driver / Service");
//        list.add("Service");


        spinner1 = (Spinner) findViewById(R.id.spinner1);
        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(LoginActivity.this,list);
        spinner1.setAdapter(customSpinnerAdapter);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvMob= (EditText) findViewById(R.id.tvMob);
                tvPass= (EditText) findViewById(R.id.tvPass);
                mRootLayout= (RelativeLayout) findViewById(R.id.mRootLayout);
                if(spinner1.getSelectedItemPosition()==0){
                    Snackbar snackbar=Snackbar.make(mRootLayout, "Please Select Service Type", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                } else if(tvMob.getText().toString().length() !=10) {
                    Snackbar snackbar=Snackbar.make(mRootLayout, "Please Enter Valid Number", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                } else if(tvPass.getText().toString().trim().length()==0) {
                    Snackbar snackbar=Snackbar.make(mRootLayout, "Please Enter Password", Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();
                }else {

                    callLoginService();

//                    // for temp testing
//                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(i);
//                    finish();
                }


            }
        });
    }

    private void callLoginService() {


        JSONObject object=new JSONObject();
        try {
            object.put("NotificationId",PrefUtils.getNotificationId(LoginActivity.this)+"");
            object.put("ServiceType",spinner1.getSelectedItemPosition()+"");
            object.put("Mobile",tvMob.getText().toString().trim());
            object.put("Password",tvPass.getText().toString().trim());
            Log.e("login request:",object+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog=new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        new PostServiceCall(AppConstants.LOGIN,object){

            @Override
            public void response(String response) {
                progressDialog.dismiss();
                Log.e("login Response: ",response+"");
                LoginResponse loginResponse=new GsonBuilder().create().fromJson(response,LoginResponse.class);

                if(loginResponse.getResponseId().equalsIgnoreCase("0")){
                    Snackbar snackbar=Snackbar.make(mRootLayout, loginResponse.getResponseMessage(), Snackbar.LENGTH_LONG);
                    snackbar.getView().setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    snackbar.show();

                } else {
                    PrefUtils.setCurrentDriver(loginResponse.getDriver(), LoginActivity.this);
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void error(String error) {
                progressDialog.dismiss();
            }
        }.call();
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> asr;

        public CustomSpinnerAdapter(Context context,ArrayList<String> asr) {
            this.asr=asr;
            activity = context;
        }

        public int getCount()
        {
            return asr.size();
        }

        public Object getItem(int i)
        {
            return asr.get(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(LoginActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);

            txt.setText(asr.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(LoginActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(0, 0, -20, -20);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.parseColor("#ffffff"));
            return  txt;
        }
    }


}
