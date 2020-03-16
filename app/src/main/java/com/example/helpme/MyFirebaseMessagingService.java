package com.example.helpme;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String mtitle;
    public static String mbody;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("remote", "onMessageReceived: " +remoteMessage);
//        String title = "New Call";
//        String body = "check if you can help!";

        Log.d("remote", "displayNotification: " + mtitle + mbody);
        NotificationHelper.displayNotification(getApplicationContext(),mtitle,mbody);
    }


    public static void setTitle(String title) {
        mtitle = title;
    }
    public static void setBody(String body) {
        mbody = body;
    }
}
