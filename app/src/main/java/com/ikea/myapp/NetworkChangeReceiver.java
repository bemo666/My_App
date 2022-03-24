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

    private static boolean recentlyDisplayed = false;
    private static ConnectivityManager connMgr;
    private static android.net.NetworkInfo wifi, mobile;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        hasConnection(null);
    }

    public static boolean hasConnection(String str) {
        if (wifi.isConnected() || mobile.isConnected()) {
            Log.d("tag", "Network Connected");
            if (recentlyDisplayed) {
                recentlyDisplayed = false;
                NetworkChangeReceiver.snack("Internet connection has been restored, Enjoy!");
            }
            return true;
        } else {
            Log.d("tag", "Network Disconnected");
            if (str != null) {
                NetworkChangeReceiver.snack(str);
            } else {
                NetworkChangeReceiver.snack("Internet is unavailable, some functions may not work");
            }
            recentlyDisplayed = true;
            return false;
        }
    }

    public static void snack(String message) {
        if (MyApp.appactivity != null) {
            Snackbar.make(MyApp.appactivity.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        }
    }

}