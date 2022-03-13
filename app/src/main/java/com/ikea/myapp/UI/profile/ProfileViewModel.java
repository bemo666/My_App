package com.ikea.myapp.UI.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.Task;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;

public class ProfileViewModel extends AndroidViewModel {

    private TripRepo repo;
    private LiveData<String> name;
    private String email;
    private FirebaseManager manager;

    public ProfileViewModel(@NonNull Application application) {
        super(application);

        repo = new TripRepo(application);
        if(FirebaseManager.loggedIn()){
            manager = new FirebaseManager();
            name = repo.getUsername();
            email = repo.getEmail();
        }
    }

    public LiveData<String> getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Task<Void> setName(String name) { return manager.setFirstName(name); }
}
