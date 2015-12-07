package com.nap.bycab.app;

import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;


public class MyApplication extends Application {


    public static final String TAG = "VolleyPatterns";
    private RequestQueue mRequestQueue;
    private static MyApplication sInstance;
    private static Typeface light;
    private static Typeface normal;
    private static Typeface italic;
    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;
        light = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/light.ttf");
        normal = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/regular.ttf");
        italic = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/italic.ttf");
    }

    public static Typeface getLightFont() {
        return light;
    }

    public static Typeface getNormalFont() {
        return normal;
    }

    public static Typeface getitalicFont() {
        return italic;
    }

    public static synchronized MyApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);

        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void cancelAll(){
        try{
            mRequestQueue.cancelAll(null);
        }catch(Exception e){

        }
    }
}
