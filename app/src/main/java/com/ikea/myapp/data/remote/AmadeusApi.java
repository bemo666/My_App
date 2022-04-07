package com.ikea.myapp.data.remote;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Location;
import com.google.android.gms.maps.model.LatLng;
import com.ikea.myapp.R;
import com.ikea.myapp.utils.AppExecutors;


public class AmadeusApi {

    private static Amadeus amadeus;

    public AmadeusApi(Context context) {
        if (amadeus == null) {
            amadeus = Amadeus.builder(context.getString(R.string.a_apiKey), context.getString(R.string.a_apiSecret))
                    .build();
        }
    }

    public MutableLiveData<Location[]> getInspirationLocations(String code) {

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
    public MutableLiveData<Location[]> getNearestAirport(LatLng location) {

        MutableLiveData<Location[]> locations = new MutableLiveData<>();
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                locations.postValue(amadeus.referenceData.locations.airports.get(
                        Params.with("latitude", location.latitude).and("longitude", location.longitude)
                ));
                Log.d("tag", "getNearestAirport: success");

            } catch (ResponseException e) {
                e.printStackTrace();
                Log.d("tag", "getNearestAirport: fail");

            }
        });
        return locations;
    }

}
