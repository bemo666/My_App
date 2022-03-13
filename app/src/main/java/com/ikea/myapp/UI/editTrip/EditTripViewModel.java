package com.ikea.myapp.UI.editTrip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.google.firebase.auth.FirebaseAuth;
import com.ikea.myapp.Expense;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.TripList;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.util.List;

public class EditTripViewModel extends AndroidViewModel {

    FirebaseManager firebaseManager;
    private TripRepo tripRepo;
    LiveData<TripList> trips;


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
    }


    private void fetchTrips() {
        trips = tripRepo.getRemoteTrips();
    }

    public LiveData<TripList> getTrips() {
        return trips;
    }
    public void updateTrip(MyTrip trip){
        if(FirebaseManager.loggedIn())
            tripRepo.updateRemoteTrip(trip);
        else
            tripRepo.updateLocalTrip(trip);
    }




}
