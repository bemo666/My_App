package com.ikea.myapp.UI.editTrip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.data.TripRepo;

import java.util.List;

public class EditTripViewModel extends AndroidViewModel {
    private TripRepo tripRepo;
    private LiveData<List<MyTrip>> myTrips;


    public EditTripViewModel(@NonNull Application application) {
        super(application);
    }
}
