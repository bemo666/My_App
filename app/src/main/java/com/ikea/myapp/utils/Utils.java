package com.ikea.myapp.utils;


import android.content.Context;
import android.net.ConnectivityManager;

import com.ikea.myapp.AmadeusApi;

public class Utils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}
