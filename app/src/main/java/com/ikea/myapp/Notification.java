package com.ikea.myapp;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ikea.myapp.UI.editTrip.EditTripActivity;


public class Notification extends BroadcastReceiver {
    public static final String CHANNEL_ID = "id";
    public static final String CHANNEL_NAME = "Trip Reminder";
    public static final String CHANNEL_DESCRIPTION = "Reminding you of an Upcoming Trip";
    @Override
    public void onReceive(Context context, Intent intent) {
        String tripName = (String) intent.getExtras().get("tripName");
        String id = (String) intent.getExtras().get("id");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_app_icon_green)
                .setContentTitle("Trip to "+ tripName)
                .setAutoCancel(true)
                .setContentText("Have you finished packing? your trip is coming up very soon! click here to see your trip")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(context, EditTripActivity.class);
        notificationIntent.putExtra("id", id);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        PendingIntent conPendingIntent = stackBuilder.getPendingIntent( 0,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        builder.setContentIntent(conPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build());
    }
}
