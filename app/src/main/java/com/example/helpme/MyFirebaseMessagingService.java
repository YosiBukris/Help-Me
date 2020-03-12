package com.example.helpme;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String title;
    private String body;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.d("remote", "onMessageReceived: " +remoteMessage);
////        String title = "New Call";
////        String body = "check if you can help!";
//
//        Log.d("remote", "displayNotification: " + title + body);
//        NotificationHelper.displayNotification(getApplicationContext(),title,body);
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
