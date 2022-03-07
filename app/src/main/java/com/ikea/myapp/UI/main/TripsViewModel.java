package com.ikea.myapp.UI.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.TripList;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.local.TripDao;
import com.ikea.myapp.data.remote.FirebaseManager;
import com.ikea.myapp.utils.AppExecutors;

import java.util.ArrayList;
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
            fetchTrips();
        } else
            fetchLocalTrips();
    }

    private void fetchLocalTrips() {
        LiveData<List<MyTrip>> list = tripRepo.getLocalTrips();

        trips = Transformations.map(list, input -> new TripList(input));
    }


    private void fetchTrips() {
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
