package com.ikea.myapp.UI.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class PastFragment extends Fragment {

    //Variables
    private TripsViewModel viewmodel;
    private RecyclerView tripSlider;
    private List<MyTrip> pastTripList, totalTripList;
    private CardView noPastTrips;
    private TextView countriesNum, countriesText, tripsNum, tripsText;


    public PastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_past, container, false);
        tripSlider = view.findViewById(R.id.past_trips_rv);
        noPastTrips = view.findViewById(R.id.past_no_trips_card);
        countriesNum = view.findViewById(R.id.past_countries_num);
        countriesText = view.findViewById(R.id.past_countries_text);
        tripsNum = view.findViewById(R.id.past_trips_num);
        tripsText = view.findViewById(R.id.past_trips_text);
        viewmodel = ViewModelProviders.of(requireActivity()).get(TripsViewModel.class);


        PastTripsRVAdapter adapter = new PastTripsRVAdapter(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tripSlider.setAdapter(adapter);
        tripSlider.setLayoutManager(layoutManager2);
        viewmodel.getTrips().observe(getViewLifecycleOwner(), myTrips -> {
            if (myTrips != null) {
                if (!myTrips.isEmpty()) {
                    pastTripList = new ArrayList<>();
                    totalTripList = new ArrayList<>();
                    for (MyTrip t : myTrips) {
                        long endStamp = t.getEndStamp();
                        TimeZone tz =  TimeZone.getDefault();
                        long currentTime = Calendar.getInstance().getTimeInMillis() + tz.getOffset(Calendar.getInstance().getTimeInMillis());
                        if (endStamp < currentTime) {
                            pastTripList.add(t);
                        }
                        totalTripList.add(t);
                    }
                    adapter.setTrips(pastTripList);
                }
            }
            displayCorrectTrips();
            showStats();
        });

        return view;
    }

    private void showStats() {
        List<String> lst = new ArrayList<>();
        if (totalTripList != null) {
            for (MyTrip t : totalTripList) {
                if (!lst.contains(t.getCountry())) {
                    lst.add(t.getCountry());
                }
            }
        }
        countriesNum.setText(String.valueOf(lst.size()));
        if (lst.size() == 1) {
            countriesText.setText("Country");
        } else {
            countriesText.setText("Countries");
        }
        tripsNum.setText(String.valueOf(totalTripList.size()));
        if (totalTripList.size() == 1) {
            tripsText.setText("Trip");
        } else {
            tripsText.setText("Trips");
        }
    }

    private void displayCorrectTrips() {
        if (pastTripList == null) {
            pastTripList = new ArrayList<>();
        }
        if (pastTripList.size() == 0) {

            tripSlider.setVisibility(View.GONE);
            noPastTrips.setVisibility(View.VISIBLE);
        } else {
            tripSlider.setVisibility(View.VISIBLE);
            noPastTrips.setVisibility(View.GONE);
        }
    }

    private String getTripIdAt(int position) {
        return pastTripList.get(position).getId();
    }

    public void goToEditTripActivity(ImageView imageView, TextView textView, int position) {
        Intent intent = new Intent(getContext(), EditTripActivity.class);
        intent.putExtra("id", viewmodel.getTripIdAt(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                Pair.create(imageView, ViewCompat.getTransitionName(imageView)),
                Pair.create(textView, ViewCompat.getTransitionName(textView)));
        getActivity().startPostponedEnterTransition();
        this.startActivity(intent, options.toBundle());

    }

}