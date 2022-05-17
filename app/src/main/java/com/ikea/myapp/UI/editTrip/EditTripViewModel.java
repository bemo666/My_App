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
    private LiveData<String> image;

    public EditTripViewModel(@NonNull Application application, String id) {
        super(application);
        tripRepo = new TripRepo(application);
        if (FirebaseManager.loggedIn()){
            trip = tripRepo.getRemoteTrip(id);
        } else{
            trip =  tripRepo.getLocalTrip(id);
        }
        image = tripRepo.getImage(id);

    }


    public LiveData<MyTrip> getTrip() {
        return trip;
    }

    public LiveData<String> getImage() {
        return image;
    }

    public void updateTrip(MyTrip trip) {
        tripRepo.updateTrip(trip);
    }

    public void deleteTrip(MyTrip trip) {
        tripRepo.deleteTrip(trip);
    }


    public void setImage(String id, String image, int version) {
        tripRepo.setImage(id, image, version);
    }
}
