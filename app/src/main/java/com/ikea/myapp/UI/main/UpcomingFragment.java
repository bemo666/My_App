package com.ikea.myapp.UI.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
    private CardView extraIcon, welcomeCard, itineraryCard;
    private RelativeLayout planBar;
    private MaterialButton createTrip, addPlan;
    private ViewPager2 tripSlider;
    private TripsViewModel viewmodel;
    private UpcomingTripsRVAdapter adapter;
    protected int sliderPos;
    private static String sliderId;
    private static boolean sliderIdChanged;
    private List<MyTrip> tripList;
    private UpcomingTripDetailsRVAdapter adapter2;
    private TextInputEditText searchBar;
    private TextInputLayout searchBarLayout;
    private InputMethodManager imm;
    private TextWatcher textWatcher;
    private ShimmerFrameLayout details_shimmer;
    private TextView cardText;

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
        cardText = view.findViewById(R.id.upcoming_welcome_text);
        searchBar = view.findViewById(R.id.upcoming_search_bar);
        searchBarLayout = view.findViewById(R.id.upcoming_search_bar_layout);
        addPlan = view.findViewById(R.id.add_to_itinerary);
        itineraryCard = view.findViewById(R.id.itinerary_new_card);
        details_shimmer = view.findViewById(R.id.shimmer_view_container_trip);
        viewmodel = new ViewModelProvider(requireActivity()).get(TripsViewModel.class);
        viewmodel.getName().observe(getViewLifecycleOwner(), name -> {
            if (!name.equals("-1")) {
                cardText.setText("Welcome " + name + "!");
            }
        });
        extraIcon.setOnClickListener(view1 -> goToEditTripActivity(sliderPos, "map"));
        addPlan.setOnClickListener(view1 -> goToEditTripActivity(sliderPos, "addPlan"));
        searchBarInit();
        sliderInit();

        return view;
    }

    private void searchBarInit() {
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter2.search(editable.toString());
            }
        };
        imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
            }
            return false;
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        searchBar.removeTextChangedListener(textWatcher);
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
        handleItineraryCard();
        tripSlider.setAdapter(adapter);
        tripSlider.setClipToPadding(false);
        tripSlider.setClipChildren(false);
        tripSlider.setOffscreenPageLimit(3);
        tripSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        tripSlider.setNestedScrollingEnabled(true);
        tripSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderPos = position;
                adapter2.setList(tripList.get(sliderPos).getPlans());
                searchBarLayout.setVisibility(adapter2.getItemCount() == 0 ? View.GONE : View.VISIBLE);
                handleItineraryCard();
            }
        });


        viewmodel.getTrips().observe(getViewLifecycleOwner(), myTrips -> {
            if (myTrips != null) {
                tripList = new ArrayList<>();
                if (!myTrips.isEmpty()) {
                    for (MyTrip t : myTrips) {
                            long endStamp = t.getEndStamp();
                            TimeZone tz = TimeZone.getDefault();
                            long currentTime = Calendar.getInstance().getTimeInMillis() + tz.getOffset(Calendar.getInstance().getTimeInMillis());
                            if (endStamp >= currentTime) {
                                tripList.add(t);
                            }
                    }
                }
                adapter.setTrips(tripList);
            }
            if (tripList == null) {
                tripList = new ArrayList<>();
            } else {
                tripDetailsInit();
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

    private void handleItineraryCard() {
        itineraryCard.setVisibility(View.GONE);
        searchBar.setVisibility(View.GONE);
        if (tripList != null && adapter2 != null)
                if (adapter2.getItemCount() != 0) {
                    searchBar.setVisibility(View.VISIBLE);
                    details_shimmer.setVisibility(View.VISIBLE);
                    details_shimmer.stopShimmer();
                    details_shimmer.startShimmer();
                    rv_details.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        details_shimmer.setVisibility(View.GONE);
                        rv_details.setVisibility(View.VISIBLE);
                    }, 350);
                } else
                    itineraryCard.setVisibility(View.VISIBLE);
    }

    private void handleItineraryCardWithoutShimmer(){
        itineraryCard.setVisibility(View.GONE);
        if (tripList != null && adapter2 != null)
                if (adapter2.getItemCount() != 0) {
                    searchBar.setVisibility(View.VISIBLE);
                    details_shimmer.setVisibility(View.GONE);
                    rv_details.setVisibility(View.VISIBLE);
                } else {
                    itineraryCard.setVisibility(View.VISIBLE);
                    searchBar.setVisibility(View.GONE);
                }
    }

    private void handleWelcomeCard() {
        if (tripList.size() == 0) {
            tripSlider.setVisibility(View.GONE);
            welcomeCard.setVisibility(View.VISIBLE);
            planBar.setVisibility(View.GONE);
            searchBarLayout.setVisibility(View.GONE);
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
        if (tripList.size() != 0) {
            adapter2 = new UpcomingTripDetailsRVAdapter(this);
            adapter2.setList(tripList.get(sliderPos).getPlans());
            rv_details.setAdapter(adapter2);
            rv_details.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
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

    public void goToEditTripActivity(int position, String goTo) {
        Intent intent = new Intent(getContext(), EditTripActivity.class);
        intent.putExtra("id", getTripIdAt(position));
        intent.putExtra("goto", goTo);
        this.startActivity(intent);
    }

    private String getTripIdAt(int position) {
        return tripList.get(position).getId();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchBar.addTextChangedListener(textWatcher);
        int i = 0;
        if (sliderId != null && sliderIdChanged && tripList != null) {
                for (MyTrip t : tripList) {
                    if (t.getId().equals(sliderId)) {
                        sliderPos = i;
                        break;
                    }
                    i++;
                }
            sliderIdChanged = false;
        } else {
            sliderPos = 0;
        }
        tripSlider.setCurrentItem(sliderPos);
        handleItineraryCardWithoutShimmer();
    }

    public static void setSliderId(String id) {
        sliderIdChanged = true;
        sliderId = id;
    }

    public void setImage(String id, String absolutePath, int imageVersion) {
        viewmodel.setImage(id, absolutePath, imageVersion);
    }
}