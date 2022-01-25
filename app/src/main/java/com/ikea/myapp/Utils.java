package com.ikea.myapp;


import android.content.Context;
import android.net.ConnectivityManager;

public class Utils {
    final static String textWatcher_name = "name";
    final static String textWatcher_email = "email";
    final static String textWatcher_password = "password";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
