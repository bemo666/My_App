package com.ikea.myapp.UI.editTrip;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ikea.myapp.Adapters.FragmentAdapter;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class EditTripActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ViewPager2 fragments;
    private ImageView changeImage, mainImage;
    private TextView placeName;
    private CollapsingToolbarLayout collapsingToolbar;
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
        viewModel = ViewModelProviders.of(this).get(EditTripViewModel.class);
        //Fragments setup
        FragmentManager fm = getSupportFragmentManager();
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new OverviewFragment(trip));
        list.add(new OverviewFragment(trip));
        list.add(new BudgetFragment(trip));
        FragmentAdapter fragmentAdapter = new FragmentAdapter(fm, getLifecycle(), list);
        fragments.setAdapter(fragmentAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                fragments.setCurrentItem(tab.getPosition());
                if (tab.getPosition() != 0) {
                    appBarLayout.setExpanded(false, true);
                    addButton.hide();
                }
                else {
                    appBarLayout.setExpanded(true, true);
                    addButton.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        fragments.setUserInputEnabled(false);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out);
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);




        if (Long.parseLong(trip.getStartStamp()) < Calendar.getInstance().getTimeInMillis() &&
                Long.parseLong(trip.getEndStamp()) > Calendar.getInstance().getTimeInMillis()) {
            liveBadge.setVisibility(View.VISIBLE);
            liveDot.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
        } else{
            liveDot.clearAnimation();
            liveBadge.setVisibility(View.GONE);
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

//        try{
//            byte[] encodeByte = Base64.decode(trip.getImage(), Base64.DEFAULT);
//            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
//            mainImage.setImageBitmap(bitmap);
//        }catch (Exception e){
//            Toast.makeText(getApplicationContext(), "No image", Toast.LENGTH_SHORT).show();
//        }


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
}