package com.ikea.myapp;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TripRequest {

    private LiveData<TripList> upcoming;
    private LiveData<TripList> past;

    public TripRequest(LiveData<TripList> upcoming, LiveData<TripList> past) {
        this.upcoming = upcoming;
        this.past = past;
    }

    public LiveData<TripList> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(LiveData<TripList> upcoming) {
        this.upcoming = upcoming;
    }

    public LiveData<TripList> getPast() {
        return past;
    }

    public void setPast(LiveData<TripList> past) {
        this.past = past;
    }
}
