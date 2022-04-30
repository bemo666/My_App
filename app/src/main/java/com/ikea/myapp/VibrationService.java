package com.ikea.myapp;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VibrationService extends Service {

    VibrationEffect effect;
    Vibrator vibrator;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        Log.d("tag", "onCreate: created");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            list.add(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK));
            effect = (VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
//            list.add(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK));
        }
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vibrator.cancel();
        vibrator.vibrate(effect);
        return super.onStartCommand(intent, flags, startId);

    }
}
