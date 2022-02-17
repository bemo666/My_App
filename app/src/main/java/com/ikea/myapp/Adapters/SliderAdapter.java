package com.ikea.myapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.UpcomingFragment;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private List<MyTrip> sliderItems;
    private Context context;
    private UpcomingFragment fragment;

    public SliderAdapter(UpcomingFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }



    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_trips,
                parent,
                false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {

        holder.setImageView(sliderItems.get(position));
        holder.itemView.setOnClickListener(view -> fragment.goToEditTripActivity(holder.imageView));

    }

    @Override
    public int getItemCount() {
        if (sliderItems != null)
            return sliderItems.size();
        else
            return 0;
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imageView;
        private TextView placeName, dates;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            placeName = itemView.findViewById(R.id.placeName);
            dates = itemView.findViewById(R.id.dates);
        }

        void setImageView(MyTrip trip) {
//            if you want to display an image from the internet set glide or picasso here
            imageView.setImageResource(R.drawable.london1);
            placeName.setText(trip.getDestination());
            SimpleDateFormat simpleFormatMonth = new SimpleDateFormat("MMM dd");
            SimpleDateFormat simpleFormatYear = new SimpleDateFormat("MMM dd, yyyy");
            String startDate, endDate;
            if (trip.getStartDate().getYear() == Calendar.getInstance().get(Calendar.YEAR) - 1900) {
                startDate = simpleFormatMonth.format(trip.getStartDate());
                endDate = simpleFormatMonth.format(trip.getEndDate());
                if (trip.getEndDate().getYear() > trip.getStartDate().getYear()) {
                    startDate = simpleFormatYear.format(trip.getStartDate());
                    endDate = simpleFormatYear.format(trip.getEndDate());
                }
            } else {
                startDate = simpleFormatYear.format(trip.getStartDate());
                endDate = simpleFormatYear.format(trip.getEndDate());
                if (trip.getEndDate().getYear() == trip.getStartDate().getYear()) {
                    startDate = simpleFormatMonth.format(trip.getStartDate());
                }
            }
            dates.setText(startDate + " - " + endDate);
        }
    }

    public void setSliderItems(List<MyTrip> sliderItems) {
        this.sliderItems = sliderItems;
        notifyDataSetChanged();

    }

}
