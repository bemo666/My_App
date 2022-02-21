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

    public List<MyTrip> getTrips() {
        return trips;
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
