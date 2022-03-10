package com.ikea.myapp.UI.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
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
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.UI.newTrip.NewTripActivity;


public class UpcomingFragment extends Fragment {

    //Variables
    private RecyclerView rv_details;
    private CardView extraIcon, welcomeCard;
    private RelativeLayout planBar;
    private MaterialButton createTrip;
    private ViewPager2 tripSlider;
    private ShimmerFrameLayout trip_shimmer, details_shimmer;
    private TripsViewModel viewmodel;
    private UpcomingTripsRVAdapter adapter;
    private String receivedId = null;

    public UpcomingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        adapter = new UpcomingTripsRVAdapter(this);
        tripSlider = view.findViewById(R.id.current_trips_rv);
        planBar = view.findViewById(R.id.planBar);
        rv_details = view.findViewById(R.id.rvTripDetails);
        extraIcon = view.findViewById(R.id.edit_cardview);
        trip_shimmer = view.findViewById(R.id.shimmer_view_container_trip);
        details_shimmer = view.findViewById(R.id.shimmer_view_container_trip_details);
        createTrip = view.findViewById(R.id.create_trip);
        welcomeCard = view.findViewById(R.id.welcomeCard);
        viewmodel = new ViewModelProvider(requireActivity()).get(TripsViewModel.class);

        extraIcon.setOnClickListener(view1 -> {
        });

        tripDetailsInit();

        sliderInit();

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void sliderInit() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        tripSlider.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getX() < width / 2) {
                tripSlider.setCurrentItem(tripSlider.getCurrentItem() - 1);
            } else if (motionEvent.getX() > width / 2) {
                tripSlider.setCurrentItem(tripSlider.getCurrentItem() + 1);
            }
            return false;
        });

        tripSlider.setAdapter(adapter);
        tripSlider.setClipToPadding(false);
        tripSlider.setClipChildren(false);
        tripSlider.setOffscreenPageLimit(3);
        tripSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        tripSlider.setNestedScrollingEnabled(true);


        viewmodel.getTrips().observe(getViewLifecycleOwner(), myTrips -> {
            if (myTrips != null) {
                trip_shimmer.setVisibility(View.GONE);
                if (myTrips.getErrorMessage() != null) {
                    Toast.makeText(requireContext(), myTrips.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    myTrips.setErrorMessage(null);
                }
                if (!myTrips.getTrips().isEmpty()) {
                    hideWelcomeCard();
                    adapter.setTrips(myTrips.getTrips());
                } else {
                    showWelcomeCard();
                }
            } else {
                showWelcomeCard();
            }
        });

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        tripSlider.setPageTransformer(compositePageTransformer);
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
        tripSlider.setVisibility(View.GONE);
        trip_shimmer.setVisibility(View.GONE);
        welcomeCard.setVisibility(View.VISIBLE);
        planBar.setVisibility(View.GONE);
        rv_details.setVisibility(View.GONE);
        createTrip.setOnClickListener(v -> startActivityForResult(new Intent(getActivity(), NewTripActivity.class), 200));
    }

    private void hideWelcomeCard() {
        welcomeCard.setVisibility(View.GONE);
        planBar.setVisibility(View.VISIBLE);
        tripSlider.setVisibility(View.VISIBLE);
        rv_details.setVisibility(View.VISIBLE);
    }

    private void tripDetailsInit() {
        //Setting trip details
        UpcomingTripDetailsRVAdapter adapter2 = new UpcomingTripDetailsRVAdapter(requireContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rv_details.setAdapter(adapter2);
        rv_details.setLayoutManager(layoutManager2);
    }

    public void goToEditTripActivity(ImageView imageView, TextView textView, CardView cardView, int position) {
        Intent intent = new Intent(getContext(), EditTripActivity.class);
        intent.putExtra("trip", viewmodel.getTripAt(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                Pair.create(imageView, ViewCompat.getTransitionName(imageView)),
                Pair.create(textView, ViewCompat.getTransitionName(textView))
//                ,Pair.create(cardView, ViewCompat.getTransitionName(cardView))
        );
        this.startActivity(intent, options.toBundle());

    }


    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 200 && resultCode == RESULT_OK)
//            receivedId = data.getStringExtra("id");
//        Log.d("tag", "onActivityResult");
//        if (receivedId != null) {
//            int i;
//            Log.d("tag", "childrencount: " + tripSlider.getChildCount());
//            Log.d("tag", "childid: " + tripSlider.getChildAt(0).getId());
//            for (i = 0; i < tripSlider.getChildCount(); i++) {
////                if(tripSlider.getChildAt(i)  == receivedId)
////                    break;
//            }
//            //tripSlider.setCurrentItem(i);
//            receivedId = null;
//
//        }
//    }
}