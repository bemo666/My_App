package com.ikea.myapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Location;


public class AmadeusApi {

    private static Amadeus amadeus;

    public static Amadeus getAmadeus(Context context){
        if(amadeus == null){
            amadeus = Amadeus.builder(context.getString(R.string.amadeus_api_key), context.getString(R.string.amadeus_api_secret))
                    .build();
        }
        return amadeus;
    }
    public static class RecommendedLocations extends AsyncTask<Void, Void, Location[]> {
        private final Amadeus amadeus;
        Location[] locations;

        public RecommendedLocations(Amadeus amadeus) {
            this.amadeus = amadeus;
        }

        @Override
        public Location[] doInBackground(Void... voids) {
            try {
                Location[] tmp = amadeus.referenceData.recommendedLocations.get(Params.with("cityCodes", "PAR"));

                for (Location l : tmp) {
                    Log.d("tag", "name: " + l.getName());
                    Log.d("tag", "Analytics: " + l.getAnalytics());
                    Log.d("tag", "DetailedName: " + l.getDetailedName());
                    Log.d("tag", "IataCode: " + l.getIataCode());
                    Log.d("tag", "SubType: " + l.getSubType());
                    Log.d("tag", "TimezoneOffset: " + l.getTimeZoneOffset());
                    Log.d("tag", "Type: " + l.getType());
                    Log.d("tag", "Distance: " + l.getDistance());
                    Log.d("tag", "Relevance: " + l.getRelevance());
                    Log.d("tag", "GeoCode: " + l.getGeoCode());
                }
                return tmp;

            } catch (ResponseException e) {
                e.printStackTrace();
            }
            return null;
        }

        public Location[] getLocations() {
            return locations;
        }
    }
}
