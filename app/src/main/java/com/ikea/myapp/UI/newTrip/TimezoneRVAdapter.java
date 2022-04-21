package com.ikea.myapp.UI.newTrip;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.BudgetFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class TimezoneRVAdapter extends RecyclerView.Adapter<TimezoneRVAdapter.ViewHolder> {

    private final NewTripActivity activity;
    private List<String> timezones, searchList;


    public TimezoneRVAdapter(NewTripActivity activity) {
        this.activity = activity;
        timezones = Arrays.asList(TimeZone.getAvailableIDs());
        searchList = timezones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(activity.getApplicationContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String timezone = searchList.get(position);
        holder.text1.setText(timezone);
//        holder.itemView.setOnClickListener(view -> activity.setTimezone(timezone));

    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CheckedTextView text1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
        }
    }

    public void searchUpdate(String str) {
        if (str.isEmpty()) {
            searchList = timezones;
        } else {
            searchList = new ArrayList<>();
            for (String zone : timezones) {
                if (zone.toLowerCase().contains(str.toLowerCase())) {
                    searchList.add(zone);
                } }
        }
        notifyDataSetChanged();
    }
}

