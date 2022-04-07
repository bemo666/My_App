package com.ikea.myapp.UI.newTrip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
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
import com.ikea.myapp.data.remote.AmadeusApi;
import com.ikea.myapp.models.CustomCurrency;
import com.ikea.myapp.models.CustomProgressDialog;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

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
    private String origin = "", placeId = "", destination = "", tripType;
    private LatLng originLatLng, destinationLatLng;
    private String startDate, endDate, startStamp, endStamp;
    private MaterialButton createButton;
    private boolean isDestination;
    private CustomProgressDialog progressDialog;
    private final Handler handler = new Handler();
    private NewTripActivityViewModel viewmodel;
    private boolean first;
    private Place dest, orig;
    private List<Place.Field> fieldList;
    private byte[] image;
    private CustomCurrency c;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    AmadeusApi amadeusRequests;


    //Location Permission
    private final int PERMISSIONS_FINE_LOCATION = 99;

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
        getLocation = findViewById(R.id.getLocation);
        c = new CustomCurrency(Currency.getInstance(Locale.US));


        //Set welcome text
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (FirebaseManager.loggedIn()) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userRef = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid());
        }
        viewmodel = new ViewModelProvider(this).get(NewTripActivityViewModel.class);
        first = true;

        populateAndUpdateTimeZone();
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

    private void populateAndUpdateTimeZone() {
        AutoCompleteTextView editTextFilledExposedDropdown =  findViewById(R.id.filled_exposed_dropdown);
        String[] idArray = TimeZone.getAvailableIDs();
        Log.d("tag", idArray.length+ "");
        ArrayAdapter<String> idAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, idArray);
        Log.d("tag", idAdapter.getCount() + "");
        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTextFilledExposedDropdown.setAdapter(idAdapter);

        // now set the spinner to default timezone from the time zone settings
//        for (int i = 0; i < idAdapter.getCount(); i++) {
//            if (idAdapter.getItem(i).equals(TimeZone.getDefault().getID())) {
//                editTextFilledExposedDropdown.setSelection(i);
//            }
//        }
    }

