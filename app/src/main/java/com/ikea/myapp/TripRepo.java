package com.ikea.myapp;

import android.appwidget.AppWidgetHost;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.amadeus.Amadeus;
import com.amadeus.resources.Location;
import com.ikea.myapp.utils.AppExecutors;

public class TripRepo {

    private AmadeusApi amadeusApi;
    private AppExecutors appExecutors;


    public TripRepo(Context context) {
        amadeusApi = new AmadeusApi(context);
        appExecutors = AppExecutors.getInstance();
    }

    public LiveData<Location[]> getLocations(){
        return amadeusApi.getLocations("PAR");
    }
}
