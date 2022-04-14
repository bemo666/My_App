package com.ikea.myapp.UI.newTrip;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.ikea.myapp.Notification;
import com.ikea.myapp.UI.profile.CurrenciesRVAdapter;
import com.ikea.myapp.models.CustomCurrency;
import com.ikea.myapp.models.CustomProgressDialog;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class NewTripActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring Variables
    private TextInputEditText inputDestination, inputDates, inputTimezone;
    private Toolbar toolbar;
    private TextView welcomeText;
    private FirebaseManager firebaseManager;
    private String placeId = "", destination = "";
    private LatLng destinationLatLng;
    private String startStamp, endStamp;
    private MaterialButton createButton;
    private CustomProgressDialog progressDialog;
    private final Handler handler = new Handler();
    private NewTripActivityViewModel viewmodel;
    private Place dest;
    private List<Place.Field> fieldList;
    private CustomCurrency c;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private String timezone;
    private Pair<Date, Date> rangeDate;
    private BottomSheetDialog timezoneSheet;
    private RecyclerView timezoneRV;
    private TimezoneRVAdapter timezoneRVAdapter;
    private EditText timezoneSearchBar;


    //Location Permission
    private final int PERMISSIONS_FINE_LOCATION = 99;

    public NewTripActivity() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        setTitle("");

        //Linking xml objects to java variables
        inputDestination = findViewById(R.id.inputDestination);
        welcomeText = findViewById(R.id.welcomeText);
        inputDates = findViewById(R.id.inputDates);
        createButton = findViewById(R.id.create_trip);
        toolbar = findViewById(R.id.newTripToolbar);
        inputTimezone = findViewById(R.id.inputTimezone);
        c = new CustomCurrency(Currency.getInstance(Locale.US));
        firebaseManager = new FirebaseManager();
        progressDialog = new CustomProgressDialog(this, "Creating Trip");

        timezoneSheet = new BottomSheetDialog(this);
        timezoneSheet.setContentView(R.layout.dialog_timezone_selector);
        timezoneRV = timezoneSheet.findViewById(R.id.timezone_recycler_view);
        timezoneSearchBar = timezoneSheet.findViewById(R.id.timezone_search_edit_text);


        viewmodel = new ViewModelProvider(this).get(NewTripActivityViewModel.class);

        populateAndUpdateTimeZone();
        viewmodel.getName().observe(this, name -> {
            if (name != null) {
                welcomeText.setText(getString(R.string.newtrip_hey) + name + getString(R.string.ui_comma));
            }
        });


        MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker().setTheme(R.style.ThemeOverlay_App_DatePicker).build();
        inputDates.setOnClickListener(view -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(selection -> {
            inputDates.setText(datePicker.getHeaderText());
            rangeDate = new Pair<>(new Date((Long) selection.first), new Date((Long) ((Pair) selection).second));
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
        inputTimezone.setOnClickListener(view -> timezoneSheet.show());
        timezoneRVAdapter = new TimezoneRVAdapter(this);
        timezoneRV.setAdapter(timezoneRVAdapter);
        timezoneRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        timezoneSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                timezoneRVAdapter.searchUpdate(editable.toString());
            }
        });
    }

    @Nullable
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog = super.onCreateDialog(id, args);
        if (dialog instanceof BottomSheetDialog) {
            ((BottomSheetDialog) dialog).getBehavior().setSkipCollapsed(false);
            ((BottomSheetDialog) dialog).getBehavior().setState(STATE_EXPANDED);
        }
        return dialog;
    }

    private void initializeAPIs() {
        //Initialize the places api
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.g_apiKey));
        }

    }

    private void initializeLocationStuff() {
        inputDestination.setFocusable(false);
        inputDestination.setOnClickListener(view -> {
            initializeAPIs();
            fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID, Place.Field.PHOTO_METADATAS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                    , fieldList).build(NewTripActivity.this);
            startActivityForResult(intent, 100);

        });
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        updateLocationVar(location);
//                    }
//                }
//                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//            }
//        };
//        getLocation.setOnClickListener(view -> {
//            updateGPS();
//        });
    }

