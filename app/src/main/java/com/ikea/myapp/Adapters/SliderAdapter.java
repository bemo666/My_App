package com.ikea.myapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.main.UpcomingFragment;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        holder.setCardView(sliderItems.get(position));
        holder.itemView.setOnClickListener(view -> fragment.goToEditTripActivity(holder.imageView, position));

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
        private CardView liveBadge, liveDot;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            placeName = itemView.findViewById(R.id.placeName);
            dates = itemView.findViewById(R.id.dates);
            liveBadge = itemView.findViewById(R.id.live_badge);
            liveDot = itemView.findViewById(R.id.live_dot);

        }

        void setCardView(MyTrip trip)  {
//            if you want to display an image from the internet set glide or picasso here
            imageView.setImageResource(R.drawable.london2);
            placeName.setText(trip.getDestination());
            if(Long.valueOf(trip.getStartStamp()) < Calendar.getInstance().getTimeInMillis() &&
            Long.valueOf(trip.getEndStamp())> Calendar.getInstance().getTimeInMillis()){
                liveBadge.setVisibility(View.VISIBLE);
                liveDot.startAnimation(AnimationUtils.loadAnimation(context, R.anim.blink));

            }
            SimpleDateFormat simpleFormatMonth = new SimpleDateFormat("EEE MMM dd", Locale.ENGLISH);
            SimpleDateFormat simpleFormatYear = new SimpleDateFormat("EEE MMM dd, yyyy", Locale.ENGLISH);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
            String startDate, endDate;
            Date tripStart, tripEnd;
            try {
                tripStart = dateFormat.parse(trip.getStartDate());
                tripEnd = dateFormat.parse(trip.getEndDate());
            } catch (ParseException e) {
                tripEnd = null;
                tripStart = null;
            }

            if (tripStart.getYear() == Calendar.getInstance().get(Calendar.YEAR)-1900) {
                startDate = simpleFormatMonth.format(tripStart);
                endDate = simpleFormatMonth.format(tripEnd);
                if (tripEnd.getYear() > tripStart.getYear()) {
                    startDate = simpleFormatYear.format(tripStart);
                    endDate = simpleFormatYear.format(tripEnd);
                }
            } else {
                startDate = simpleFormatYear.format(tripStart);
                endDate = simpleFormatYear.format(tripEnd);
                if (tripEnd.getYear() == tripStart.getYear()) {
                    startDate = simpleFormatMonth.format(tripStart);
                }
            }
            dates.setText(startDate + " - " + endDate);
        }
    }

    public void setTrips(List<MyTrip> sliderItems) {
        this.sliderItems = sliderItems;
        notifyDataSetChanged();

    }

}
