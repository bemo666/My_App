package com.ikea.myapp;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ikea.myapp.data.remote.FirebaseManager;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if (FirebaseManager.loggedIn())
            FirebaseDatabase.getInstance().getReference("UserData/" +
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).keepSynced(true);

        BroadcastReceiver receiver = new NetworkChangeReceiver();

        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }



}