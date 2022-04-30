package com.ikea.myapp.UI.newTrip;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

public class NewTripActivityViewModel extends AndroidViewModel {

    FirebaseManager firebaseManager;
    MutableLiveData<String> name;
    TripRepo tripRepo;


    public NewTripActivityViewModel(@NonNull Application application) {
        super(application);
        name = new MutableLiveData<>();
        tripRepo = new TripRepo(application);
        if (FirebaseManager.loggedIn()) {
            firebaseManager = new FirebaseManager();
            fetchName();
        } else {
            name.setValue(null);
        }
    }

    private void fetchName() {
        firebaseManager.getNameRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setValue(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void insertLocalTrip(MyTrip trip) {
        tripRepo.insertLocalTrip(trip);
    }

    public void setImage(String id, String image, int version) {
        tripRepo.setImage(id, image,version);
    }


    public MutableLiveData<String> getName() {
        return name;
    }

//    public void setLocalImage(String id, byte[] image){
//        tripRepo.setLocalImage(id, image );
//    }
//
//    public void setRemoteImage(String id, byte[] image){
//        tripRepo.setLocalImage(id, image);
//    }

}
