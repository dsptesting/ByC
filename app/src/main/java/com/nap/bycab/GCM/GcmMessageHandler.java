package com.nap.bycab.GCM;

/**
 * Created by nirav on 28-10-2014.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nap.bycab.R;
import com.nap.bycab.activity.MainActivity;
import com.nap.bycab.activity.SplashActivity;
import com.nap.bycab.models.NotificationList;
import com.nap.bycab.util.AppConstants;
import com.nap.bycab.util.PrefUtils;

import java.util.Random;


public class GcmMessageHandler extends IntentService {

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    //974763131360
    public GcmMessageHandler() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification(extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification(extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*for (int i=0; i<5; i++) {
                    Log.i("", "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i("", "Completed work @ " + SystemClock.elapsedRealtime());*/
                // Post notification of received message.
                Log.e("Received message: ", "Received: " + extras.toString());
                sendNotification(extras);
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(Bundle response) {

        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        if(response==null){
            return;
        }

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        // TODO is this right init place for this
        if(PrefUtils.getUpcomingNotificationIdList(this) == null) PrefUtils.setUpcomingNotificationIdList(new NotificationList(), this);
        if(PrefUtils.getCurrentNotificationIdList(this) == null) PrefUtils.setCurrentNotificationIdList(new NotificationList(), this);

        boolean IsCurrentRide = Boolean.parseBoolean(response.getString("IsCurrentRide"));

        //TODO...remove this...
        IsCurrentRide = true;

        if(Boolean.parseBoolean(response.getString("IsOrderCompleted"))){

            // Order completed... handler it.
            Log.v(AppConstants.DEBUG_TAG, "Order completed Noti Id: " + m);
        }
        else if(!IsCurrentRide){

            // upcoming ride..
            Log.v(AppConstants.DEBUG_TAG, "upcoming ride Noti Id: " + m);

            NotificationList notificationList = PrefUtils.getUpcomingNotificationIdList(this);
            notificationList.getIdList().add(new Integer(m));
           // Log.v(AppConstants.DEBUG_TAG, "nnn u notificationList: " + notificationList.getIdList());
            PrefUtils.setUpcomingNotificationIdList(notificationList,this);
        }
        else if(IsCurrentRide){

            // current ride...
            Log.v(AppConstants.DEBUG_TAG, "current ride Noti Id: " + m);

            NotificationList notificationList = PrefUtils.getCurrentNotificationIdList(this);
            notificationList.getIdList().add(new Integer(m));
            //Log.v(AppConstants.DEBUG_TAG, "nnn c notificationList: " + notificationList.getIdList());
            PrefUtils.setCurrentNotificationIdList(notificationList, this);
        }

        /*Log.v(AppConstants.DEBUG_TAG, "PrefUtils.getUpcomingNotificationIdList(GcmMessageHandler.this) " + PrefUtils.getUpcomingNotificationIdList(this));
        Log.v(AppConstants.DEBUG_TAG, "PrefUtils.getCurrentNotificationIdList(GcmMessageHandler.this) " + PrefUtils.getCurrentNotificationIdList(this));*/


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);   // To open only one activity on launch.
        intent.putExtra("IsCurrentRide", IsCurrentRide);
        intent.putExtra("isNotificationLocation", false);
        if(IsCurrentRide){
            intent.putExtra("notificationType", AppConstants.NOTIFICATION_TYPE_CURRENT_RIDE);
        }
        else{
            intent.putExtra("notificationType", AppConstants.NOTIFICATION_TYPE_UPCOMING_RIDE);
        }
        intent.putExtra("notification_id", m);
        intent.setAction(""+m);
        PendingIntent contentIntent = PendingIntent.getActivity(this, m, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(GcmMessageHandler.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(response.getString("contentTitle").toString())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(response.getString("message").toString()))
                        .setContentText(response.getString("message").toString()+", "+m)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setVibrate(new long[] { 1000 })
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setOngoing(true);

        mBuilder.setContentIntent(contentIntent);

        // TODO... MIMP.. REMOVE THIS COMMENT TO ENABLE NOTIFICATION..
        // mNotificationManager.notify(m, mBuilder.build());
    }
}