package com.ikea.myapp.UI.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.UI.newTrip.NewTripActivity;

import java.util.ArrayList;
import java.util.Objects;


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
    private Point touch;

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

        extraIcon.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), EditTripActivity.class)));

        tripDetailsInit();

        sliderInit();
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void sliderInit() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        trips.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getX() < 50 && trips.getCurrentItem() != 0) {
                trips.setCurrentItem(trips.getCurrentItem() - 1);
            } else if (motionEvent.getX() > width-50 && trips.getCurrentItem() != trips.getChildCount()) {
                trips.setCurrentItem(trips.getCurrentItem() + 1);

            }
            return false;
        });

        trips.setAdapter(adapter);
        trips.setClipToPadding(false);
        trips.setClipChildren(false);
        trips.setOffscreenPageLimit(3);
        trips.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        trips.setNestedScrollingEnabled(true);


        viewmodel.getTrips().observe(getViewLifecycleOwner(), myTrips -> {
            if (myTrips != null) {
                trip_shimmer.setVisibility(View.GONE);
                if (myTrips.getErrorMessage() != null) {
                    Toast.makeText(requireContext(), myTrips.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    myTrips.setErrorMessage(null);
                }
                if (!myTrips.getTrips().isEmpty()) {
                    Log.d("tag", "UpcomFrag, observe !mytrips.empty");
                    hideWelcomeCard();
                    adapter.setTrips(myTrips.getTrips());
                } else {
                    showWelcomeCard();
                    Log.d("tag", "UpcomFrag, observe mytrips.empty");
                }
            } else {
                Log.d("tag", "UpcomFrag, observe mytrips == null");
                showWelcomeCard();
            }
        });

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        trips.setPageTransformer(compositePageTransformer);
//        trips.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                details_shimmer.setVisibility(View.VISIBLE);
//                rv_details.setVisibility(View.GONE);
//                handler.postDelayed(() -> {
//                    details_shimmer.setVisibility(View.GONE);
//                    rv_details.setVisibility(View.VISIBLE);
//                }, 100);
//            }
//        });

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
        trips.setVisibility(View.VISIBLE);
        rv_details.setVisibility(View.VISIBLE);
    }

    private void tripDetailsInit() {
        //Setting trip details
        TripDetailsAdapter adapter2 = new TripDetailsAdapter(requireContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rv_details.setAdapter(adapter2);
        rv_details.setLayoutManager(layoutManager2);
    }

    public void goToEditTripActivity(ImageView imageView, int position) {
        Intent intent = new Intent(getContext(), EditTripActivity.class);
        intent.putExtra("trip", viewmodel.getTripAt(position));
        Log.d("tag", String.valueOf(position));
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

    public boolean onTouch(View v, MotionEvent event) {
        touch.x = (int) event.getX();
        touch.y = (int) event.getY();
        return true;
    }
}