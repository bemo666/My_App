package com.ikea.myapp.UI.editTrip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ikea.myapp.Adapters.FragmentAdapter;
import com.ikea.myapp.UI.main.UpcomingFragment;
import com.ikea.myapp.data.remote.FirebaseManager;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.R;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.utils.MyViewModelFactory;
import com.ikea.myapp.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class EditTripActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ViewPager2 fragments;
    private ImageView mainImage;
    private TextView placeName;
    private CollapsingToolbarLayout collapsingToolbar;
    private String id, goTo;
    private MyTrip trip;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private Animation rotateOpen, rotateClose, slideIn, slideOut, fadeIn, fadeOut;
    private FloatingActionButton addButton;
    private ExtendedFloatingActionButton notes, flights, hotels, rentals, activities;
    private boolean clicked = false;
    private View mask;
    private EditTripViewModel viewModel;
    private CoordinatorLayout.LayoutParams layoutParams;
    private ItineraryFragment itineraryFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        id = getIntent().getStringExtra("id");
        goTo = getIntent().getStringExtra("goto");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        fragments = findViewById(R.id.editTrip_viewpager);
        toolbar = findViewById(R.id.editTripToolbar);
        collapsingToolbar = findViewById(R.id.editTripCollapsingToolbar);
        placeName = findViewById(R.id.editTrip_placeName);
        appBarLayout = findViewById(R.id.editTripAppBar);
        addButton = findViewById(R.id.add_button);
        notes = findViewById(R.id.add_notes);
        flights = findViewById(R.id.add_flights);
        hotels = findViewById(R.id.add_hotel);
        rentals = findViewById(R.id.add_rental);
        activities = findViewById(R.id.add_activity);
        mask = findViewById(R.id.editTrip_mask);
        mainImage = findViewById(R.id.editTrip_mainImage);
        tabLayout = findViewById(R.id.editTripTabLayout);
        itineraryFragment = new ItineraryFragment(id, this);

        viewModel = ViewModelProviders.of(this, new MyViewModelFactory(getApplication(), id)).get(EditTripViewModel.class);

        viewModel.getTrip().observe(this, myTrip -> {
            trip = myTrip;
            if (trip != null) {
                placeName.setText(trip.getDestination());
            }
        });

        viewModel.getImage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (trip.getImage() != null)
                    Glide.with(EditTripActivity.this).load(trip.getImage()).fitCenter().skipMemoryCache(true).into(mainImage);
            }
        });

        //Fragments setup
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(itineraryFragment);
        list.add(new MapFragment(id));
        list.add(new BudgetFragment(id));
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), list);
        fragments.setAdapter(fragmentAdapter);
        fragments.setUserInputEnabled(false);
        addButton.setOnClickListener(view -> click());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                fragments.setCurrentItem(tab.getPosition());
                if (clicked) {
                    click();
                }
                switch (tab.getPosition()) {
                    case 0:
                        addButton.show();
                        addButton.setEnabled(true);
                        break;
                    case 1:
                        appBarLayout.setExpanded(false, true);
                        addButton.hide();
                        addButton.setEnabled(false);
                        break;
                    case 2:
                        appBarLayout.setExpanded(false, true);
                        addButton.hide();
                        addButton.setEnabled(false);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if(goTo != null){
            if(goTo.equals("map")){
                openMapsFragment();
            }
        }


        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false, set = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset <= 85) {
                    if (!set) {
                        if (trip != null)
                            collapsingToolbar.setTitle(trip.getDestination());
                        set = true;
                    }

                    isShow = true;
                } else if (isShow) {
                    //Careful there must a space between double quote otherwise it doesn't work
                    collapsingToolbar.setTitle(" ");
                    set = false;
                    isShow = false;
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(toolbar);
        setTitle(" ");

        mask.setOnClickListener(view -> {
            if(clicked)
            click();
        });
        addButton.setOnClickListener(view -> click());
        notes.setOnClickListener(this);
        hotels.setOnClickListener(this);
        rentals.setOnClickListener(this);
        flights.setOnClickListener(this);
        activities.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        if(!clicked) {
            super.onBackPressed();
            finishAfterTransition();
        } else {
            click();
        }
    }

    private void click() {
        setVisibility();
        setAnimation();
        clicked = !clicked;
    }


    private void setAnimation() {
        if (!clicked) {
            notes.startAnimation(slideOut);
            flights.startAnimation(slideOut);
            hotels.startAnimation(slideOut);
            rentals.startAnimation(slideOut);
            activities.startAnimation(slideOut);
            addButton.startAnimation(rotateOpen);
            mask.startAnimation(fadeIn);
        } else {
            notes.startAnimation(slideIn);
            flights.startAnimation(slideIn);
            hotels.startAnimation(slideIn);
            rentals.startAnimation(slideIn);
            activities.startAnimation(slideIn);
            addButton.startAnimation(rotateClose);
            mask.startAnimation(fadeOut);
        }
    }

    private void setVisibility() {
        if (!clicked) {
//            appBarLayout.setExpanded(true, false);
            notes.show();
            flights.show();
            hotels.show();
            rentals.show();
            activities.show();
            mask.setVisibility(View.VISIBLE);

        } else {
            notes.hide();
            flights.hide();
            hotels.hide();
            rentals.hide();
            activities.hide();
            mask.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == notes.getId()) {
            itineraryFragment.checkForAddHeader(PlanType.Note);
        } else if (id == flights.getId()) {
            itineraryFragment.checkForAddHeader(PlanType.Flight);
        } else if (id == hotels.getId()) {
            itineraryFragment.checkForAddHeader(PlanType.Hotel);
        } else if (id == rentals.getId()) {
            itineraryFragment.checkForAddHeader(PlanType.Rental);
        } else if (id == activities.getId()) {
            itineraryFragment.checkForAddHeader(PlanType.Activity);
        }

        click();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_trip_menu, menu);
        Drawable deleteTripIcon = menu.findItem(R.id.delete_trip).getIcon(),
                changeImageIcon = menu.findItem(R.id.change_image).getIcon();
        deleteTripIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red), PorterDuff.Mode.SRC_IN);
        changeImageIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black), PorterDuff.Mode.SRC_IN);
        if (menu instanceof MenuBuilder) {

            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_trip:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme_Delete)
                        .setTitle(R.string.ui_delete_trip)
                        .setMessage("Are you sure you want to delete your trip to " + trip.getDestination() + "?")
                        .setPositiveButton("DELETE", (dialog, which) -> {
                            if (trip != null){
                                viewModel.deleteTrip(trip);
                                Log.d("tag", "deletetd");
                            }
                            finish();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = alertDialog.create();
                dialog.show();
                return true;

            case R.id.change_image:
                Toast.makeText(getApplicationContext(), "Fetching new image", Toast.LENGTH_LONG).show();
                fetchImage();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:

                super.onOptionsItemSelected(item);

        }
        return true;

    }


    private void fetchImage() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.g_apiKey));
        }
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID, Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(trip.getPlaceId(), fieldList);
        PlacesClient placesClient = Places.createClient(this);
        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
            final Place place = response.getPlace();
            // Get the photo metadata.
            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.d("tag", "No photo metadata.");
            } else {
                if (trip.getImageVersion() >= 9)
                    trip.setImageVersion(0);
                final PhotoMetadata photoMetadata = metadata.get(trip.getImageVersion());
                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();

                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

                    File file = new File(getFilesDir(), id + "_" + (trip.getImageVersion() + 1 ) + ".jpg");

                    try (FileOutputStream fos = openFileOutput(file.getName(), Context.MODE_PRIVATE)) {
                        fos.write(baos.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    viewModel.setImage(trip.getId(), file.getAbsolutePath(), trip.getImageVersion());

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        Log.e("tag", "Place not found: " + exception.getMessage());
                    }
                });
            }

        });

    }
    public void openMapsFragment(){
        tabLayout.selectTab(tabLayout.getTabAt(1), true);
    }

    @Override
    protected void onPause() {
        UpcomingFragment.setSliderId(id);
        super.onPause();
    }
}