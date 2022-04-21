package com.ikea.myapp.UI.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import com.google.android.material.button.MaterialButton;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.EditTripActivity;
import com.ikea.myapp.UI.newTrip.NewTripActivity;
import com.ikea.myapp.models.MyTrip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class UpcomingFragment extends Fragment {

    //Variables
    private RecyclerView rv_details;
    private CardView extraIcon, welcomeCard;
    private RelativeLayout planBar;
    private MaterialButton createTrip;
    private ViewPager2 tripSlider;
    private TripsViewModel viewmodel;
    private UpcomingTripsRVAdapter adapter;
    private int sliderPos;
    private boolean sliderPosChanged;
    private List<MyTrip> tripList;

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
        createTrip = view.findViewById(R.id.create_trip);
        welcomeCard = view.findViewById(R.id.welcomeCard);
        viewmodel = new ViewModelProvider(requireActivity()).get(TripsViewModel.class);

        extraIcon.setOnClickListener(view1 -> {
            adapter.openEditTrip();
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
                if (!myTrips.isEmpty()) {
                    tripList = new ArrayList<>();
                    for (MyTrip t : myTrips) {
                        long endStamp = t.getEndStamp();
                        TimeZone tz = TimeZone.getDefault();
                        long currentTime = Calendar.getInstance().getTimeInMillis() + tz.getOffset(Calendar.getInstance().getTimeInMillis());

                        if (endStamp >= currentTime) {
                            tripList.add(t);
                        }
                    }
                    adapter.setTrips(tripList);
                }
            }
            handleWelcomeCard();
        });

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        tripSlider.setPageTransformer(compositePageTransformer);

    }

    private void handleWelcomeCard() {
        if (tripList == null){
            tripList = new ArrayList<>();
        }
        if (tripList.size() == 0) {
            tripSlider.setVisibility(View.GONE);
            welcomeCard.setVisibility(View.VISIBLE);
            planBar.setVisibility(View.GONE);
            rv_details.setVisibility(View.GONE);
            createTrip.setOnClickListener(v -> startActivityForResult(new Intent(getActivity(), NewTripActivity.class), 200));
        } else {
            welcomeCard.setVisibility(View.GONE);
            planBar.setVisibility(View.VISIBLE);
            tripSlider.setVisibility(View.VISIBLE);
            rv_details.setVisibility(View.VISIBLE);
        }
    }

    private void tripDetailsInit() {
        //Setting trip details
        UpcomingTripDetailsRVAdapter adapter2 = new UpcomingTripDetailsRVAdapter(requireContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rv_details.setAdapter(adapter2);
        rv_details.setLayoutManager(layoutManager2);
    }

    public void goToEditTripActivity(ImageView imageView, TextView textView, int position) {
        Intent intent = new Intent(getContext(), EditTripActivity.class);
        intent.putExtra("id", getTripIdAt(position));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                Pair.create(imageView, ViewCompat.getTransitionName(imageView)),
                Pair.create(textView, ViewCompat.getTransitionName(textView))
        );
        this.startActivity(intent, options.toBundle());

    }

    private String getTripIdAt(int position) {
        return tripList.get(position).getId();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sliderPosChanged) {
            tripSlider.setCurrentItem(sliderPos);
            sliderPosChanged = false;
        }
    }

    public void setSliderPos(int sliderPos) {
        this.sliderPos = sliderPos;
        sliderPosChanged = true;
    }

    public int getSliderPos() {
        return sliderPos;
    }
}