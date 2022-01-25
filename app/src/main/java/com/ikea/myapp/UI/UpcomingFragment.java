package com.ikea.myapp.UI;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.ikea.myapp.R;
import com.ikea.myapp.Adapters.SliderAdapter;
import com.ikea.myapp.SliderItem;
import com.ikea.myapp.Adapters.TripDetailsAdapter;

import java.util.ArrayList;
import java.util.List;


public class UpcomingFragment extends Fragment {

    //Variables
    private ArrayList<String> mNames = new ArrayList<String>();
    RecyclerView rv_details;
    ViewPager2 trips;
    ScrollView scrollView;
    ShimmerFrameLayout trip_shimmer, details_shimmer;


    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        trips = view.findViewById(R.id.trips);
        rv_details = view.findViewById(R.id.rvTripDetails);
        scrollView = view.findViewById(R.id.scrollView);
        trip_shimmer = view.findViewById(R.id.shimmer_view_container_trip);
        details_shimmer = view.findViewById(R.id.shimmer_view_container_trip_details);

        //recyclerViewInit();

        tripDetailsInit();

        sliderInit();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                trip_shimmer.setVisibility(View.GONE);
                details_shimmer.setVisibility(View.GONE);
                trips.setVisibility(View.VISIBLE);
                rv_details.setVisibility(View.VISIBLE);
            }
        }, 3500);
        return view;
    }

    private void sliderInit() {
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItem(R.drawable.london1, "London", "Feb 1 - Apr 6"));
        sliderItems.add(new SliderItem(R.drawable.london2, "Amsterdam", "Aug 20 - Oct 1"));
        sliderItems.add(new SliderItem(R.drawable.london1, "Moscow", "Jun 8 - Jun 11"));
        sliderItems.add(new SliderItem(R.drawable.london2, "Syney", "Jan 29 - Feb 28"));

        trips.setAdapter(new SliderAdapter(sliderItems, trips));
        trips.setClipToPadding(false);
        trips.setClipChildren(false);
        trips.setOffscreenPageLimit(3);
        trips.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        trips.setPageTransformer(compositePageTransformer);

    }

    private void tripDetailsInit() {
        //Setting trip details
        TripDetailsAdapter adapter2 = new TripDetailsAdapter(requireContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        rv_details.setAdapter(adapter2);
        rv_details.setLayoutManager(layoutManager2);
    }

    public ScrollView getScrollView() {
        return scrollView;
    }
}