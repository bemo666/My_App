package com.ikea.myapp.UI.editTrip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.ikea.myapp.CustomCurrency;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.TripList;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.util.List;

public class EditTripViewModel extends AndroidViewModel {

    FirebaseManager firebaseManager;
    private final TripRepo tripRepo;
    LiveData<TripList> trips;
    LiveData<CustomCurrency> currency;


    public EditTripViewModel(@NonNull Application application) {
        super(application);

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
        currency = null;
    }


    private void fetchTrips() {
        trips = tripRepo.getRemoteTrips();
    }

    public LiveData<TripList> getTrips() {
        return trips;
    }

    public void updateTrip(MyTrip trip) {
        tripRepo.updateTrip(trip);
    }

    public void deleteTrip(MyTrip trip) {
        tripRepo.deleteTrip(trip);

    }
}
