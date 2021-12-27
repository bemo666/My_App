package com.ikea.myapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;


public class UpcomingFragment<recyclerViewInit> extends Fragment {

    //Variables
    private ArrayList<String> mNames = new ArrayList<String>();
    RecyclerView rv_trips, rv_details;
    RecyclerViewAdapter adapter;


    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_upcoming, container, false);
         rv_trips=  view.findViewById(R.id.rvTrips);
         rv_details = view.findViewById(R.id.rvTripDetails);
        recyclerViewInit();

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(rv_trips);
        return view;
    }

    public void recyclerViewInit(){
        mNames.add("London");
        mNames.add("Australia");
        mNames.add("Amsterdam");
        mNames.add("Russia");
        adapter = new RecyclerViewAdapter(mNames, requireContext());
        rv_trips.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        rv_trips.setLayoutManager(layoutManager);

        TripDetailsAdapter adapter2 = new TripDetailsAdapter(requireContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rv_details.setAdapter(adapter2);
        rv_details.setLayoutManager(layoutManager2);



    }
}