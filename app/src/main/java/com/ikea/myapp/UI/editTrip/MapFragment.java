package com.ikea.myapp.UI.editTrip;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.shape.AbsoluteCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.ikea.myapp.R;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.utils.MyViewModelFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private final String id;
    private EditTripViewModel viewModel;
    private CardView card, button;
    private ChipGroup chipGroup;
    private TextView rating, ratingCount, time, location, url, phone;
    private LinearLayout ratingLayout, timeLayout, locationLayout, urlLayout, phoneLayout;
    private ImageView zoomImage;
    private GoogleMap mMap;
    private MyTrip trip;
    private final int PERMISSIONS_FINE_LOCATION = 99;
    private boolean zoomedIn;
    private List<Marker> markerList;
    private Marker currentMarker;
    private Marker originalMarker;

    public MapFragment(String id) {
        this.id = id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isServicesOK();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        rating = view.findViewById(R.id.marker_rating_text);
        ratingCount = view.findViewById(R.id.marker_rating_review_count);
        time = view.findViewById(R.id.marker_time_text);
        location = view.findViewById(R.id.marker_location_text);
        url = view.findViewById(R.id.marker_url_text);
        phone = view.findViewById(R.id.marker_phone_number_text);

        ratingLayout = view.findViewById(R.id.marker_rating_layout);
        timeLayout = view.findViewById(R.id.marker_time_layout);
        locationLayout = view.findViewById(R.id.marker_location_layout);
        urlLayout = view.findViewById(R.id.marker_url_layout);
        phoneLayout = view.findViewById(R.id.marker_phone_number_layout);

        card = view.findViewById(R.id.map_card);
        card.setVisibility(View.GONE);
        card.setOnClickListener(this);
        button = view.findViewById(R.id.map_zoom_button);
        zoomImage = view.findViewById(R.id.map_zoom_image);
        chipGroup = view.findViewById(R.id.marker_chip_group);
        viewModel = ViewModelProviders.of(requireActivity(), new MyViewModelFactory(requireActivity().getApplication(), id)).get(EditTripViewModel.class);
        viewModel.getTrip().observe(getViewLifecycleOwner(), myTrip -> {
            if (myTrip != null) {
                trip = myTrip;
                updateMap();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        markerList = new ArrayList<>();

        button.setOnClickListener(view1 -> zoom());

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mMap.setOnMarkerClickListener(marker -> {
                currentMarker = marker;
                marker.hideInfoWindow();
                showMoreInfo(marker);
                return false;
            });
            mMap.setOnMapClickListener(latLng -> {
                zoomImage.setImageResource(R.drawable.ic_zoom_in);
                zoomedIn = false;
                hideMoreInfo();
            });
        }, 300);
        return view;
    }

    private void zoom() {
        if (!zoomedIn) {
            if (currentMarker == null) {
                if ( markerList.size()==0)
                    currentMarker = originalMarker;
                else
                    currentMarker = markerList.get(0);
            }
            showMoreInfo(currentMarker);
            zoomImage.setImageResource(R.drawable.ic_zoom_out_map);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentMarker.getPosition(), 17));
            zoomedIn = true;
        } else {
            LatLng sw = new LatLng(trip.getSwLat(), trip.getSwLon());
            LatLng ne = new LatLng(trip.getNeLat(), trip.getNeLon());
            LatLngBounds bounds = new LatLngBounds(sw, ne);
            for (Marker m : markerList) {
                bounds = bounds.including(m.getPosition());
            }
            hideMoreInfo();
            zoomImage.setImageResource(R.drawable.ic_zoom_in);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            zoomedIn = false;
        }
    }

    private void hideMoreInfo() {
        card.setVisibility(View.GONE);
    }

    private void showMoreInfo(Marker marker) {
        if (!Objects.equals(marker.getTitle(), trip.getDestination())) {
            card.setVisibility(View.VISIBLE);
            chipGroup.removeAllViews();
            ratingLayout.setVisibility(View.GONE);
            locationLayout.setVisibility(View.GONE);
            phoneLayout.setVisibility(View.GONE);
            timeLayout.setVisibility(View.GONE);
            urlLayout.setVisibility(View.GONE);
            for (Plan p : trip.getPlans()) {
                if (p.getStartLocationId() != null) {
                    if (p.getStartLocationId().equals(marker.getTag())) {
                        if(p.getStartLocationTimes()!= null){
                            if(p.getStartDate() != null){
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(p.getStartDate());
                                int weekDay = calendar.get(calendar.DAY_OF_WEEK) - 2;
                                weekDay = weekDay < 0? weekDay + 7: weekDay;
                                time.setText(p.getStartLocationTimes().get(weekDay));
                                timeLayout.setVisibility(View.VISIBLE);
                            }
                        }
                        if (p.getStartLocationRating() != null) {
                            rating.setText(String.valueOf(p.getStartLocationRating()));
                            ratingLayout.setVisibility(View.VISIBLE);
                        }
                        if (p.getStartLocationRatingCount() != null) {
                            ratingCount.setText(" (" + p.getStartLocationRatingCount() + ")");
                        }
                        if (p.getStartLocationAddress() != null) {
                            location.setText(p.getStartLocationAddress());
                            locationLayout.setVisibility(View.VISIBLE);
                            locationLayout.setOnClickListener(view -> {
                                String uri = String.format(Locale.ENGLISH, "geo:%1$s,%2$s?q=%1$s,%2$s", p.getStartLocationLat(), p.getStartLocationLong());
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            });
                        }
                        if (p.getStartLocationPhoneNumber() != null) {
                            phone.setText(p.getStartLocationPhoneNumber());
                            phoneLayout.setVisibility(View.VISIBLE);
                            phoneLayout.setOnClickListener(view -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + p.getStartLocationPhoneNumber()));
                                startActivity(intent);
                            });
                        }
                        if (p.getStartLocationUrl() != null) {
                            url.setText(p.getStartLocationUrl());
                            urlLayout.setVisibility(View.VISIBLE);
                            urlLayout.setOnClickListener(view -> {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getStartLocationUrl()));
                                startActivity(browserIntent);
                            });
                        }
                        if (p.getStartEstablishmentTypes() != null) {
                            for (String type : p.getStartEstablishmentTypes()) {
                                boolean proceed = true;
                                for (int i = 0; i < chipGroup.getChildCount(); i++){
                                    if (((Chip)chipGroup.getChildAt(i)).getText().equals(type)){
                                        proceed = false;
                                        break;
                                    }
                                }
                                if(proceed) {
                                    Chip chip = new Chip(requireContext());
                                    chip.setText(type);
                                    chip.setRippleColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent)));
                                    chip.setChipBackgroundColorResource(R.color.moreThanBarelyGrey);
                                    chipGroup.addView(chip);
                                }
                            }
                        }
                    }
                }
                if (p.getEndLocationId() != null) {
                    if (p.getEndLocationId().equals(marker.getTag())) {
                        if(p.getEndLocationTimes()!= null){
                            if(p.getEndDate() != null){
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(p.getEndDate());
                                int weekDay = calendar.get(calendar.DAY_OF_WEEK) - 2;
                                weekDay = weekDay < 0? weekDay + 7: weekDay;
                                time.setText(p.getEndLocationTimes().get(weekDay));
                                timeLayout.setVisibility(View.VISIBLE);
                            }
                        }
                        if (p.getEndLocationRating() != null) {
                            rating.setText(String.valueOf(p.getEndLocationRating()));
                            ratingLayout.setVisibility(View.VISIBLE);
                        }
                        if (p.getEndLocationRatingCount() != null) {
                            ratingCount.setText(" (" + p.getEndLocationRatingCount() + ")");
                        }
                        if (p.getEndLocationAddress() != null) {
                            location.setText(p.getEndLocationAddress());
                            locationLayout.setVisibility(View.VISIBLE);
                            locationLayout.setOnClickListener(view -> {
                                String uri = String.format(Locale.ENGLISH, "geo:%1$s,%2$s?q=%1$s,%2$s", p.getEndLocationLat(), p.getEndLocationLong());
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                startActivity(intent);
                            });
                        }
                        if (p.getEndLocationPhoneNumber() != null) {
                            phone.setText(p.getEndLocationPhoneNumber());
                            phoneLayout.setVisibility(View.VISIBLE);
                            phoneLayout.setOnClickListener(view -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + p.getEndLocationPhoneNumber()));
                                startActivity(intent);
                            });
                        }
                        if (p.getEndLocationUrl() != null) {
                            url.setText(p.getEndLocationUrl());
                            urlLayout.setVisibility(View.VISIBLE);
                            urlLayout.setOnClickListener(view -> {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(p.getEndLocationUrl()));
                                startActivity(browserIntent);
                            });
                        }
                        if (p.getEndEstablishmentTypes() != null) {
                            for (String type : p.getEndEstablishmentTypes()) {
                                boolean proceed = true;
                                for (int i = 0; i < chipGroup.getChildCount(); i++){
                                    if (((Chip)chipGroup.getChildAt(i)).getText().equals(type)){
                                        proceed = false;
                                        break;
                                    }
                                }
                                if(proceed) {
                                    Chip chip = new Chip(requireContext());
                                    chip.setText(type);
                                    chip.setRippleColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent)));
                                    chip.setChipBackgroundColorResource(R.color.moreThanBarelyGrey);
                                    chipGroup.addView(chip);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            hideMoreInfo();
        }
    }


    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext());
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(requireActivity(), available, ERROR_DIALOG_REQUEST);
            assert dialog != null;
            dialog.show();
        } else {
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
    }


    @SuppressLint("MissingPermission")
    private void updateMap() {
        if (trip != null && mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            if (trip.getPlans() != null) {
                for (Plan p : trip.getPlans()) {
                    if (p.getStartLocationLat() != null && p.getStartLocationLong() != null) {
                        LatLng pos = new LatLng(p.getStartLocationLat(), p.getStartLocationLong());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(p.getStartLocation()));
                        assert marker != null;
                        marker.setTag(p.getStartLocationId());
                        markerList.add(marker);
                    }
                    if (p.getEndLocationLat() != null && p.getEndLocationLong() != null) {
                        LatLng pos = new LatLng(p.getEndLocationLat(), p.getEndLocationLong());
                        Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(p.getEndLocation()));
                        assert marker != null;
                        marker.setTag(p.getEndLocationId());
                        markerList.add(marker);
                    }
                }
            }
            LatLng latLng = new LatLng(trip.getDestinationLat(), trip.getDestinationLon());
            originalMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(trip.getDestination()));
            LatLng sw = new LatLng(trip.getSwLat(), trip.getSwLon());
            LatLng ne = new LatLng(trip.getNeLat(), trip.getNeLon());
            LatLngBounds bounds = new LatLngBounds(sw, ne);
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
            mMap.setOnMyLocationButtonClickListener(() -> {
                if (checkForPermissions()) {
                    enableLocation();
                }
                return false;
            });
            if (checkForPermissions())
                mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    private boolean checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
        return false;
    }

    private void enableLocation() {
        LocationManager lm = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        if (!gps_enabled && !network_enabled) {
            androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme_LocationOff);
            alertDialog.setTitle(R.string.gps_network_not_enabled);
            alertDialog.setMessage(R.string.gps_network_not_enabled_message);
            alertDialog.setPositiveButton(R.string.open_location_settings, (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)));
            alertDialog.setNegativeButton(R.string.ui_cancel, (dialog, which) -> dialog.cancel());
            androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {

    }
}