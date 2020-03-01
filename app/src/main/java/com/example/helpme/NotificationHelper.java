package com.example.helpme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;

public class NotificationHelper {

    public static void displayNotification(Context context,  String title, String body){
        Intent intent = new Intent(context,StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        Log.d("titleb", "displayNotification: " + title + body);
        NotificationCompat.Builder mBulider = new NotificationCompat.Builder(context,StartActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.helpmeicon).setContentTitle(title).setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(body)
                .setPriority(PRIORITY_DEFAULT);
        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(context);
        mNotificationMgr.notify(1,mBulider.build());
    }
}
