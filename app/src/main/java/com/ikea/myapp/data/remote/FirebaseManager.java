package com.ikea.myapp.data.remote;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ikea.myapp.MyTrip;

import java.util.Currency;
import java.util.Objects;

public class FirebaseManager {

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final DatabaseReference userdata;

    public FirebaseManager() {
        if (loggedIn())
            userdata = FirebaseDatabase.getInstance().getReference("UserData").child(Objects.requireNonNull(firebaseAuth.getUid()));
        else userdata = null;
    }

    public static boolean loggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public static Long getCreationStamp(){
        FirebaseUserMetadata metadata = firebaseAuth.getCurrentUser().getMetadata();
        return metadata.getCreationTimestamp();
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

    public Query getTripsRef() {
        return userdata.child("Trips").orderByChild("startStamp");
    }

    public void updateTrip(MyTrip trip) {
        userdata.child("Trips").child(trip.getId()).setValue(trip);
    }

    public DatabaseReference getNameRef() {
        return userdata.child("firstName");
    }

    public DatabaseReference getCurrencyDisplayNameRef() {
        return userdata.child("currency/displayName");
    }

}