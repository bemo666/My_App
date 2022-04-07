package com.ikea.myapp.data.remote;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ikea.myapp.models.MyTrip;

import java.util.Objects;

public class FirebaseManager {

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static boolean loggedIn = firebaseAuth.getCurrentUser() != null;
    private final DatabaseReference userdata, tripsRef;
    private final StorageReference storage;

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
            storage = FirebaseStorage.getInstance().getReference("trip_pictures");
        } else {
            userdata = null;
            tripsRef = null;
            storage = null;
        }
    }
    public static boolean loggedIn(){ return loggedIn;}


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

    public DatabaseReference newTrip(){
        if(tripsRef == null)
            return FirebaseDatabase.getInstance().getReference().push();
        return tripsRef.push();
    }

    public Query getTripsRef() {
        return tripsRef.orderByChild("startStamp");
    }

    public DatabaseReference getTripRef(String id){
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

    public void addTripImage(String id, byte[] bytes){ //maybe save by location id so it can be reused by different users, or save by userId + tripId
        storage.child(id + ".jpg").putBytes(bytes).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        tripsRef.child(id).child("image").setValue(uri.toString());
                    }
                });
            }
        });
    }


    public void deleteTrip(MyTrip trip) {
        tripsRef.child(trip.getId()).removeValue();
        storage.child(trip.getId() + ".jpg").delete();
    }
}