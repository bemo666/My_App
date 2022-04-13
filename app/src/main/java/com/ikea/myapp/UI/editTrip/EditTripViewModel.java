package com.ikea.myapp.UI.editTrip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ikea.myapp.data.remote.FirebaseManager;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.data.TripRepo;

public class EditTripViewModel extends AndroidViewModel {

    private final TripRepo tripRepo;
    private final LiveData<MyTrip> trip;

    public EditTripViewModel(@NonNull Application application, String id) {
        super(application);
        tripRepo = new TripRepo(application);
        if (FirebaseManager.loggedIn()){
            trip = tripRepo.getRemoteTrip(id);
        } else{
            trip =  tripRepo.getLocalTrip(id);
        }
    }


    public LiveData<MyTrip> getTrip() {
        return trip;
    }

    public void updateTrip(MyTrip trip) {
        tripRepo.updateTrip(trip);
    }

    public void deleteTrip(MyTrip trip) {
        tripRepo.deleteTrip(trip);
    }

    public void setLocalImage(String id, String image){
        tripRepo.setLocalImage(id, image);
    }
}
