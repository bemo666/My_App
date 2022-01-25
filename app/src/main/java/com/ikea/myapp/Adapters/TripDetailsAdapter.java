package com.ikea.myapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.R;

import java.util.ArrayList;

public class TripDetailsAdapter extends RecyclerView.Adapter<TripDetailsAdapter.ViewHolder> {

    ArrayList<String> sampleText;
    Context context;

    public TripDetailsAdapter(Context context) {
        this.sampleText = new ArrayList<>();
        sampleText.add("Cool Place");
        sampleText.add("Even Cooler place");
        sampleText.add("Didn't know it could get this cool");
        sampleText.add("Not that cool");
        this.context = context;
    }

    @NonNull
    @Override
    public TripDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_trip_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TripDetailsAdapter.ViewHolder holder, int position) {
        holder.placeName.setText(sampleText.get(position));
    }

    @Override
    public int getItemCount() {
        return sampleText.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView dates, placeName, stayName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img2);
            dates = itemView.findViewById(R.id.dates2);
            placeName = itemView.findViewById(R.id.place_name);
            stayName = itemView.findViewById(R.id.stayName);
        }
    }
}
