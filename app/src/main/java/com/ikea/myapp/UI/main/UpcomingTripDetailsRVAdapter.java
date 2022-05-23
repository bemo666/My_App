package com.ikea.myapp.UI.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
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
import com.ikea.myapp.models.PlanType;
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

    private List<SubPlan> plans, original;
    private final UpcomingFragment upcomingFragment;
    private final Context context;
    private final DateFormat date = new SimpleDateFormat("EEE, MMM dd, yyyy");
    private final DateFormat time = new SimpleDateFormat("HH:mm");

    public UpcomingTripDetailsRVAdapter(UpcomingFragment upcomingFragment) {
        this.upcomingFragment = upcomingFragment;
        this.context = upcomingFragment.requireContext();
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
            holder.itemView.setOnClickListener(view -> {
                upcomingFragment.goToEditTripActivity(upcomingFragment.sliderPos, mPlan.getId());
            });
            //hotel, rental, flight, activity

            if (mPlan.getType().getType() == 1) {
                int color = ContextCompat.getColor(context, R.color.purple);
                holder.iconLayout.setCardBackgroundColor(color);
                holder.line.setCardBackgroundColor(color);
                holder.icon.setImageResource(R.drawable.ic_bed_side);
                String text = (mPlan.getNote() != null ? mPlan.getNote() + " - " : "") + (mPlan.getConfirmationNumber() != null ? mPlan.getConfirmationNumber() : "");
                if (!text.equals("")) {
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.isStart())
                    holder.name.setText("Check in: " + mPlan.getLocation());
                else
                    holder.name.setText("Check out: " + mPlan.getLocation());
            } else if (mPlan.getType().getType() == 2) {
                int color = ContextCompat.getColor(context, R.color.green);
                holder.iconLayout.setCardBackgroundColor(color);
                holder.line.setCardBackgroundColor(color);
                holder.icon.setImageResource(R.drawable.ic_car);
                String text = (mPlan.getNote() != null ? mPlan.getNote() + " - " : "") + (mPlan.getConfirmationNumber() != null ? mPlan.getConfirmationNumber() : "");
                if (!text.equals("")) {
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.isStart())
                    holder.name.setText("Pick up: " + mPlan.getLocation());
                else
                    holder.name.setText("Drop off: " + mPlan.getLocation());
            } else if (mPlan.getType().getType() == 3) {
                int color = ContextCompat.getColor(context, R.color.orange);
                holder.iconLayout.setCardBackgroundColor(color);
                holder.line.setCardBackgroundColor(color);
                holder.icon.setImageResource(R.drawable.ic_airplane);
                String text = (mPlan.getAirline() != null ? mPlan.getAirline() + " - " : "") + (mPlan.getFlightCode() != null ? mPlan.getFlightCode() + " - " : "") + (mPlan.getConfirmationNumber() != null ? mPlan.getConfirmationNumber() : "");
                if (!text.equals("")) {
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.isStart())
                    holder.name.setText("Departure: " + mPlan.getLocation());
                else
                    holder.name.setText("Arrival: " + mPlan.getLocation());
            } else if (mPlan.getType().getType() == 4) {
                int color = ContextCompat.getColor(context, R.color.blue);
                holder.iconLayout.setCardBackgroundColor(color);
                holder.line.setCardBackgroundColor(color);
                holder.icon.setImageResource(R.drawable.ic_man);
                String text = (mPlan.getNote() != null ? mPlan.getNote() + " - " : "") + (mPlan.getConfirmationNumber() != null ? mPlan.getConfirmationNumber() : "");
                if (!text.equals("")) {
                    holder.details.setText(text);
                    holder.details.setVisibility(View.VISIBLE);
                }
                if (mPlan.getTime() != null) {
                    holder.time.setText(time.format(new Date(mPlan.getTime())) + (mPlan.getEndtime() != null ? "\n     -\n" + time.format(new Date(mPlan.getEndtime())) : ""));
                } else
                    holder.time.setText(R.string.trip_details_tbd);

            }


            holder.address.setText(mPlan.getLocationAddress());
            holder.address.setPaintFlags(holder.address.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.address.setOnClickListener(view -> {
                String uri = String.format(Locale.ENGLISH, "geo:%1$s,%2$s?q=%1$s,%2$s", mPlan.getLatitude(), mPlan.getLongitude());
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
                    PlanType type = PlanType.getTypeByInt(p.getObjectType());
                    if (p.getStartDate() != null) {
                        SubPlan tmp = new SubPlan(p.getId(), p.getAirline(), p.getFlightCode(), p.getStartLocationLat(), p.getStartLocationLong(), p.getStartLocation(), p.getStartLocationId(), p.getStartLocationAddress(), p.getStartTime(), p.getStartDate(), p.getEndTime(), p.getNote(), p.getConfirmationNumber(), true, type);
                        this.plans.add(tmp);
                    }
                    if (p.getEndDate() != null) {
                        SubPlan tmp;
                        if (p.getObjectType() != 4) {
                            if (p.getEndLocation() == null && p.getStartLocation() != null) {
                                tmp = new SubPlan(p.getId(), p.getAirline(), p.getFlightCode(), p.getStartLocationLat(), p.getStartLocationLong(), p.getStartLocation(), p.getStartLocationId(), p.getStartLocationAddress(), p.getEndTime(), p.getEndDate(), null, p.getNote(), p.getConfirmationNumber(), false, type);
                            } else {
                                tmp = new SubPlan(p.getId(), p.getAirline(), p.getFlightCode(), p.getEndLocationLat(), p.getEndLocationLong(), p.getEndLocation(), p.getEndLocationId(), p.getEndLocationAddress(), p.getEndTime(), p.getEndDate(), null, p.getNote(), p.getConfirmationNumber(), false, type);
                            }
                            this.plans.add(tmp);
                        }
                    }
                }
            }
        }
        sortList();
        notifyDataSetChanged();
        original = new ArrayList<>(this.plans);

    }

    List<SubPlan> bubbleSort(List<SubPlan> list) {
        int n = list.size();
        if (n > 0) {
            for (int i = 0; i < n - 1; i++)
                for (int j = 0; j < n - i - 1; j++)
                    if (list.get(j) != null)
                        if (list.get(j).getDate() != null)
                            if (list.get(j).getDate() > list.get(j + 1).getDate()) {
                                SubPlan temp = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, temp);
                            }
        }
        return list;
    }

    List<SubPlan> bubbleSortByTime(List<SubPlan> list) {
        int n = list.size();
        if (n > 0) {
            for (int i = 0; i < n - 1; i++)
                for (int j = 0; j < n - i - 1; j++)
                    if (list.get(j) != null)
                        if (list.get(j).getTime() != null)
                            if (list.get(j).getTime() > list.get(j + 1).getTime()) {
                                SubPlan temp = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, temp);
                            }
        }
        return list;
    }

    void addNulls() {
        if (!plans.isEmpty()) {
            plans.add(0, null);
            notifyItemInserted(0);
            for (int i = 1; i < plans.size(); i++) {
                if (plans.get(i) != null && plans.get(i - 1) != null)
                    if (!Objects.equals(plans.get(i).getDate(), plans.get(i - 1).getDate())) {
                        plans.add(i, null);
                        notifyItemInserted(i);
                    }
            }
        }

    }

    void sortList() {
        plans = bubbleSort(plans);

        //adding nulls (dates in recyclerview)
        addNulls();

        //sort plans by time
        ArrayList<SubPlan> newList = new ArrayList<>();
        boolean lastNull = true;
        for (int i = 0; i < plans.size(); i++) {
            if (plans.get(i) == null) {
                newList.add(null);
                for (int j = i + 1; j < plans.size(); j++) {
                    if (plans.get(j) == null) {
                        if (j - i == 2) {
                            newList.add(plans.get(i + 1));
                        } else {
                            newList.addAll(bubbleSortByTime(plans.subList(i + 1, j)));
                        }
                        lastNull = false;
                        break;
                    } else {
                        lastNull = true;
                    }
                }

                if (lastNull)
                    newList.addAll(bubbleSortByTime(plans.subList(i + 1, plans.size())));

            }
        }
        plans = newList;

    }


    public void search(String searchText) {
        plans = new ArrayList<>(original);
        notifyDataSetChanged();

        for (int i = 0; i < plans.size(); i++)
            if (plans.get(i) == null) {
                notifyItemRemoved(i);
                plans.remove(i);
            }

        for (int i = 0; i < plans.size(); i++) {
            SubPlan tmp = plans.get(i);
            DateFormat formatter = new SimpleDateFormat("MMM dd");
            DateFormat time = new SimpleDateFormat("HH:mm  HHmm");
            time.setTimeZone(TimeZone.getTimeZone("GMT"));
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            String t = "";
            t += formatter.format(new Date(tmp.getDate())).toLowerCase(Locale.ROOT) + " ";

            if (tmp.getTime() != null) {
                t += time.format(new Date(tmp.getTime()));
                t += " ";
            }
            if (tmp.getEndtime() != null)
                t += time.format(new Date(tmp.getEndtime()));

            if (tmp.getLocation().toLowerCase().contains(searchText.toLowerCase()) ||
                    tmp.getAirline() != null && tmp.getAirline().toLowerCase().contains(searchText.toLowerCase()) ||
                    tmp.getFlightCode() != null && tmp.getFlightCode().toLowerCase().contains(searchText.toLowerCase()) ||
                    tmp.getNote() != null && tmp.getNote().toLowerCase().contains(searchText.toLowerCase()) ||
                    tmp.getConfirmationNumber() != null && tmp.getConfirmationNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                    tmp.getLocationAddress() != null && tmp.getLocationAddress().toLowerCase().contains(searchText.toLowerCase()) ||
                    t.contains(searchText.toLowerCase(Locale.ROOT)) ||
                    tmp.getType().getStartText() != null && tmp.getType().getStartText().toLowerCase().contains(searchText.toLowerCase()) && tmp.isStart() ||
                    tmp.getType().getEndText() != null && tmp.getType().getEndText().toLowerCase().contains(searchText.toLowerCase()) && !tmp.isStart()) {

                continue;
            } else {
                notifyItemRemoved(i);
                plans.remove(i);
                i--;
            }
        }

        addNulls();

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
