package com.ikea.myapp.UI.newTrip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ikea.myapp.CustomProgressDialog;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.data.remote.FirebaseManager;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewTripActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    private TextInputEditText inputDestination, inputOrigin, inputDates;
    private TextInputLayout layoutInputDestination, layoutInputOrigin, layoutInputDates;
    private Toolbar toolbar;
    private TextView welcomeText, inspirationText;
    private LinearLayout linearLayout;
    private MaterialButton getLocation;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private String origin = "", placeId = "", destination = "";
    private LatLng originLatLng, destinationLatLng;
    private String startDate, endDate, startStamp, endStamp;
    private MaterialButton createButton;
    private boolean isDestination;
    private CustomProgressDialog progressDialog;
    private final Handler handler = new Handler();
    private NewTripActivityViewModel viewmodel;
    private boolean first;

    //Location Permission
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        setTitle("");

        //Linking xml objects to java variables
        inputDestination = findViewById(R.id.inputDestination);
        layoutInputDestination = findViewById(R.id.layoutInputDestination);
        inputOrigin = findViewById(R.id.inputOrigin);
        layoutInputOrigin = findViewById(R.id.layoutInputOrigin);
        welcomeText = findViewById(R.id.welcomeText);
        inspirationText = findViewById(R.id.inspirationText);
        linearLayout = findViewById(R.id.linearLayout);
        layoutInputDates = findViewById(R.id.layoutInputDates);
        inputDates = findViewById(R.id.inputDates);
        createButton = findViewById(R.id.create_trip);
        toolbar = findViewById(R.id.newTripToolbar);

        //Set welcome text
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (FirebaseManager.loggedIn()) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userRef = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid());
        }
        viewmodel = new ViewModelProvider(this).get(NewTripActivityViewModel.class);
        first = true;


        viewmodel.getName().observe(this, name -> {
            if (name != null) {
                welcomeText.setText(getString(R.string.newtrip_hey) + name +
                        getString(R.string.ui_comma));
            }
        });

        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.ThemeOverlay_App_DatePicker).build();

        inputDates.setOnClickListener(view -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(selection -> {
            inputDates.setText(datePicker.getHeaderText());
            final Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selection.first), new Date((Long) ((Pair) selection).second));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
            startDate = dateFormat.format(rangeDate.first);
            endDate = dateFormat.format(rangeDate.second);
            startStamp = String.valueOf(rangeDate.first.getTime());
            endStamp = String.valueOf(rangeDate.second.getTime());

        });
        createButton.setOnClickListener(this);
        initializeAPIs();


        //Actionbar setup
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        initializeLocationStuff();

    }

    private void initializeAPIs() {
        //Initialize the places api
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.g_apiKey));
        }
//            AmadeusRequests tripRepo = new AmadeusRequests();
//            tripRepo.getLocations().observe(NewTripActivity.this, locations -> {
//                if (locations != null) {
//                    for (Location l : locations) {
//                        Log.d("tag", "name: " + l.getName());
//                        Log.d("tag", "Analytics: " + l.getAnalytics());
//                        Log.d("tag", "DetailedName: " + l.getDetailedName());
//                        Log.d("tag", "IataCode: " + l.getIataCode());
//                        Log.d("tag", "SubType: " + l.getSubType());
//                        Log.d("tag", "TimezoneOffset: " + l.getTimeZoneOffset());
//                        Log.d("tag", "Type: " + l.getType());
//                        Log.d("tag", "Distance: " + l.getDistance());
//                        Log.d("tag", "Relevance: " + l.getRelevance());
//                        Log.d("tag", "GeoCode: " + l.getGeoCode());
//                    }
//                }
//            });
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationStuff() {
        inputDestination.setFocusable(false);
        inputOrigin.setFocusable(false);
        inputDestination.setOnClickListener(view -> {
            initializeAPIs();
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                    , fieldList).build(NewTripActivity.this);
            isDestination = true;
            startActivityForResult(intent, 100);

        });
        inputOrigin.setOnClickListener(view -> {
            initializeAPIs();
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                    , fieldList).build(NewTripActivity.this);
            isDestination = false;
            startActivityForResult(intent, 100);

        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private String verifyInput() {
        if (startDate != null) {
            if (destinationLatLng != null) {
                if (originLatLng != null) {
                    return "orig";
                }
                return "dest";
            }
        }
        return "false";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //When successful, initialize place
            Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
            destination = place.getName();
            inputDestination.setText(place.getName());
            destinationLatLng = place.getLatLng();
            placeId = place.getId();
            if (!isDestination) {
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_trip) {
            {
                String str = verifyInput();
                if (!str.equals("false")) {
                    MyTrip data = null;
                    if (FirebaseManager.loggedIn()) {
                        progressDialog = new CustomProgressDialog(NewTripActivity.this, "Creating Trip", this);
                        progressDialog.show();

                        DatabaseReference pushedTrip = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid()).child("Trips").push();

                        if (str.equals("dest"))
                            data = new MyTrip(destination, destinationLatLng, startDate, startStamp, endDate, endStamp, placeId, pushedTrip.getKey());
                        else if (str.equals("orig"))
                            data = new MyTrip(origin, destination, originLatLng, destinationLatLng, startDate, startStamp, endDate, startStamp, placeId, pushedTrip.getKey());

                        MyTrip finalData = data;
                        pushedTrip.setValue(data).addOnCompleteListener(task -> {
                            progressDialog.hide();
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
                                intent.putExtra("trip", finalData);
                                startActivityForResult(intent, 200, ActivityOptions.makeSceneTransitionAnimation(NewTripActivity.this).toBundle());
                                handler.postDelayed(this::finish, 900);
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to create new trip, Try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else{

                        DatabaseReference pushedTrip = firebaseDatabase.getReference("UserData").push();

                        if (str.equals("dest"))
                            data = new MyTrip(destination, destinationLatLng, startDate, startStamp, endDate, endStamp, placeId, pushedTrip.getKey());
                        else if (str.equals("orig"))
                            data = new MyTrip(origin, destination, originLatLng, destinationLatLng, startDate, startStamp, endDate, endStamp, placeId, pushedTrip.getKey());

                        viewmodel.insertTrip(data);

                        Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
                        intent.putExtra("trip", data);
                        startActivityForResult(intent, 200, ActivityOptions.makeSceneTransitionAnimation(NewTripActivity.this).toBundle());
                        handler.postDelayed(this::finish, 900);

                    }

                } else
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();

            }
        }
    }
}

