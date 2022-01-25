package com.ikea.myapp.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amadeus.Amadeus;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ikea.myapp.AmadeusApi;
import com.ikea.myapp.EditTripActivity;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UserData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NewTripActivity extends AppCompatActivity {
    //Declaring Variables
    TextInputEditText inputDestination, inputOrigin;
    TextInputLayout layoutInputDestination, layoutInputOrigin;
    TextView welcomeText, inspirationText;
    LinearLayout linearLayout;
    RelativeLayout layoutOrigin;
    MaterialButton getLocation;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    DatabaseReference userRef;
    ActionBar actionBar;
    String origin = "";
    String destination = "";
    LatLng originLatLng;
    LatLng destinationLatLng;
    boolean isDestination;
    Snackbar snackbar;

    //Location Permission
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    ProgressDialog progressDialog;
    Amadeus amadeus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        //Disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        //Linking xml objects to java variables
        inputDestination = findViewById(R.id.inputDestination);
        layoutInputDestination = findViewById(R.id.layoutInputDestination);
        inputOrigin = findViewById(R.id.inputOrigin);
        layoutInputOrigin = findViewById(R.id.layoutInputOrigin);
        welcomeText = findViewById(R.id.welcomeText);
        getLocation = findViewById(R.id.getLocation);
        inspirationText = findViewById(R.id.inspirationText);
        layoutOrigin = findViewById(R.id.origin);
        linearLayout = findViewById(R.id.linearLayout);

        //Initializing the progress dialog
        progressDialog = new ProgressDialog(this);

        //Initialize the places api
        if (!Places.isInitialized())
            Places.initialize(getApplicationContext(), getResources().getString(R.string.googles_api_key));

        //Initialize Amadeus
        amadeus = AmadeusApi.getAmadeus(this);

        AmadeusApi.RecommendedLocations recommendedLocations = new AmadeusApi.RecommendedLocations(amadeus);
        recommendedLocations.execute();


        //Set welcome text
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userRef = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid());
            welcomeText.setText("Hi " + firebaseUser.getDisplayName() + ",");
            updateName();
        }

        //Actionbar setup
        setTitle("");
        actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
        inspirationText.setText("Enter your departure city to find your best and cheapest flight destinations!");

        initializeLocationStuff();

    }

    @SuppressLint("MissingPermission")
    private void initializeLocationStuff() {
        inputDestination.setFocusable(false);
        inputOrigin.setFocusable(false);
        inputDestination.setOnClickListener(view -> {
            //Initialize place field list
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            //Create intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                    , fieldList).build(NewTripActivity.this);
            //Start activity result
            isDestination = true;
            startActivityForResult(intent, 100);
        });
        inputOrigin.setOnClickListener(view -> {
            //Initialize place field list
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            //Create intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                    , fieldList).build(NewTripActivity.this);
            //Start activity result
            isDestination = false;
            startActivityForResult(intent, 100);
        });
    }

    //Inflate menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.newTrip) {
            if (verifyInput()) {
                showProgressDialogWithTitle("Creating Trip");
                MyTrip data = new MyTrip(origin, destination, originLatLng, destinationLatLng);
                DatabaseReference pushedTrip = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid()).child("Trips");
                pushedTrip.push().setValue(data).addOnCompleteListener(task -> {
                    hideProgressDialogWithTitle();
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
                        intent.putExtra("pushedTrip", pushedTrip.getKey());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(NewTripActivity.this).toBundle());
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to create new trip, Try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(getApplicationContext(), "Don't forget to pick a destination!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean verifyInput() {
        return originLatLng != null && destinationLatLng != null && !origin.equals("") && !destination.equals("");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //When successful, initialize place
            Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
            //Set address on edittext
            if (isDestination) {
                destination = place.getName();
                inputDestination.setText(place.getName());
                destinationLatLng = place.getLatLng();
                Toast.makeText(getApplicationContext(), "Lat: " + destinationLatLng.toString(), Toast.LENGTH_LONG).show();
            } else {
                origin = place.getName();
                inputOrigin.setText(place.getName());
                originLatLng = place.getLatLng();

            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            //When Error, initiate status
            Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void updateName() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseUser == null) {
                    userRef.removeEventListener(this);
                } else {
                    UserData value = snapshot.getValue(UserData.class);
                    welcomeText.setText("Hi " + value.getFirstName() + ",");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to update name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    private void showProgressDialogWithTitle(String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(substring);
        progressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }




}

