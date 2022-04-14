package com.ikea.myapp.UI.main;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.ikea.myapp.Adapters.FragmentAdapter;
import com.ikea.myapp.Notification;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.profile.ProfileActivity;
import com.ikea.myapp.UI.newTrip.NewTripActivity;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String CHANNEL_ID = "Trip Reminder";
    //Declaring Variables
    private FloatingActionButton addFab;
    private TabLayout tabLayout;
    private ViewPager2 fragments;
    private Toolbar toolbar;
    private FragmentAdapter fragmentAdapter;
    private ImageView backdrop;
    private AppBarLayout appBarLayout;
    private Animation animTop, animBottom;
    private ImageView logo;
    private TextView appName;
    private LinearLayout SplashMask;


    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Linking xml objects to java variables
        tabLayout = findViewById(R.id.tab_view);
        fragments = findViewById(R.id.viewpager);
        addFab = findViewById(R.id.add_button);
        backdrop = findViewById(R.id.backdrop);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);
        logo = findViewById(R.id.logo);
        appName =findViewById(R.id.app_name);
        SplashMask = findViewById(R.id.splash_mask);

        animTop = AnimationUtils.loadAnimation(this, R.anim.slide_from_top);
        animBottom = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom);

        logo.startAnimation(animTop);
        appName.startAnimation(animBottom);

        //Setting the Actionbar attributes
        setSupportActionBar(toolbar);
        toolbar.setBackground(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.transparent)));

        //OnClick listeners
        addFab.setOnClickListener(this);

        //Fragments setup
        FragmentManager fm = getSupportFragmentManager();
        ArrayList<Fragment> list = new ArrayList<>();
        list.add(new UpcomingFragment());
        list.add(new PastFragment());
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle(), list);
        fragments.setAdapter(fragmentAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragments.setCurrentItem(tab.getPosition());
                appBarLayout.setExpanded(true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        fragments.setUserInputEnabled(false);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> SplashMask.setVisibility(View.GONE), 2250);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu (search and profile)
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //Get profile button and change its color
        Drawable drawable = menu.findItem(R.id.profile).getIcon();
        drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.orange),
                PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == addFab) {
            startActivity(new Intent(this, NewTripActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}