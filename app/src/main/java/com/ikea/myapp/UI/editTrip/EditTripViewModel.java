package com.ikea.myapp.UI.editTrip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.ikea.myapp.Expense;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.util.List;

public class EditTripViewModel extends AndroidViewModel {

    private TripRepo tripRepo;

    public EditTripViewModel(@NonNull Application application) {
        super(application);

        tripRepo = new TripRepo(application);
    }

    public void updateTrip(MyTrip trip){
        if(FirebaseManager.loggedIn())
            tripRepo.updateRemoteTrip(trip);
        else
            tripRepo.updateLocalTrip(trip);
    }


}
