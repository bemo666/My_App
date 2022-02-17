package com.ikea.myapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.ikea.myapp.Adapters.SliderAdapter;
import com.ikea.myapp.Adapters.TripDetailsAdapter;
import com.ikea.myapp.Managers.FirebaseRequestManager;
import com.ikea.myapp.R;
import com.ikea.myapp.ViewModels.UpcomingFragmentViewModel;

import java.util.ArrayList;


public class UpcomingFragment extends Fragment {

    //Variables
    private ArrayList<String> mNames = new ArrayList<String>();
    private RecyclerView rv_details;
    private CardView extraIcon, welcomeCard;
    private RelativeLayout planBar;
    private MaterialButton createTrip;
    private ViewPager2 trips;
    private ShimmerFrameLayout trip_shimmer, details_shimmer;
    private final Handler handler = new Handler();
    private UpcomingFragmentViewModel viewmodel;
    private SliderAdapter adapter;
    private boolean first;

    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        adapter = new SliderAdapter(this);
        trips = view.findViewById(R.id.trips);
        planBar = view.findViewById(R.id.planBar);
        rv_details = view.findViewById(R.id.rvTripDetails);
        extraIcon = view.findViewById(R.id.edit_cardview);
        trip_shimmer = view.findViewById(R.id.shimmer_view_container_trip);
        details_shimmer = view.findViewById(R.id.shimmer_view_container_trip_details);
        createTrip = view.findViewById(R.id.create_trip);
        welcomeCard = view.findViewById(R.id.welcomeCard);
        viewmodel = new ViewModelProvider(requireActivity()).get(UpcomingFragmentViewModel.class);
        //Log.d("tag", "OnCreate2");

        extraIcon.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), EditTripActivity.class)));
        if (!FirebaseRequestManager.loggedIn()) {
            showWelcomeCard();
            //Log.d("tag", "notLoggedIn");

        } else {
            trips.setVisibility(View.GONE);
            rv_details.setVisibility(View.GONE);
            //Log.d("tag", "LoggedIn");

        }
        tripDetailsInit();

        sliderInit();
        return view;
    }

    private void sliderInit() {
        trips.setOnClickListener(view -> {

            Toast.makeText(getContext(), trips.getCurrentItem() + "", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getContext(), EditTripActivity.class);
            //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getContext(), trips.getImage());
        });
        trips.setAdapter(adapter);
        trips.setClipToPadding(false);
        trips.setClipChildren(false);
        trips.setOffscreenPageLimit(3);
        trips.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        trips.setNestedScrollingEnabled(true);

        first = true;

        viewmodel.getToast().observe(getViewLifecycleOwner(), s -> {
//            if (first)
//                first = false;
//            else
                if (s != null) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                }
        });
//        Log.d("tag", "observer created");
        viewmodel.getTrips().observe(getViewLifecycleOwner(), myTrips -> {
            if (myTrips != null) {

                trip_shimmer.setVisibility(View.GONE);

                if (!myTrips.isEmpty()) {
                    trips.setVisibility(View.VISIBLE);
                    hideWelcomeCard();
                    adapter.setSliderItems(myTrips);
//                    Log.d("tag", "vm hiding");
                } else {
                    showWelcomeCard();
                    Log.d("tag", "vm showing");

                }
            }
        });


        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        trips.setPageTransformer(compositePageTransformer);
        trips.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                details_shimmer.setVisibility(View.VISIBLE);
                rv_details.setVisibility(View.GONE);
                handler.postDelayed(() -> {
                    details_shimmer.setVisibility(View.GONE);
                    rv_details.setVisibility(View.VISIBLE);
                }, 100);
            }
        });

    }

    private void showWelcomeCard() {
        trips.setVisibility(View.GONE);
        trip_shimmer.setVisibility(View.GONE);
        welcomeCard.setVisibility(View.VISIBLE);
        planBar.setVisibility(View.GONE);
        rv_details.setVisibility(View.GONE);
        createTrip.setOnClickListener(v -> startActivity(new Intent(getActivity(), NewTripActivity.class)));
    }

    private void hideWelcomeCard() {
        welcomeCard.setVisibility(View.GONE);
        planBar.setVisibility(View.VISIBLE);
    }

    private void tripDetailsInit() {
        //Setting trip details
        TripDetailsAdapter adapter2 = new TripDetailsAdapter(requireContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rv_details.setAdapter(adapter2);
        rv_details.setLayoutManager(layoutManager2);
    }

    public void goToEditTripActivity(ImageView imageView) {
        Intent intent = new Intent(getContext(), EditTripActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imageView, ViewCompat.getTransitionName(imageView));
        this.startActivity(intent, options.toBundle());
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d("tag", "onPause");

    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("tag", "onResume");
    }
}