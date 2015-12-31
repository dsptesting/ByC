package com.nap.bycab.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nap.bycab.R;
import com.nap.bycab.models.NotificationList;
import com.nap.bycab.util.PrefUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SplashActivity extends AppCompatActivity {


    private GoogleCloudMessaging gcm;
    private String regid;
    private String PROJECT_NUMBER = "414298641591";
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        try {


        } catch (Exception e){
            e.printStackTrace();
        }

        initUi();
        try {
            if (PrefUtils.getCurrentDriver(SplashActivity.this) != null) {

                findViewById(R.id.btnLogin).setVisibility(View.GONE);
                countDownTimer=  new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {

                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }.start();
            } else {
                findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
                checkGCMStatus();
            }
        } catch (Exception e){
            e.printStackTrace();
            checkGCMStatus();
        }


    }

    private void initUi() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primaryColorDark));

        }

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);

            }
        });


    }

    public void checkGCMStatus() {

        try {
            if (PrefUtils.getNotificationId(SplashActivity.this).length() > 7) {
                /*new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }.start();*/
            } else {
                if (isInternetAvailable()) {
                    getRegId();
                } else {
                    coomonAlertDialog("No Internet...", "Please Check your Internet Connection and Try Again",false);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (isInternetAvailable()) {
                getRegId();
            } else {
                coomonAlertDialog("No Internet...", "Please Check your Internet Connection and Try Again", false);

            }
        }

    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public void getRegId() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    Log.e("GCM ID :", regid);

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.e("error", ex.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (regid == null || regid == "") {
                    coomonAlertDialog("Error...","Unable to find Device ID, try again!",true);
                } else {
                    countDownTimer=  new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {

                            try {
                                PrefUtils.setNotificationId(regid, SplashActivity.this);
                                if (PrefUtils.getNotificationId(SplashActivity.this).length() > 7) {
//                                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                                    startActivity(i);
//                                    finish();
                                } else {
                                    checkGCMStatus();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                checkGCMStatus();
                            }

                        }
                    }.start();
                }
            }
        }.execute();
    } // end of getRegId

    private void coomonAlertDialog(String title, String message, final boolean isForGCM) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle(title);
        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (isForGCM) {
                    getRegId();
                    dialog.dismiss();
                } else {
                    checkGCMStatus();
                }

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                   SplashActivity.this.finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (NullPointerException ne){
            ne.printStackTrace();
        }
    }
}
