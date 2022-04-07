package com.ikea.myapp.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ikea.myapp.data.local.TripDao;
import com.ikea.myapp.data.local.TripDatabase;
import com.ikea.myapp.data.remote.FirebaseManager;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class TripRepo {

    private final TripDao tripDao;
    private final FirebaseManager firebaseManager;
    private final StorageReference storage;
    private final AppExecutors appExecutors;

    public TripRepo(Application application) {
        firebaseManager = new FirebaseManager();
        tripDao = TripDatabase.getDatabase(application).tripDao();
        appExecutors = AppExecutors.getInstance();
        storage = FirebaseStorage.getInstance().getReference().child("pictures");
    }

    public void insertLocalTrip(MyTrip trip) {
        appExecutors.diskIO().execute(() -> tripDao.insert(trip));
    }

    public LiveData<List<MyTrip>> getLocalTrips() {
        return tripDao.getTrips();
    }

    public LiveData<MyTrip> getLocalTrip(String id) {
        return tripDao.getTrip(id);
    }

    public void updateTrip(MyTrip trip) {
        if (FirebaseManager.loggedIn()) {
            firebaseManager.updateTrip(trip);
        } else {
            appExecutors.diskIO().execute(() -> tripDao.updateTrip(trip));
        }
    }


    public LiveData<String> getUsername() {
        MutableLiveData<String> name = new MutableLiveData<>();

        firebaseManager.getNameRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setValue(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return name;
    }

    public String getEmail() {
        return firebaseManager.getEmail();
    }

    public void deleteTrip(MyTrip trip) {
        if (FirebaseManager.loggedIn()) {
            firebaseManager.deleteTrip(trip);
        } else {
            appExecutors.diskIO().execute(() -> tripDao.deleteTrip(trip));
        }
    }

    public void deleteTable() {
        appExecutors.diskIO().execute(tripDao::deleteTable);
    }

    public MutableLiveData<List<MyTrip>> getRemoteTrips() {
        MutableLiveData<List<MyTrip>> trips = new MutableLiveData<>();
        trips.setValue(null);
        firebaseManager.getTripsRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                List<MyTrip> list = trips.getValue();
                if (list == null) list = new ArrayList<MyTrip>();
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
                List<MyTrip> list = trips.getValue();
                if (list == null) list = new ArrayList<MyTrip>();
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

                list.set(i, snapshot.getValue(MyTrip.class));
                trips.setValue(list);
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

                trips.setValue(list);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                List<MyTrip> list = trips.getValue();
                if (list == null) list = new ArrayList<MyTrip>();
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

                list.set(i, snapshot.getValue(MyTrip.class));
                trips.setValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                List<MyTrip> list = trips.getValue();
                if (list != null) {
                    trips.setValue(list);
                }
            }
        });

        return trips;
    }

    public LiveData<MyTrip> getRemoteTrip(String id) {
        MutableLiveData<MyTrip> trip = new MutableLiveData<>();
        firebaseManager.getTripRef(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trip.setValue(snapshot.getValue(MyTrip.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return trip;
    }

//    public void setLocalImage(String id, byte[] image){
//        appExecutors.diskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                tripDao.setImage(id, image);
//            }
//        });
//    }
//
//    public void setRemoteImage(String id, byte[] image){
//        ByteArrayInputStream bs = new ByteArrayInputStream(image);
//        storage.child(id + ".jpg").putStream(bs);
//    }
}
