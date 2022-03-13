package com.ikea.myapp.UI.editTrip;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.getCorrectDate;

public class OverviewFragment extends Fragment {
    private TextView dates;
    private MyTrip trip;
    private ViewModel viewModel;

    public OverviewFragment(MyTrip trip) {
        this.trip = trip;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        dates = view.findViewById(R.id.editTrip_dates);
        viewModel = ViewModelProviders.of(this).get(EditTripViewModel.class);

        getCorrectDate date = new getCorrectDate(trip);
        dates.setText(date.getStartDateUpcomingFormat() + getResources().getString(R.string.ui_dash) + date.getEndDateUpcomingFormat());
        return view;
    }
}