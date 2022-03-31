package com.ikea.myapp.UI.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class TripsViewModel extends AndroidViewModel {

    FirebaseManager firebaseManager;
    TripRepo tripRepo;
    LiveData<List<MyTrip>> trips;

    public TripsViewModel(@NonNull Application application) {
        super(application);
        trips = new MutableLiveData<>();
        tripRepo = new TripRepo(application);
        if (FirebaseManager.loggedIn()) {
            firebaseManager = new FirebaseManager();
            fetchRemoteTrips();
        } else
            fetchLocalTrips();
    }

    private void fetchLocalTrips() {
        LiveData<List<MyTrip>> list = tripRepo.getLocalTrips();
        trips = Transformations.map(list, ArrayList::new);
    }


    private void fetchRemoteTrips() {
        trips = tripRepo.getRemoteTrips();
    }

    public LiveData<List<MyTrip>> getTrips() {
        return trips;
    }


    public String getTripIdAt(int i) {
        if (trips.getValue() != null) {
            return trips.getValue().get(i).getId();
        } else return null;
    }
}