//    private void populateAndUpdateTimeZone() {
//
//        //populate spinner with all timezones
//        Spinner mSpinner = (Spinner) findViewById(R.id.mytimezonespinner);
//        String[] idArray = TimeZone.getAvailableIDs();
//        ArrayAdapter<String> idAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
//                idArray);
//        idAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(idAdapter);
//
//        // now set the spinner to default timezone from the time zone settings
//        for (int i = 0; i < idAdapter.getCount(); i++) {
//            if (idAdapter.getItem(i).equals(TimeZone.getDefault().getID())) {
//                mSpinner.setSelection(i);
//            }
//        }
//    }


    private void initializeAPIs() {
        amadeusRequests = new AmadeusApi(this);
        //Initialize the places api
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.g_apiKey));
        }

    }

    private void initializeLocationStuff() {
        inputDestination.setFocusable(false);
        inputOrigin.setFocusable(false);
        inputDestination.setOnClickListener(view -> {
            initializeAPIs();
            fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID, Place.Field.PHOTO_METADATAS);
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

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        updateLocationVar(location);
                    }
                }
                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        };
        getLocation.setOnClickListener(view -> {
            updateGPS();
        });
    }

    private void updateLocationVar(Location location) {
        if (location != null) {
            originLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(this, originLatLng.toString(), Toast.LENGTH_SHORT).show();
//            amadeusRequests.getNearestAirport(originLatLng).observe(NewTripActivity.this, locations -> {
//                if (locations != null) {
//                    Log.d("tag", "length: " + locations.length);
//                    for (com.amadeus.resources.Location l : locations) {
//
//                        Log.d("tag", "name: " + l.getName());
////                        Log.d("tag", "Analytics: " + l.getAnalytics());
//                        Log.d("tag", "DetailedName: " + l.getDetailedName());
//                        Log.d("tag", "IataCode: " + l.getIataCode());
//                        Log.d("tag", "SubType: " + l.getSubType());
////                        Log.d("tag", "TimezoneOffset: " + l.getTimeZoneOffset());
////                        Log.d("tag", "Type: " + l.getType());
////                        Log.d("tag", "Distance: " + l.getDistance());
////                        Log.d("tag", "Relevance: " + l.getRelevance());
////                        Log.d("tag", "GeoCode: " + l.getGeoCode());
//                    }
//                }
//            });
        } else {
            Toast.makeText(this, "Can't find your current location", Toast.LENGTH_SHORT).show();
            getLiveLocation();
        }
    }


    @SuppressLint("MissingPermission")
    private void getLiveLocation() {
        Log.d("tag", "getting live location");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationEnabled()) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateLocationVar(location);
                    } else {
                        getLiveLocation();
                    }
                });
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    private boolean locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme_LocationOff);
            alertDialog.setTitle(R.string.gps_network_not_enabled);
            alertDialog.setMessage(R.string.gps_network_not_enabled_message);
            alertDialog.setPositiveButton(R.string.open_location_settings, (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            });
            alertDialog.setNegativeButton(R.string.ui_cancel, (dialog, which) -> dialog.cancel());
            androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
            dialog.show();
            return false;
        } else
            return true;
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
            if (isDestination) {
                dest = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                destination = dest.getName();
                inputDestination.setText(dest.getName());
                destinationLatLng = dest.getLatLng();
                placeId = dest.getId();
            } else {
                orig = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                origin = orig.getName();
                inputOrigin.setText(orig.getName());
                Location l = new Location("y");
                l.setLatitude(Objects.requireNonNull(orig.getLatLng()).latitude);
                l.setLongitude(Objects.requireNonNull(orig.getLatLng()).longitude);
                updateLocationVar(l);
                originLatLng = orig.getLatLng();
            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_trip) {
            {
                tripType = verifyInput();
                if (!tripType.equals("false")) {
                    final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fieldList);
                    PlacesClient placesClient = Places.createClient(this);
                    placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                        final Place place = response.getPlace();
                        // Get the photo metadata.
                        final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                        if (metadata == null || metadata.isEmpty()) {
                            Log.d("tag", "No photo metadata.");
                            createTrip();
                        } else {
                            final PhotoMetadata photoMetadata = metadata.get(0);
                            // Get the attribution text.
                            final String attributions = photoMetadata.getAttributions();
                            // Create a FetchPhotoRequest.
                            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                                    .setMaxWidth(500) // Optional.
                                    .setMaxHeight(300) // Optional.
                                    .build();
                            progressDialog = new CustomProgressDialog(this, "Creating Trip");
                            progressDialog.show();
                            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                Log.d("tag", "worked");
                                image = baos.toByteArray();

                                createTrip();

                            }).addOnFailureListener((exception) -> {
                                if (exception instanceof ApiException) {
                                    Log.e("tag", "Place not found: " + exception.getMessage());
                                }
                            });
                        }


                    });


                } else
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void createTrip() {
        MyTrip data = null;
        if (FirebaseManager.loggedIn()) {
            DatabaseReference pushedTrip = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid()).child("Trips").push();
            if (tripType.equals("dest"))
                data = new MyTrip(destination, destinationLatLng, startDate, startStamp, endDate, endStamp, placeId, pushedTrip.getKey(), c);
            else if (tripType.equals("orig"))
                data = new MyTrip(origin, destination, originLatLng, destinationLatLng, startDate, startStamp, endDate, startStamp, placeId, pushedTrip.getKey(), c);

            pushedTrip.setValue(data).addOnCompleteListener(task -> {
                progressDialog.hide();
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
                    intent.putExtra("id", pushedTrip.getKey());
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(NewTripActivity.this).toBundle());
                    handler.postDelayed(this::finish, 900);
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to create new trip, Try again later", Toast.LENGTH_SHORT).show();
                }
            });
        } else {

            DatabaseReference pushedTrip = firebaseDatabase.getReference("UserData").push();

            if (tripType.equals("dest"))
                data = new MyTrip(destination, destinationLatLng, startDate, startStamp, endDate, endStamp, placeId, pushedTrip.getKey(), c);
            else if (tripType.equals("orig"))
                data = new MyTrip(origin, destination, originLatLng, destinationLatLng, startDate, startStamp, endDate, endStamp, placeId, pushedTrip.getKey(), c);

            viewmodel.insertLocalTrip(data);

//                viewmodel.setLocalImage(data.getId(), image);

            Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
            intent.putExtra("id", pushedTrip.getKey());
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(NewTripActivity.this).toBundle());
            handler.postDelayed(this::finish, 900);

        }
    }
}

