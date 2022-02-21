package com.ikea.myapp.UI.editTrip;

import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class EditTripActivity extends AppCompatActivity {

    private FloatingActionButton fabLeft, fabMiddle, fabRight;
    private Toolbar toolbar;
    private ImageView mainImage;
    private TextView placeName, dates;
    private CollapsingToolbarLayout collapsingToolbar;
    private MyTrip trip;


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

        mainImage = findViewById(R.id.editTrip_mainImage);
        fabLeft = findViewById(R.id.left_button);
        fabMiddle = findViewById(R.id.middle_button);
        fabRight = findViewById(R.id.right_button);
        toolbar = findViewById(R.id.editTripToolbar);
        collapsingToolbar = findViewById(R.id.editTripCollapsingToolbar);
        placeName = findViewById(R.id.editTrip_placeName);
        dates = findViewById(R.id.editTrip_dates);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        mainImage.setOnClickListener(view -> { });
        setFabBehaviour();

        placeName.setText(trip.getDestination());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dates.setText(trip.getStartDate());


    }

    private void setFabBehaviour() {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fabRight.getLayoutParams();
        FloatingActionButton.Behavior behavior = (FloatingActionButton.Behavior) lp.getBehavior();
        if (behavior != null) {
            behavior.setAutoHideEnabled(false);
        }else {
            behavior = new FloatingActionButton.Behavior();
            behavior.setAutoHideEnabled(false);
            lp.setBehavior(behavior);
        }
        CoordinatorLayout.LayoutParams lp2 = (CoordinatorLayout.LayoutParams) fabMiddle.getLayoutParams();
        FloatingActionButton.Behavior behavior2 = (FloatingActionButton.Behavior) lp2.getBehavior();
        if (behavior2 != null) {
            behavior2.setAutoHideEnabled(false);
        }else {
            behavior2 = new FloatingActionButton.Behavior();
            behavior2.setAutoHideEnabled(false);
            lp2.setBehavior(behavior2);
        }
        CoordinatorLayout.LayoutParams lp3 = (CoordinatorLayout.LayoutParams) fabLeft.getLayoutParams();
        FloatingActionButton.Behavior behavior3 = (FloatingActionButton.Behavior) lp3.getBehavior();
        if (behavior3 != null) {
            behavior3.setAutoHideEnabled(false);
        }else {
            behavior3 = new FloatingActionButton.Behavior();
            behavior3.setAutoHideEnabled(false);
            lp3.setBehavior(behavior3);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}