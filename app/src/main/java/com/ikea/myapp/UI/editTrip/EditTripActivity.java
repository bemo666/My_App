package com.ikea.myapp.UI.editTrip;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ikea.myapp.Adapters.FragmentAdapter;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.R;
import com.ikea.myapp.utils.MyViewModelFactory;

import java.util.ArrayList;
import java.util.Calendar;

public class EditTripActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ViewPager2 fragments;
    private ImageView mainImage;
    private TextView placeName;
    private CollapsingToolbarLayout collapsingToolbar;
    private String id;
    private MyTrip trip;
    private AppBarLayout appBarLayout;
    private TabLayout tabLayout;
    private Animation rotateOpen, rotateClose, slideIn, slideOut, fadeIn, fadeOut;
    private FloatingActionButton addButton;
    private ExtendedFloatingActionButton notes, flights, hotels, rentals;
    private boolean clicked = false;
    private View mask;
    private CardView liveBadge, liveDot;
    private EditTripViewModel viewModel;
    private CoordinatorLayout.LayoutParams layoutParams;
    private ItineraryFragment itineraryFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
        id = (String) getIntent().getSerializableExtra("id");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        fragments = findViewById(R.id.editTrip_viewpager);
        toolbar = findViewById(R.id.editTripToolbar);
        collapsingToolbar = findViewById(R.id.editTripCollapsingToolbar);
        placeName = findViewById(R.id.editTrip_placeName);
        appBarLayout = findViewById(R.id.editTripAppBar);
        addButton = findViewById(R.id.add_button);
        notes = findViewById(R.id.add_notes);
        flights = findViewById(R.id.add_flights);
        hotels = findViewById(R.id.add_hotel);
        rentals = findViewById(R.id.add_rental);
        mask = findViewById(R.id.editTrip_mask);
        liveBadge = findViewById(R.id.live_badge);
        liveDot = findViewById(R.id.live_dot);
        mainImage = findViewById(R.id.editTrip_mainImage);
        tabLayout = findViewById(R.id.editTripTabLayout);
        itineraryFragment = new ItineraryFragment(id);

        viewModel = ViewModelProviders.of(this, new MyViewModelFactory(getApplication(), id)).get(EditTripViewModel.class);

        viewModel.getTrip(id).observe(this, myTrip -> {
            trip = myTrip;
            if (Long.parseLong(trip.getStartStamp()) < Calendar.getInstance().getTimeInMillis() &&
                    Long.parseLong(trip.getEndStamp()) > Calendar.getInstance().getTimeInMillis()) {
                liveBadge.setVisibility(View.VISIBLE);
                liveDot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
            } else {
                liveDot.clearAnimation();
                liveBadge.setVisibility(View.GONE);
            }
            placeName.setText(trip.getDestination());
        });

        //Fragments setup
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(itineraryFragment);
        list.add(new MapFragment(id));
        list.add(new BudgetFragment(id));
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), list);
        fragments.setAdapter(fragmentAdapter);
        fragments.setUserInputEnabled(false);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                fragments.setCurrentItem(tab.getPosition());
                if (clicked) {
                    click();
                }
                switch (tab.getPosition()) {
                    case 0:
                        appBarLayout.setExpanded(true, true);
                        addButton.show();
                        addButton.setOnClickListener(view -> click());

                        break;
                    case 1:
                    case 2:
                        appBarLayout.setExpanded(false, true);
                        addButton.hide();
                        addButton.setOnClickListener(null);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });


        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false, set = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset <= 85) {
                    if (!set) {
                        if (trip != null)
                            collapsingToolbar.setTitle(trip.getDestination());
                        set = true;
                    }

                    isShow = true;
                } else if (isShow) {
                    //Careful there must a space between double quote otherwise it doesn't work
                    collapsingToolbar.setTitle(" ");
                    set = false;
                    isShow = false;
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        setSupportActionBar(toolbar);
        setTitle(" ");

        mask.setOnClickListener(view -> click());
        addButton.setOnClickListener(view -> click());
        notes.setOnClickListener(this);
        hotels.setOnClickListener(this);
        rentals.setOnClickListener(this);
        flights.setOnClickListener(this);


//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MM dd", Locale.UK);
//        Date startDate = null;
//        try {
//            startDate = dateFormat.parse(trip.getStartStamp());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    private void click() {
        setVisibility();
        setAnimation();
        clicked = !clicked;
    }


    private void setAnimation() {
        if (!clicked) {
            notes.startAnimation(slideOut);
            flights.startAnimation(slideOut);
            hotels.startAnimation(slideOut);
            rentals.startAnimation(slideOut);
            addButton.startAnimation(rotateOpen);
            mask.startAnimation(fadeIn);
        } else {
            notes.startAnimation(slideIn);
            flights.startAnimation(slideIn);
            hotels.startAnimation(slideIn);
            rentals.startAnimation(slideIn);
            addButton.startAnimation(rotateClose);
            mask.startAnimation(fadeOut);
        }
    }

    private void setVisibility() {
        if (!clicked) {
            appBarLayout.setExpanded(true, true);
            notes.show();
            flights.show();
            hotels.show();
            rentals.show();
            mask.setVisibility(View.VISIBLE);

        } else {
            notes.hide();
            flights.hide();
            hotels.hide();
            rentals.hide();
            mask.setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == notes.getId()) {
            itineraryFragment.checkForAddHeader(PlanHeader.NOTE);
        }

        click();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_trip_menu, menu);
        Drawable deleteTripIcon = menu.findItem(R.id.delete_trip).getIcon(),
                changeImageIcon = menu.findItem(R.id.change_image).getIcon();
        deleteTripIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.red), PorterDuff.Mode.SRC_IN);
        changeImageIcon.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.black), PorterDuff.Mode.SRC_IN);
        if (menu instanceof MenuBuilder) {

            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_trip:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme_Delete)
                        .setTitle(R.string.ui_delete_trip)
                        .setMessage("Are you sure you want to delete your trip to " + trip.getDestination() + "?")
                        .setPositiveButton("DELETE", (dialog, which) -> {
                            if (trip != null)
                                viewModel.deleteTrip(trip);
                            finish();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = alertDialog.create();
//                dialog.getButton(0).setTextColor(ContextCompat.getColor(this, R.color.red));
//                dialog.getButton(1).setTextColor(ContextCompat.getColor(this, R.color.black));
                dialog.show();
                return true;

            case R.id.change_image:
                Toast.makeText(getApplicationContext(), "Call Back Clicked", Toast.LENGTH_LONG).show();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            default:

                super.onOptionsItemSelected(item);

        }
        return true;

    }

    public void setTabPosition(int pos) {
        tabLayout.selectTab(tabLayout.getTabAt(pos));
    }


}