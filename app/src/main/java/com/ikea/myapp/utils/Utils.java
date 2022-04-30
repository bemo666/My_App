package com.ikea.myapp.utils;


import android.content.Context;
import android.net.ConnectivityManager;

import java.io.File;

public class Utils {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String prettyPrint(double d) {
        int i = (int) d;
        return d == i ? String.valueOf(i) : String.valueOf(d);
    }

    public static void clearApplicationData(Context context) {
        File applicationDirectory = context.getFilesDir();
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (fileName.charAt(0) == '-') {
                    new File(applicationDirectory, fileName).delete();
                }
            }
        }
    }
}
