package com.ikea.myapp;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ikea.myapp.data.remote.FirebaseManager;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        if(FirebaseManager.loggedIn())
            FirebaseDatabase.getInstance().getReference("UserData/" +
                    FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Trips").keepSynced(true);

    }
}