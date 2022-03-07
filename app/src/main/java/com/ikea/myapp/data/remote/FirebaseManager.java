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

    private static CustomProgressDialog progressDialog;
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final DatabaseReference userdata;



    public FirebaseManager() {
        if(loggedIn())
            userdata = FirebaseDatabase.getInstance().getReference("UserData").child(Objects.requireNonNull(firebaseAuth.getUid()));
        else userdata = null;
    }


    public static boolean loggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public String getEmail() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
    }
    public static void DeleteAccount(Context context){
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            user.delete().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    //userdata.removeValue()
                    Toast.makeText(context, R.string.profile_account_deleted, Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, MainActivity.class));
                }else{
                    Toast.makeText(context, task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else{
            Toast.makeText(context, R.string.profile_not_signed_in_error, Toast.LENGTH_SHORT).show();
        }
    }


    public static void SignIn(String email, String password, Context context, Activity activity, RelativeLayout relativeLayout) {
        progressDialog = new CustomProgressDialog(context, "Signing In", activity);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
                    progressDialog.hide();
                    new TripRepo(activity.getApplication()).deleteTable();

                    if (task.isSuccessful()) {
                        Toast.makeText(context, R.string.login_signed_in, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (!Utils.isNetworkConnected(context)) {
                        Log.d("tag", task.getException().toString());
                        Toast.makeText(context, R.string.ui_no_internet, Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                            task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.d("tag", task.getException().toString());
                        Toast.makeText(context, R.string.login_incorrect_details, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("tag", task.getException().toString());
                        Snackbar snackbar = Snackbar
                                .make(relativeLayout, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
    }


    public static void SignUp(String name, String email, String password, Context context, Activity activity, RelativeLayout relativeLayout) {
        progressDialog = new CustomProgressDialog(context, "Signing Up", activity);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                (task -> {
                    if (task.isSuccessful()) {
                        new FirebaseManager().setUsername(name).
                                addOnCompleteListener(task1 -> {
                                    progressDialog.hide();
                                    Toast.makeText(context, R.string.login_account_created_successfully, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                });
                    } else {
                        progressDialog.hide();
                        if (!Utils.isNetworkConnected(context)) {
                            Toast.makeText(context, R.string.ui_no_internet, Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle(R.string.login_encountered_problem);
                            alertDialog.setMessage(R.string.login_email_in_use);

                            alertDialog.setPositiveButton("Sign In", (dialog, which) -> {
                                LoginActivity.setTabPos(0);
                            });
                            alertDialog.setNegativeButton("Try again", (dialog, which) -> dialog.cancel());
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                                task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(context, R.string.login_incorrect_email, Toast.LENGTH_SHORT).show();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(relativeLayout, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                });

    }

    public static void ForgotPassword(String resetEmail, Context context, Activity activity, RelativeLayout relativeLayout) {
        progressDialog = new CustomProgressDialog(context, "Sending reset link", activity);
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(resetEmail)
                .addOnCompleteListener(task -> {
                    progressDialog.hide();
                    if (task.isSuccessful()) {
                        Toast.makeText(context, R.string.login_reset_link_sent, Toast.LENGTH_LONG).show();
                        LoginActivity.setTabPos(0);
                    } else if (!Utils.isNetworkConnected(context)) {
                        Toast.makeText(context, R.string.ui_no_internet, Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                            task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, R.string.login_incorrect_email, Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(relativeLayout, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });

    }

    public static void SignOut() {
        firebaseAuth.signOut();
    }

    public Query getTripsRef(){ return  userdata.child("Trips").orderByChild("startStamp");  }

    public void updateTrip(MyTrip trip){
        userdata.child("Trips").child(trip.getId()).setValue(trip);
    }

    public DatabaseReference getNameRef(){ return  userdata.child("firstName"); }

    public Task<Void> setUsername(String name){
        return userdata.child("firstName").setValue(name);
    }

}