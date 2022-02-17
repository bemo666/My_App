package com.ikea.myapp;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Location;
import com.ikea.myapp.utils.AppExecutors;


public class AmadeusApi {

    private static Amadeus amadeus;

    public AmadeusApi(Context context) {
        if (amadeus == null) {
            amadeus = Amadeus.builder(context.getString(R.string.a_apiKey), context.getString(R.string.a_apiSecret))
                    .build();
        }
    }

    public MutableLiveData<Location[]> getLocations(String code) {

        MutableLiveData<Location[]> locations = new MutableLiveData<>();
        AppExecutors.getInstance().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    locations.postValue(amadeus.referenceData.recommendedLocations.get(Params.with("cityCodes", code)));

                } catch (ResponseException e) {
                    e.printStackTrace();
                }
            }
        });
        return locations;
    }

}
