package com.ikea.myapp.UI.newTrip;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ikea.myapp.Notification;
import com.ikea.myapp.UI.profile.CurrenciesRVAdapter;
import com.ikea.myapp.models.CustomCurrency;
import com.ikea.myapp.models.CustomDateTime;
import com.ikea.myapp.models.CustomProgressDialog;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class NewTripActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring Variables
    private TextInputEditText inputDestination, inputDates;
    private Toolbar toolbar;
    private TextView welcomeText;
    private FirebaseManager firebaseManager;
    private String placeId = "", destination = "";
    private LatLng destinationLatLng, ne, sw;
    private long startStamp;
    private long endStamp;
    private MaterialButton createButton;
    private CustomProgressDialog progressDialog;
    private final Handler handler = new Handler();
    private NewTripActivityViewModel viewmodel;
    private Place dest;
    private List<Place.Field> fieldList;
    private CustomCurrency c;
    private Pair<Date, Date> rangeDate;
    private String country;

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
        c = new CustomCurrency(Currency.getInstance(Locale.US));
        firebaseManager = new FirebaseManager();
        progressDialog = new CustomProgressDialog(this, "Creating Trip");

        viewmodel = new ViewModelProvider(this).get(NewTripActivityViewModel.class);
        viewmodel.getName().observe(this, name -> {
            if (!name.equals("-1")) {
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
            fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID, Place.Field.PHOTO_METADATAS, Place.Field.VIEWPORT, Place.Field.ADDRESS_COMPONENTS);
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                    , fieldList).build(NewTripActivity.this);
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

    private Boolean verifyInput() {
        return rangeDate != null && destinationLatLng != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            dest = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
            destination = dest.getName();
            inputDestination.setText(dest.getName());
            destinationLatLng = dest.getLatLng();
            placeId = dest.getId();
            sw = Objects.requireNonNull(dest.getViewport()).southwest;
            ne = Objects.requireNonNull(dest.getViewport()).northeast;
            List<AddressComponent> addressComponents = dest.getAddressComponents().asList();
            for (AddressComponent component: addressComponents){
                for (String type : component.getTypes()){
                    if (type.equals("country")){
                        country = component.getName();
                    }
                }
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
        CustomDateTime start = new CustomDateTime(0, 0,
                rangeDate.first.getDate(),
                rangeDate.first.getDay(),
                rangeDate.first.getMonth() + 1,
                rangeDate.first.getYear() + 1900);
        CustomDateTime end = new CustomDateTime(59, 11,
                rangeDate.first.getDate(),
                rangeDate.first.getDay(),
                rangeDate.first.getMonth() + 1,
                rangeDate.first.getYear() + 1900);
        startStamp = rangeDate.first.getTime();
        endStamp = rangeDate.second.getTime() + 86399999;

        MyTrip data = new MyTrip(destination, destinationLatLng, sw, ne, startStamp, start, endStamp, end, placeId,
                Objects.requireNonNull(pushedTrip.getKey()), c, country);
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
        TimeZone tz = TimeZone.getDefault();
        Intent displayNotification = new Intent(this, Notification.class);
        displayNotification.putExtra("id", key);
        displayNotification.putExtra("tripName", destination);
        displayNotification.putExtra("tripTime", startStamp);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                (startStamp + tz.getOffset(startStamp) - 129600000),
                PendingIntent.getBroadcast(getApplicationContext(), 0,
                        displayNotification, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

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
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                    File file = new File(getFilesDir(), key + "_1" + ".jpg");

                    try (FileOutputStream fos = openFileOutput(file.getName(), Context.MODE_PRIVATE)) {
                        fos.write(baos.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    viewmodel.setImage(key, file.getAbsolutePath(), 0);

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        Log.e("tag", "Place not found: " + exception.getMessage());
                    }
                });
            }

        });
    }
}

