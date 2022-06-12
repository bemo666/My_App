package com.ikea.myapp.UI.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.utils.getCorrectDate;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PastTripsRVAdapter extends RecyclerView.Adapter<PastTripsRVAdapter.SliderViewHolder> {

    private List<MyTrip> sliderItems;
    private final PastFragment fragment;

    public PastTripsRVAdapter(PastFragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_past_trips, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setCardView(sliderItems.get(position));
        holder.itemView.setOnClickListener(view -> fragment.goToEditTripActivity(holder.imageView, holder.placeName, position));
    }


    @Override
    public int getItemCount() {
        if (sliderItems != null)
            return sliderItems.size();
        else
            return 0;
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private final RoundedImageView imageView;
        private final TextView placeName, dates;


        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            placeName = itemView.findViewById(R.id.placeName);
            dates = itemView.findViewById(R.id.dates);

        }

        void setCardView(MyTrip trip) {
            if (trip.getImage() != null){
                File file = new File(trip.getImage());
                if(file.exists())
                    Glide.with(fragment.getContext()).load(trip.getImage()).fitCenter().skipMemoryCache(true).into(imageView);
                else
                    fetchImage(trip);

            }
            placeName.setText(trip.getDestination());
            getCorrectDate date = new getCorrectDate(trip);
            dates.setText(date.getDatesOnlyMonthAndYearFormat());
        }


    }

    private void fetchImage(MyTrip trip) {
        if (!Places.isInitialized()) {
            Places.initialize(fragment.getActivity().getApplicationContext(), fragment.getContext().getString(R.string.g_apiKey));
        }
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID, Place.Field.PHOTO_METADATAS);
        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(trip.getPlaceId(), fieldList);
        PlacesClient placesClient = Places.createClient(fragment.getContext());
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

                    File file = new File(fragment.getContext().getFilesDir(), trip.getId() + "_" + (trip.getImageVersion() + 1 ) + ".jpg");

                    try (FileOutputStream fos = fragment.getContext().openFileOutput(file.getName(), Context.MODE_PRIVATE)) {
                        fos.write(baos.toByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    fragment.setImage(trip.getId(), file.getAbsolutePath(), trip.getImageVersion());

                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        Log.e("tag", "Place not found: " + exception.getMessage());
                    }
                });
            }

        });

    }

    public void setTrips(List<MyTrip> sliderItems) {
        this.sliderItems = sliderItems;
        notifyDataSetChanged();

    }
}
