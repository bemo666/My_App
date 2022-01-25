package com.ikea.myapp.UI;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.transition.platform.MaterialElevationScale;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ikea.myapp.Adapters.FragmentAdapter;
import com.ikea.myapp.FirebaseManager;
import com.ikea.myapp.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    FloatingActionButton add;
    TabLayout tabLayout;
    ViewPager2 fragments;
    Toolbar toolbar;
    FragmentAdapter fragmentAdapter;
    ImageView backdrop;
    AppBarLayout appBarLayout;
    FirebaseDatabase database;
    DatabaseReference myRef;

    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Disable dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        //Linking xml objects to java variables
        tabLayout = findViewById(R.id.tab_view);
        fragments = findViewById(R.id.viewpager);
        add = findViewById(R.id.add_button);
        backdrop = findViewById(R.id.backdrop);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.app_bar);

        //Setting the Actionbar attributes
        setSupportActionBar(toolbar);
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        //OnClick listeners
        add.setOnClickListener(this);

        //Fragments setup
        FragmentManager fm = getSupportFragmentManager();
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(new UpcomingFragment());
        list.add(new PastFragment());
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle(), list);
        fragments.setAdapter(fragmentAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragments.setCurrentItem(tab.getPosition());
                appBarLayout.setExpanded(true);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (fragmentAdapter.getItem(0) != null && tab.getPosition() == 1)
                            ((UpcomingFragment) fragmentAdapter.getItem(0)).getScrollView().fullScroll(ScrollView.FOCUS_UP);

                        else if ( fragmentAdapter.getItem(1) != null && tab.getPosition() == 0)
                            ((PastFragment) fragmentAdapter.getItem(1)).getScrollView().fullScroll(ScrollView.FOCUS_UP);

                        //Do something after 100ms
                    }
                }, 500);


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        fragments.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        fragments.setUserInputEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu (search and profile)
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //Get profile button and change its color
        Drawable drawable = menu.findItem(R.id.profile).getIcon();
        drawable.setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.profile:
                getWindow().setExitTransition(new MaterialElevationScale(true));
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

                return true;

            case R.id.search:
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onClick(View view) {
        if (view == add) {
            Intent intent = new Intent(this, NewTripActivity.class);
            startActivity(intent);

        }
    }
}