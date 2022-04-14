package com.ikea.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;



public class Notification extends BroadcastReceiver {
    public static final String CHANNEL_ID = "id";
    public static final String CHANNEL_NAME = "Trip Reminder";
    public static final String CHANNEL_DESCRIPTION = "Reminding you of an Upcoming Trip";
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_icon_green)
                .setContentTitle("Trip to ...")
                .setContentText("Have you finished packing? your trip is in two days! click here for more information")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());
    }
}
