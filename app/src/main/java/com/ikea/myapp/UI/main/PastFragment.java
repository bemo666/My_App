package com.ikea.myapp.UI.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;


public class PastFragment extends Fragment {

    //Variables
    private TripsViewModel viewmodel;
    private RecyclerView tripSlider;


    public PastFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past, container, false);
        tripSlider = view.findViewById(R.id.past_trips_rv);
        viewmodel = ViewModelProviders.of(requireActivity()).get(TripsViewModel.class);


        PastTripsRVAdapter adapter = new PastTripsRVAdapter(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tripSlider.setAdapter(adapter);
        tripSlider.setLayoutManager(layoutManager2);
        viewmodel.getTrips().observe(getViewLifecycleOwner(), myTrips -> {
            if (myTrips != null) {
                if (myTrips.getErrorMessage() != null) {
                    Toast.makeText(requireContext(), myTrips.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    myTrips.setErrorMessage(null);
                }
                if (!myTrips.getTrips().isEmpty()) {
                    adapter.setTrips(myTrips.getTrips());
                }
            }
        });


        return view;
    }

    public void goToEditTripActivity(ImageView imageView, TextView textView, int position) {
        Intent intent = new Intent(getContext(), EditTripActivity.class);
        intent.putExtra("trip", viewmodel.getTripAt(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                Pair.create(imageView, ViewCompat.getTransitionName(imageView)),
                Pair.create(textView, ViewCompat.getTransitionName(textView)));
        getActivity().startPostponedEnterTransition();
        this.startActivity(intent, options.toBundle());

    }

}