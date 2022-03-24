package com.ikea.myapp.UI.editTrip;

import static com.ikea.myapp.PlanHeader.NOTE;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.PlanHeader;
import com.ikea.myapp.PlanNote;
import com.ikea.myapp.R;
import com.ikea.myapp.getCorrectDate;

public class ItineraryFragment extends Fragment {
    private TextView dates;
    private MyTrip trip;
    private ViewModel viewModel;
    private RecyclerView itineraryRV;
    private ItineraryRVAdapter rvAdapter;

    public ItineraryFragment(MyTrip trip) {
        this.trip = trip;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);
        dates = view.findViewById(R.id.editTrip_dates);
        itineraryRV = view.findViewById(R.id.itinerary_recycler_view);

        viewModel = ViewModelProviders.of(this).get(EditTripViewModel.class);

        rvAdapter = new ItineraryRVAdapter(this);
        rvAdapter.setList();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        itineraryRV.setAdapter(rvAdapter);
        itineraryRV.setLayoutManager(layoutManager2);

        getCorrectDate date = new getCorrectDate(trip);
        dates.setText(date.getStartDateUpcomingFormat() + getResources().getString(R.string.ui_dash) + date.getEndDateUpcomingFormat());
        return view;
    }

    public void checkHeader(int type){
        if (trip.hasPlanHeaders()){
            boolean has = false;
            for (PlanHeader h: trip.getPlanHeaders()) {
                if(h.getPlanType() == type){
                    has = true;
                    Log.d("tag", "breaking out ");
                }
                Log.d("tag", "still inside ");
            }
            if (!has){
                addHeader(type);
            }

        }
        addHeader(type);
    }

    private void addHeader(int type) {
        trip.addPlanHeader(new PlanHeader(type));
        switch(type){
            case NOTE:
                trip.getPlanHeaders().get(trip.getPlanHeaders().size()-1).addNotes(new PlanNote());
        }
    }
}