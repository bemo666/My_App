package com.ikea.myapp.UI.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.getCorrectDate;
import com.makeramen.roundedimageview.RoundedImageView;

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
            imageView.setImageResource(R.drawable.london2);
            placeName.setText(trip.getDestination());
            getCorrectDate date = new getCorrectDate(trip);
            dates.setText(date.getDatesPastFormat());

            try {
                byte[] encodeByte = Base64.decode(trip.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
            }
        }
    }

    public void setTrips(List<MyTrip> sliderItems) {
        this.sliderItems = sliderItems;
        notifyDataSetChanged();

    }
}
