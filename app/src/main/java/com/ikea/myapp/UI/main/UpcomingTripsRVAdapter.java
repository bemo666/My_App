package com.ikea.myapp.UI.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.utils.getCorrectDate;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;
import java.util.List;

public class UpcomingTripsRVAdapter extends RecyclerView.Adapter<UpcomingTripsRVAdapter.SliderViewHolder> {

    private List<MyTrip> sliderItems;
    private Context context;
    private final UpcomingFragment fragment;
    private boolean hasTrips = false;
    private CardView liveBadge, liveDot;

    public UpcomingTripsRVAdapter(UpcomingFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_trips, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setCardView(sliderItems.get(position));
        holder.itemView.setOnClickListener(view -> fragment.goToEditTripActivity(holder.imageView, holder.placeName, liveBadge, position));
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
            liveBadge = itemView.findViewById(R.id.live_badge);
            liveDot = itemView.findViewById(R.id.live_dot);

        }

        void setCardView(MyTrip trip) {
            imageView.setImageResource(R.drawable.im_london2);
            placeName.setText(trip.getDestination());
            getCorrectDate date = new getCorrectDate(trip);
            dates.setText(date.getStartDateUpcomingFormat() + context.getResources().getString(R.string.ui_dash) + date.getEndDateUpcomingFormat());
            if ((Long.valueOf(trip.getStartStamp()) < Calendar.getInstance().getTimeInMillis() &&
                    Long.valueOf(trip.getEndStamp()) > Calendar.getInstance().getTimeInMillis())) {
                liveBadge.setVisibility(View.VISIBLE);
                liveDot.startAnimation(AnimationUtils.loadAnimation(context, R.anim.blink));
            } else {
                liveDot.clearAnimation();
                liveBadge.setVisibility(View.GONE);
            }

//            try {
//                byte[] encodeByte = Base64.decode(trip.getImage(), Base64.DEFAULT);
//                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//                imageView.setImageBitmap(bitmap);
//            } catch (Exception e) {
//            }
        }


    }

    public void setTrips(List<MyTrip> sliderItems) {
        this.sliderItems = sliderItems;
        hasTrips = true;
        notifyDataSetChanged();

    }

    public boolean hasTrips() {
        return hasTrips;
    }

}
