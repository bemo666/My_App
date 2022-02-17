package com.ikea.myapp.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ikea.myapp.Managers.FirebaseRequestManager;
import com.ikea.myapp.MyTrip;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragmentViewModel extends AndroidViewModel {

    FirebaseRequestManager firebaseRequestManager;
    MutableLiveData<List<MyTrip>> trips;
    MutableLiveData<String> toast;

    public UpcomingFragmentViewModel(@NonNull Application application) {
        super(application);
        trips = new MutableLiveData<>(new ArrayList<>());
        toast = new MutableLiveData<>();
        if (FirebaseRequestManager.loggedIn()) {
            firebaseRequestManager = new FirebaseRequestManager();
            fetchTrips();
        } else
            trips.setValue(new ArrayList<>());
    }

    private void fetchTrips() {
        firebaseRequestManager.getTripsRef().orderByChild("startDate/time").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                List<MyTrip> list = trips.getValue();
                int i;
                if (previousChildName == null) {
                    i = 0;
                } else {
                    for (i = 0; i < list.size(); i++) {
                        if (list.get(i).getId().equals(previousChildName)) {
                            i++;
                            break;
                        }
                    }
                }

                list.add(i, snapshot.getValue(MyTrip.class));
                trips.setValue(list);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                for (MyTrip t : trips.getValue()) {
//                    if(t.getId().equals((snapshot.getValue(MyTrip.class).getId()))){
//                        for (t.get:
//                             ) {
//
//                        }
//                    }
//                }
                toast.setValue("changed");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                List<MyTrip> list = trips.getValue();
                for (MyTrip t : trips.getValue()) {
                    if (t.getId().equals((snapshot.getValue(MyTrip.class).getId()))) {
                        list.remove(t);
                        break;
                    }
                }
                toast.setValue("removed");

                trips.setValue(list);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast.setValue(error.getMessage());
            }
        });
    }

    public LiveData<List<MyTrip>> getTrips() {
        return trips;
    }

    public MutableLiveData<String> getToast() {
        return toast;
    }
}
