package com.ikea.myapp.UI.editTrip;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditTripActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView fabLeft, fabMiddle, fabRight;
    private Toolbar toolbar;
    private ImageView changeImage, mainImage;
    private TextView placeName, dates;
    private CollapsingToolbarLayout collapsingToolbar;
    private MyTrip trip;
    private AppBarLayout appBarLayout;
    private Animation rotateOpen, rotateClose, slideIn, slideOut, fadeIn, fadeOut;
    private FloatingActionButton addButton;
    private ExtendedFloatingActionButton notes, flights, hotels;
    private boolean clicked = false;
    private View mask;
    private CardView liveBadge, liveDot;


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
        trip = (MyTrip) getIntent().getSerializableExtra("trip");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        changeImage = findViewById(R.id.editTrip_changeImage);
//        fabLeft = findViewById(R.id.left_button);
//        fabMiddle = findViewById(R.id.middle_button);
//        fabRight = findViewById(R.id.right_button);
        toolbar = findViewById(R.id.editTripToolbar);
        collapsingToolbar = findViewById(R.id.editTripCollapsingToolbar);
        placeName = findViewById(R.id.editTrip_placeName);
        dates = findViewById(R.id.editTrip_dates);
        appBarLayout = findViewById(R.id.editTripAppBar);
        addButton = findViewById(R.id.add_button);
        notes = findViewById(R.id.add_notes);
        flights = findViewById(R.id.add_flights);
        hotels = findViewById(R.id.add_hotel);
        mask = findViewById(R.id.editTrip_mask);
        liveBadge = findViewById(R.id.live_badge);
        liveDot = findViewById(R.id.live_dot);
        mainImage = findViewById(R.id.editTrip_mainImage);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);


        if (Long.valueOf(trip.getStartStamp()) < Calendar.getInstance().getTimeInMillis() &&
                Long.valueOf(trip.getEndStamp()) > Calendar.getInstance().getTimeInMillis()) {
            liveBadge.setVisibility(View.VISIBLE);
            liveDot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));

        }
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
                        collapsingToolbar.setTitle(trip.getDestination());
                        set = true;
                    }

                    isShow = true;
                } else if (isShow) {
                    //Careful there must a space between double quote otherwise it dose't work
                    collapsingToolbar.setTitle(" ");
                    set = false;
                    isShow = false;
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        changeImage.setOnClickListener(this);
        addButton.setOnClickListener(this);
        mask.setOnClickListener(this);

        placeName.setText(trip.getDestination());

//        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MM dd", Locale.UK);
//        Date startDate = null;
//        try {
//            startDate = dateFormat.parse(trip.getStartStamp());
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        dates.setText(trip.getStartDate() + getResources().getString(R.string.ui_dash) + trip.getEndDate());


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent data = new Intent();
        data.putExtra("id", trip.getId());
        setResult(Activity.RESULT_OK, data);
        finishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view == addButton) {
            click();
        } else if (view == mask) {
            click();
        }
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
            addButton.startAnimation(rotateOpen);
            mask.startAnimation(fadeIn);
        } else {
            notes.startAnimation(slideIn);
            flights.startAnimation(slideIn);
            hotels.startAnimation(slideIn);
            addButton.startAnimation(rotateClose);
            mask.startAnimation(fadeOut);
        }
    }

    private void setVisibility() {
        if (!clicked) {
            notes.setVisibility(View.VISIBLE);
            flights.setVisibility(View.VISIBLE);
            hotels.setVisibility(View.VISIBLE);
            mask.setVisibility(View.VISIBLE);

        } else {
            notes.setVisibility(View.GONE);
            flights.setVisibility(View.GONE);
            hotels.setVisibility(View.GONE);
            mask.setVisibility(View.GONE);

        }
    }
}