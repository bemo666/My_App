package com.ikea.myapp.UI.editTrip;

import static com.ikea.myapp.models.PlanHeader.FLIGHT;
import static com.ikea.myapp.models.PlanHeader.HOTEL;
import static com.ikea.myapp.models.PlanHeader.NOTE;
import static com.ikea.myapp.models.PlanHeader.ACTIVITY;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.PlanActivity;
import com.ikea.myapp.models.PlanFlight;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanHotel;
import com.ikea.myapp.models.PlanNote;
import com.ikea.myapp.models.PlanRental;
import com.ikea.myapp.R;
import com.ikea.myapp.utils.getCorrectDate;

public class ItineraryFragment extends Fragment {
    private TextView dates;
    private String id;
    private MyTrip trip;
    private EditTripViewModel viewModel;
    private RecyclerView itineraryRV;
    private ItineraryRVAdapter rvAdapter;

    public ItineraryFragment(String id) {
        Log.d("tag", "Trip Item Received");
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);
        dates = view.findViewById(R.id.editTrip_dates);
        itineraryRV = view.findViewById(R.id.itinerary_recycler_view);

        rvAdapter = new ItineraryRVAdapter(this);
        itineraryRV.setAdapter(rvAdapter);
        itineraryRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = ViewModelProviders.of(requireActivity()).get(EditTripViewModel.class);
        viewModel.getTrip().observe(getViewLifecycleOwner(), myTrip -> {
            if (myTrip != null) { // if null trip deleted todo - handle it
                trip = myTrip;
                updateData();
            }
        });

        return view;
    }

    private void updateData() {
        rvAdapter.setList(trip);
        getCorrectDate date = new getCorrectDate(trip);
        dates.setText(date.getStartDateUpcomingFormat() + getResources().getString(R.string.ui_dash) + date.getEndDateUpcomingFormat());
    }

    public void checkForAddHeader(int type) {
        if (trip != null && trip.hasPlanHeaders()) {
            boolean has = false;
            for (PlanHeader h : trip.getPlanHeaders()) {
                if (h.getObjectType() == type) {
                    has = true;
                    addType(h, type);
                    Log.d("tag", "Adding Note ");
                }
            }
            if (!has) {
                Log.d("tag", "Adding Header cause my header doesn't exist");
                addHeader(type);
            }
        } else {
            Log.d("tag", "Adding Header cause no headers exist ");
            addHeader(type);
        }
    }

    private void addHeader(int type) {
        trip.addPlanHeader(new PlanHeader(type));
        addType(trip.getPlanHeaders().get(trip.getPlanHeaders().size() - 1), type);
    }

    private void addType(PlanHeader header, int type) {
        switch (type) {
            case NOTE:
                trip.getPlanHeaders().get(trip.getPlanHeaders().indexOf(header)).addObject(new PlanNote());
                break;
            case HOTEL:
                trip.getPlanHeaders().get(trip.getPlanHeaders().indexOf(header)).addObject(new PlanHotel());
                break;
            case PlanHeader.RENTAL:
                trip.getPlanHeaders().get(trip.getPlanHeaders().indexOf(header)).addObject(new PlanRental());
                break;
            case FLIGHT:
                trip.getPlanHeaders().get(trip.getPlanHeaders().indexOf(header) ).addObject(new PlanFlight());
                break;
            case ACTIVITY:
                trip.getPlanHeaders().get(trip.getPlanHeaders().indexOf(header)).addObject(new PlanActivity());
                break;
        }
        rvAdapter.setList(trip);
        viewModel.updateTrip(trip);
    }

    public void updateTrip(MyTrip trip) {
        this.trip = trip;
        rvAdapter.setList(trip);
        viewModel.updateTrip(trip);

    }

    private Object getRightObject(Object ob, Integer type) {
        if (type == null) {
            if (ob.getClass() == PlanNote.class) {
                type = NOTE;
            }
        }
        switch (type) {
            case NOTE:
                return new PlanNote();
            case HOTEL:
                return new PlanHotel();
            case PlanHeader.RENTAL:
                return new PlanRental();
            case FLIGHT:
                return new PlanFlight();
            case ACTIVITY:
                return new PlanActivity();
            default:
                return null;
        }
    }
}