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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            effect = (VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
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
