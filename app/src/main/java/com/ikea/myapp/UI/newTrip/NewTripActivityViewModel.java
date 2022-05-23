package com.ikea.myapp.UI.newTrip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

public class NewTripActivityViewModel extends AndroidViewModel {

    FirebaseManager firebaseManager;
    TripRepo tripRepo;


    public NewTripActivityViewModel(@NonNull Application application) {
        super(application);
        tripRepo = new TripRepo(application);
        if (FirebaseManager.loggedIn()) {
            firebaseManager = new FirebaseManager();
        }
    }

    public void insertLocalTrip(MyTrip trip) {
        tripRepo.insertLocalTrip(trip);
    }

    public void setImage(String id, String image, int version) {
        tripRepo.setImage(id, image,version);
    }

    public LiveData<String> getName() {
        if (FirebaseManager.loggedIn())
            return tripRepo.getUsername();
        else
            return new MutableLiveData<String>("-1");
    }
}
