package com.ikea.myapp.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ikea.myapp.Managers.FirebaseRequestManager;

public class NewTripActivityViewModel extends AndroidViewModel {

    FirebaseRequestManager firebaseRequestManager;
    MutableLiveData<String> name, toast;

    public NewTripActivityViewModel(@NonNull Application application) {
        super(application);
        name = new MutableLiveData<>();
        toast = new MutableLiveData<>();
        if (FirebaseRequestManager.loggedIn()) {
            firebaseRequestManager = new FirebaseRequestManager();
            fetchName();
        } else
            name.setValue(null);
    }

    private void fetchName() {
        firebaseRequestManager.getNameRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setValue(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast.setValue(error.getMessage());
            }
        });
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public MutableLiveData<String> getToast() {
        return toast;
    }
}
