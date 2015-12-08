package com.nap.bycab.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nap.bycab.R;
import com.nap.bycab.util.PrefUtils;

import java.io.IOException;


public class SplashActivity extends AppCompatActivity {


    private GoogleCloudMessaging gcm;
    private String regid;
    private String PROJECT_NUMBER = "414298641591";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        checkGCMStatus();

    }


    public void checkGCMStatus() {

        try {
            if (PrefUtils.getNotificationId(SplashActivity.this).length() > 7) {
                new CountDownTimer(3000, 1000) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }.start();
            } else {
                if (isInternetAvailable()) {
                    getRegId();
                } else {
                    coomonAlertDialog("No Internet...", "Please Check your Internet Connection and Try Again",false);
                }
            }
        } catch (Exception e) {
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
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {

                            try {
                                PrefUtils.setNotificationId(regid, SplashActivity.this);
                                if (PrefUtils.getNotificationId(SplashActivity.this).length() > 7) {
                                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
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

}
