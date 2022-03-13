package com.ikea.myapp;

import java.util.ArrayList;
import java.util.List;

public class TripList {

    private List<MyTrip> trips;
    private String errorMessage;

    public TripList() {
        trips = new ArrayList<>();
    }

    public TripList(List<MyTrip> trips) {
        this.trips = trips;
    }

    public List<MyTrip> getTrips() { return trips; }

    public MyTrip getTripWithId(String id) {
        for (MyTrip t : trips) {
            if(t.getId().equals(id)){
                return t;
            }
        }
        return null;
    }

    public void setTrips(List<MyTrip> trips) {
        this.trips = trips;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
