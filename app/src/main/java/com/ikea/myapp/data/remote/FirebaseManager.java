package com.ikea.myapp.data.remote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ikea.myapp.CustomProgressDialog;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.LoginActivity;
import com.ikea.myapp.UI.main.MainActivity;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.utils.Utils;

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

    public String getEmail() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
    }

    public Task<Void> setFirstName(String name) {
        return userdata.child("firstName").setValue(name);
    }

    public Task<Void> setEmail(String email) {
        return firebaseAuth.getCurrentUser().updateEmail(email);
    }

    public static Task<Void> DeleteAccount() {
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return user.delete();
        } else {
            return null;
        }
    }

    public static Task<AuthResult> SignIn(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }


    public static Task<AuthResult> SignUp(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password);
    }

    public static Task<Void> ForgotPassword(String resetEmail) {
        return firebaseAuth.sendPasswordResetEmail(resetEmail);
    }

    public static void SignOut() {
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

}