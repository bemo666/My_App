package com.ikea.myapp.data;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.TripList;
import com.ikea.myapp.data.local.TripDao;
import com.ikea.myapp.data.local.TripDatabase;
import com.ikea.myapp.data.remote.FirebaseManager;
import com.ikea.myapp.utils.AppExecutors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TripRepo {

    private final TripDao tripDao;
    private final FirebaseManager firebaseManager;
    private final AppExecutors appExecutors;

    public TripRepo(Application application) {
        firebaseManager = new FirebaseManager();
        tripDao = TripDatabase.getDatabase(application).tripDao();
        appExecutors = AppExecutors.getInstance();
    }

    public void insertTrip(MyTrip trip){
        appExecutors.diskIO().execute(() -> tripDao.insert(trip));
    }

    public LiveData<List<MyTrip>> getLocalTrips(){
        return  tripDao.getTrips();
    }

    public MutableLiveData<TripList> getRemoteTrips(){

        MutableLiveData<TripList> trips = new MutableLiveData<>();
        trips.setValue(null);
        firebaseManager.getTripsRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TripList list = trips.getValue();
                if (list == null) list = new TripList();
                int i;
                if (previousChildName == null) {
                    i = 0;
                } else {
                    for (i = 0; i < list.getTrips().size(); i++) {
                        if (list.getTrips().get(i).getId().equals(previousChildName)) {
                            i++;
                            break;
                        }
                    }
                }

                list.getTrips().add(i, snapshot.getValue(MyTrip.class));
                trips.setValue(list);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TripList list = trips.getValue();
                if (list == null) list = new TripList();
                int i;
                if (previousChildName == null) {
                    i = 0;
                } else {
                    for (i = 0; i < list.getTrips().size(); i++) {
                        if (list.getTrips().get(i).getId().equals(previousChildName)) {
                            i++;
                            break;
                        }
                    }
                }

                list.getTrips().set(i, snapshot.getValue(MyTrip.class));
                trips.setValue(list);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                TripList list = trips.getValue();
                for (MyTrip t : trips.getValue().getTrips()) {
                    if (t.getId().equals((snapshot.getValue(MyTrip.class).getId()))) {
                        list.getTrips().remove(t);
                        break;
                    }
                }

                trips.setValue(list);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TripList list = trips.getValue();
                if (list == null) list = new TripList();
                int i;
                if (previousChildName == null) {
                    i = 0;
                } else {
                    for (i = 0; i < list.getTrips().size(); i++) {
                        if (list.getTrips().get(i).getId().equals(previousChildName)) {
                            i++;
                            break;
                        }
                    }
                }

                list.getTrips().set(i, snapshot.getValue(MyTrip.class));
                trips.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                TripList list = trips.getValue();
                if(list != null){
                    list.setErrorMessage(error.getMessage());
                    trips.setValue(list);
                }
            }
        });


        return trips;
    }


    public void deleteTable(){
        appExecutors.diskIO().execute(tripDao::deleteTable);
    }
}
