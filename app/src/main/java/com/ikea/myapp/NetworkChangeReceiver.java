package com.ikea.myapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private boolean recentlyDisplayed = false;


    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

//        View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);

        if (wifi.isConnected() || mobile.isConnected()) {
            Log.d("tag", "Network Connected");
            if (recentlyDisplayed) {
                recentlyDisplayed = false;
//                Snackbar.make(rootView, "Internet connection has been restored, Enjoy!", Snackbar.LENGTH_LONG);

            }

        } else {
            Log.d("tag", "Network Disconnected");
//            Snackbar.make(rootView, "Internet is unavailable, some functions may not work", Snackbar.LENGTH_LONG);
            recentlyDisplayed = true;

        }
    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        return serviceManager.isNetworkAvailable();
    }
}