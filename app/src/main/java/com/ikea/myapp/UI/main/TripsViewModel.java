package com.ikea.myapp.UI.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.TripList;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.util.List;

public class TripsViewModel extends AndroidViewModel {

    FirebaseManager firebaseManager;
    TripRepo tripRepo;
    LiveData<TripList> trips;

    public TripsViewModel(@NonNull Application application) {
        super(application);
        trips = new MutableLiveData<TripList>();
        tripRepo = new TripRepo(application);
        if (FirebaseManager.loggedIn()) {
            firebaseManager = new FirebaseManager();
            fetchRemoteTrips();
        } else
            fetchLocalTrips();
    }

    private void fetchLocalTrips() {
        LiveData<List<MyTrip>> list = tripRepo.getLocalTrips();
        trips = Transformations.map(list, input -> new TripList(input));
    }


    private void fetchRemoteTrips() {
        Log.d("tag", "attempting to fetch remote trips");
        trips = tripRepo.getRemoteTrips();
    }

    public LiveData<TripList> getTrips() {
        return trips;
    }


    public MyTrip getTripAt(int i) {
        if (trips.getValue() != null) {
            return trips.getValue().getTrips().get(i);
        } else return null;
    }
}
