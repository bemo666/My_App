package com.ikea.myapp;

import androidx.lifecycle.LiveData;

import com.ikea.myapp.models.MyTrip;

import java.util.List;

public class TripRequest {

    private LiveData<List<MyTrip>> upcoming;
    private LiveData<List<MyTrip>> past;

    public TripRequest(LiveData<List<MyTrip>> upcoming, LiveData<List<MyTrip>> past) {
        this.upcoming = upcoming;
        this.past = past;
    }

    public LiveData<List<MyTrip>> getUpcoming() {
        return upcoming;
    }

    public void setUpcoming(LiveData<List<MyTrip>> upcoming) {
        this.upcoming = upcoming;
    }

    public LiveData<List<MyTrip>> getPast() {
        return past;
    }

    public void setPast(LiveData<List<MyTrip>> past) {
        this.past = past;
    }
}
