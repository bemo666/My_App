package com.ikea.myapp.UI.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.jjoe64.graphview.GraphView;

import java.util.List;


public class PastFragment extends Fragment {

    //Variables
    private TripsViewModel viewmodel;
    private RecyclerView tripSlider;
    private GraphView tripHistoryBarGraph;
    private List<MyTrip> trips;


    public PastFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past, container, false);
        tripSlider = view.findViewById(R.id.past_trips_rv);
        tripHistoryBarGraph = view.findViewById(R.id.trip_history_graph);
        viewmodel = ViewModelProviders.of(requireActivity()).get(TripsViewModel.class);


        PastTripsRVAdapter adapter = new PastTripsRVAdapter(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tripSlider.setAdapter(adapter);
        tripSlider.setLayoutManager(layoutManager2);
        viewmodel.getTrips().observe(getViewLifecycleOwner(), myTrips -> {
            if (myTrips != null) {
                if (!myTrips.isEmpty()) {
                    trips = myTrips;
                    adapter.setTrips(trips);
                }
            }
        });

//        Date d1 = new Date(FirebaseManager.getCreationStamp());
//        Date d2 = new Date(Calendar.getInstance().getTimeInMillis());
//        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
//                new DataPoint(d1, 5),
//                new DataPoint(d2, 8)
//        });
//        tripHistoryBarGraph.addSeries(series);
//
//// styling
//        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
//            @Override
//            public int get(DataPoint data) {
//                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
//            }
//        });
//
//        series.setSpacing(30);
//
//// draw values on top
//
////        series.setDrawValuesOnTop(true);
////        series.setValuesOnTopColor(ContextCompat.getColor(getContext(), R.color.darkGrey));
////series.setValuesOnTopSize(50);
//// set date label formatter
//        tripHistoryBarGraph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
//        tripHistoryBarGraph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
//
//// set manual x bounds to have nice steps
//        tripHistoryBarGraph.getViewport().setMinX(d1.getTime());
//        tripHistoryBarGraph.getViewport().setMaxX(d2.getTime());
//        tripHistoryBarGraph.getViewport().setXAxisBoundsManual(true);
//
//// as we use dates as labels, the human rounding to nice readable numbers
//// is not necessary
//        tripHistoryBarGraph.getGridLabelRenderer().setHumanRounding(false);

        return view;
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