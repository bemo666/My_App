package com.ikea.myapp.UI.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.R;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.SubPlan;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class UpcomingTripDetailsRVAdapter extends RecyclerView.Adapter<UpcomingTripDetailsRVAdapter.ViewHolder> {

    private List<SubPlan> plans;
    private Context context;
    private DateFormat date = new SimpleDateFormat("EEE, MMM dd, yyyy");
    private DateFormat time = new SimpleDateFormat("HH:mm");

    public UpcomingTripDetailsRVAdapter(Context context) {
        this.context = context;
        this.plans = new ArrayList<>();
    }

    @NonNull
    @Override
    public UpcomingTripDetailsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_trip_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingTripDetailsRVAdapter.ViewHolder holder, int position) {
        SubPlan mPlan = plans.get(position);
        TimeZone tz = TimeZone.getTimeZone("GMT");
        time.setTimeZone(tz);
        if (mPlan != null) {
            if (mPlan.getTime() != null) {
                holder.time.setText(time.format(new Date(mPlan.getTime())));
            } else
                holder.time.setText(R.string.trip_details_tbd);

            holder.name.setText(mPlan.getLocation());
            //hotel, rental, flight, activity
            if (mPlan.getObjectType() == 1) {
//                int color = ContextCompat.getColor(context, R.color.green);
//                holder.iconLayout.setCardBackgroundColor(color);
//                holder.line.setCardBackgroundColor(color);
//                holder.icon.setImageResource(R.drawable.ic_bed_side);
                String text = (mPlan.getNote() != null?mPlan.getNote() + " - ":"") + (mPlan.getConfirmationNumber()!= null?mPlan.getConfirmationNumber(): "");
                if (!text.equals("")){
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.isStart())
                    holder.name.setText("Check in: " + mPlan.getLocation());
                else
                    holder.name.setText("Check out: " + mPlan.getLocation());
            } else if (mPlan.getObjectType() == 2) {
                holder.icon.setImageResource(R.drawable.ic_car);
                String text = (mPlan.getNote() != null?mPlan.getNote() + " - ":"") + (mPlan.getConfirmationNumber()!= null?mPlan.getConfirmationNumber(): "");
                if (!text.equals("")) {
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.isStart())
                    holder.name.setText("Pick up: " + mPlan.getLocation());
                else
                    holder.name.setText("Drop off: " + mPlan.getLocation());
            } else if (mPlan.getObjectType() == 3) {
                holder.icon.setImageResource(R.drawable.ic_airplane);
                String text = (mPlan.getAirline() != null?mPlan.getAirline() + " - ":"") + (mPlan.getFlightCode()!= null?mPlan.getFlightCode() + " - ": "") + (mPlan.getConfirmationNumber()!= null?mPlan.getConfirmationNumber(): "");
                if (!text.equals("")) {
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.isStart())
                    holder.name.setText("Departure: " + mPlan.getLocation());
                else
                    holder.name.setText("Arrival: " + mPlan.getLocation());
            } else if (mPlan.getObjectType() == 4) {
                holder.icon.setImageResource(R.drawable.ic_man);
                String text = (mPlan.getNote() != null?mPlan.getNote() + " - ":"") + (mPlan.getConfirmationNumber()!= null?mPlan.getConfirmationNumber(): "");
                if (!text.equals("")) {
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.getTime() != null) {
                    holder.time.setText(time.format(new Date(mPlan.getTime())) + (mPlan.getEndtime() != null? "\n     -\n" + time.format(new Date(mPlan.getEndtime())):""));
                } else
                    holder.time.setText(R.string.trip_details_tbd);

            }


            holder.address.setText(mPlan.getLocationAddress());
            holder.address.setPaintFlags(holder.address.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            holder.address.setOnClickListener(view -> {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", mPlan.getLatitude(), mPlan.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            });

            holder.noDateLayout.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.GONE);

        } else {
            holder.noDateLayout.setVisibility(View.GONE);
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(date.format(new Date(plans.get(position + 1).getDate())));
        }
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    public void setList(List<Plan> plans) {
        this.plans = new ArrayList<>();
        if (plans != null) {
            for (Plan p : plans) {
                if (p.getObjectType() != 0) {
                    if (p.getStartDate() != null) {
                        SubPlan tmp = new SubPlan(p.getName(), p.getAirline(), p.getFlightCode(), p.getStartLocationLat(), p.getStartLocationLong(), p.getStartLocation(), p.getStartLocationId(), p.getStartLocationAddress(), p.getStartTime(), (p.getEndTime() != null? p.getEndTime() : null), p.getStartDate(), p.getNote(), p.getConfirmationNumber(), p.getObjectType(), true);
                        this.plans.add(tmp);
                    }
                    if (p.getEndDate() != null) {
                        SubPlan tmp;
                        if(p.getObjectType() != 4) {
                            if (p.getEndLocation() == null && p.getStartLocation() != null) {
                                tmp = new SubPlan(p.getName(), p.getAirline(), p.getFlightCode(), p.getStartLocationLat(), p.getStartLocationLong(), p.getStartLocation(), p.getStartLocationId(), p.getStartLocationAddress(), p.getEndTime(), null, p.getEndDate(), p.getNote(), p.getConfirmationNumber(), p.getObjectType(), false);
                            } else {
                                tmp = new SubPlan(p.getName(), p.getAirline(), p.getFlightCode(), p.getEndLocationLat(), p.getEndLocationLong(), p.getEndLocation(), p.getEndLocationId(), p.getEndLocationAddress(), p.getEndTime(), (p.getEndTime() != null? p.getEndTime() : null), p.getEndDate(), p.getNote(), p.getConfirmationNumber(), p.getObjectType(), false);
                            }
                            this.plans.add(tmp);
                        }
                    }
                }
            }
        }
        this.plans = bubbleSort(this.plans);
        notifyDataSetChanged();

    }

    List<SubPlan> bubbleSort(List<SubPlan> arr) {
        int n = arr.size();
        for (int i = 0; i < n - 1; i++)
            for (int j = 0; j < n - i - 1; j++)
                if (arr.get(j).getDate() > arr.get(j + 1).getDate()) {
                    SubPlan temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }

        arr.add(0, null);
        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i) != null && arr.get(i - 1) != null)
                if (!Objects.equals(arr.get(i).getDate(), arr.get(i - 1).getDate())) {
                    arr.add(i, null);
                }
        }

        //do time sorting here

        return arr;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView date, name, address, time, details;
        RelativeLayout noDateLayout;
        CardView line, iconLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            details = itemView.findViewById(R.id.trip_detail_details);
            icon = itemView.findViewById(R.id.trip_detail_activity_ic);
            date = itemView.findViewById(R.id.trip_detail_date);
            name = itemView.findViewById(R.id.trip_detail_name);
            address = itemView.findViewById(R.id.trip_detail_address);
            time = itemView.findViewById(R.id.trip_detail_time);
            noDateLayout = itemView.findViewById(R.id.trip_detail_non_date);
            line = itemView.findViewById(R.id.trip_detail_line);
            iconLayout = itemView.findViewById(R.id.trip_detail_card_view);
        }


    }
}
