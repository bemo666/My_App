package com.ikea.myapp;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ikea.myapp.data.remote.FirebaseManager;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks {
    public static Activity appactivity;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (FirebaseManager.loggedIn())
            FirebaseDatabase.getInstance().getReference("UserData/" +
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).keepSynced(true);

        BroadcastReceiver receiver = new NetworkChangeReceiver();

        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        registerActivityLifecycleCallbacks(this);

    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        appactivity = activity; //here we get the activity
        Intent i = new Intent(this, NetworkChangeReceiver.class);
        sendBroadcast(i); //here we are calling the broadcast receiver to check connection state.
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

}