package com.ikea.myapp.Managers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ikea.myapp.CustomProgressDialog;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.LoginActivity;
import com.ikea.myapp.UI.MainActivity;
import com.ikea.myapp.UserData;
import com.ikea.myapp.utils.Utils;

import java.util.Objects;

public class FirebaseRequestManager {

    private static CustomProgressDialog progressDialog;
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference("UserData");
    private final DatabaseReference userdata;



    public FirebaseRequestManager() {
        userdata = UsersReference.child(firebaseAuth.getUid());
    }


    public static boolean loggedIn() {

        return firebaseAuth.getCurrentUser() != null;
    }

    public static String getUid() {
        return firebaseAuth.getCurrentUser().getUid();
    }

    public static String getEmail() {
        return firebaseAuth.getCurrentUser().getEmail();
    }


    public static void SignIn(String email, String password, Context context, Activity activity, RelativeLayout relativeLayout) {
        progressDialog = new CustomProgressDialog(context, "Signing In", activity);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(task -> {
                    progressDialog.hide();
                    if (task.isSuccessful()) {
                        Toast.makeText(context, R.string.login_signed_in, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (!Utils.isNetworkConnected(context)) {
                        Toast.makeText(context, R.string.ui_no_internet, Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                            task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, R.string.login_incorrect_details, Toast.LENGTH_SHORT).show();
                    } else {
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
                        UserData data = new UserData(name, email);
                        UsersReference.child(getUid()).setValue(data).
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

    public DatabaseReference getTripsRef(){ return  userdata.child("Trips");  }
    public DatabaseReference getNameRef(){ return  userdata.child("firstName"); }



}