//    private void updateLocationVar(Location location) {
//        if (location != null) {
//            originLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//            Toast.makeText(this, originLatLng.toString(), Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Can't find your current location", Toast.LENGTH_SHORT).show();
//            getLiveLocation();
//        }
//    }
//
//
//    @SuppressLint("MissingPermission")
//    private void getLiveLocation() {
//        Log.d("tag", "getting live location");
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
//
//    private void updateGPS() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            if (locationEnabled()) {
//                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        updateLocationVar(location);
//                    } else {
//                        getLiveLocation();
//                    }
//                });
//            }
//        } else {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
//        }
//    }
//
//    private boolean locationEnabled() {
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//
//        try {
//            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch (Exception ex) {
//        }
//
//        if (!gps_enabled && !network_enabled) {
//            androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme_LocationOff);
//            alertDialog.setTitle(R.string.gps_network_not_enabled);
//            alertDialog.setMessage(R.string.gps_network_not_enabled_message);
//            alertDialog.setPositiveButton(R.string.open_location_settings, (dialog, which) -> {
//                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            });
//            alertDialog.setNegativeButton(R.string.ui_cancel, (dialog, which) -> dialog.cancel());
//            androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
//            dialog.show();
//            return false;
//        } else
//            return true;
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private Boolean verifyInput() { return rangeDate != null && destinationLatLng != null && timezone != null; }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            dest = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
            destination = dest.getName();
            inputDestination.setText(dest.getName());
            destinationLatLng = dest.getLatLng();
            placeId = dest.getId();
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.create_trip) {
            {
                if (verifyInput()) {
                    progressDialog.show();
                    createTrip();
                } else
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void createTrip() {
        DatabaseReference pushedTrip = firebaseManager.newTrip();
        TimeZone tz = TimeZone.getDefault();
        startStamp = String.valueOf(rangeDate.first.getTime() - tz.getOffset(rangeDate.first.getTime()));
        endStamp = String.valueOf(rangeDate.second.getTime() - tz.getOffset(rangeDate.first.getTime()) + 86399999);
        MyTrip data = new MyTrip(destination, destinationLatLng, startStamp, endStamp, placeId,
                Objects.requireNonNull(pushedTrip.getKey()), c, timezone);
        fetchImage(pushedTrip.getKey());
        if (FirebaseManager.loggedIn()) {
            pushedTrip.setValue(data).addOnCompleteListener(task -> {
                progressDialog.hide();
                if (task.isSuccessful()) {
                    tripCreated(pushedTrip.getKey());
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to create new trip, Try again later", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            viewmodel.insertLocalTrip(data);
            tripCreated(pushedTrip.getKey());
        }
    }

    private void tripCreated(String key) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                (Long.parseLong(startStamp) - 27000000),
                PendingIntent.getBroadcast(getApplicationContext(), 0,
                        new Intent(this, Notification.class), 0));
        Intent intent = new Intent(getApplicationContext(), EditTripActivity.class);
        intent.putExtra("id", key);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(NewTripActivity.this).toBundle());
        handler.postDelayed(this::finish, 900);
    }

    private void fetchImage(String key) {
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fieldList);
        PlacesClient placesClient = Places.createClient(this);
        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();
            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.d("tag", "No photo metadata.");
            } else {
                final PhotoMetadata photoMetadata = metadata.get(0);
                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                        .setMaxWidth(500) // Optional.
//                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                    if (FirebaseManager.loggedIn()) {
                        firebaseManager.addTripImage(key, baos.toByteArray());
                    } else {
                        File file = new File(getFilesDir(), key + ".jpg");

                        try (FileOutputStream fos = openFileOutput(file.getName(), Context.MODE_PRIVATE)) {
                            fos.write(baos.toByteArray());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        viewmodel.setLocalImage(key, file.getAbsolutePath());
                    }

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        Log.e("tag", "Place not found: " + exception.getMessage());
                    }
                });
            }

        });
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
        inputTimezone.setText(this.timezone);
        timezoneSheet.dismiss();
    }
}

