package com.ikea.myapp.data.remote;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.PlanHeader;

import java.io.File;
import java.util.Objects;

public class FirebaseManager {

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static boolean loggedIn = firebaseAuth.getCurrentUser() != null;
    private final DatabaseReference userdata, tripsRef;

    public FirebaseManager() {
        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                loggedIn = firebaseAuth.getCurrentUser() != null;
            }
        });
        if (loggedIn) {
            userdata = FirebaseDatabase.getInstance().getReference("UserData").child(Objects.requireNonNull(firebaseAuth.getUid()));
            tripsRef = userdata.child("Trips");
        } else {
            userdata = null;
            tripsRef = null;
        }
    }

    public static boolean loggedIn() {
        return loggedIn;
    }


    public static Long getCreationStamp() {
        FirebaseUserMetadata metadata = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getMetadata();
        return Objects.requireNonNull(metadata).getCreationTimestamp();
    }

    public String getEmail() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
    }

    public Task<Void> setFirstName(String name) {
        return userdata.child("firstName").setValue(name);
    }

    public Task<Void> DeleteAccount() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            userdata.removeValue();
            return user.delete();
        } else {
            return null;
        }
    }

    public Task<AuthResult> SignIn(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }


    public Task<AuthResult> SignUp(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<Void> ForgotPassword(String resetEmail) {
        return firebaseAuth.sendPasswordResetEmail(resetEmail);
    }

    public void SignOut() {
        firebaseAuth.signOut();
    }

    public DatabaseReference newTrip() {
        if (tripsRef == null)
            return FirebaseDatabase.getInstance().getReference().push();
        return tripsRef.push();
    }

    public Query getTripsRef() {
        return tripsRef.orderByChild("startStamp");
    }

    public DatabaseReference getTripRef(String id) {
        return tripsRef.child(id);
    }


    public void updateTrip(MyTrip trip) {
        tripsRef.child(trip.getId()).setValue(trip);
    }


    public DatabaseReference getNameRef() {
        return userdata.child("firstName");
    }

    public DatabaseReference getCurrencyDisplayNameRef() {
        return userdata.child("currency/displayName");
    }

    public void addTripImage(String id, String url, int version) { //maybe save by location id so it can be reused by different users, or save by userId + tripId
        tripsRef.child(id).child("image").setValue(url);
        tripsRef.child(id).child("imageVersion").setValue(version + 1);

    }


    public void deleteTrip(MyTrip trip) {
        Log.d("tag", "firebase deleted " + trip.getId());
        tripsRef.child(trip.getId()).removeValue();
    }
}