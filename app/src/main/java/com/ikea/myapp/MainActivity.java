package com.ikea.myapp;

import static java.security.AccessController.getContext;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Initializing variables
    FloatingActionButton add;
    TabLayout tabLayout;
    ViewPager2 viewPage2;
    Toolbar toolbar;
    NestedScrollView nst;
    FragmentAdapter fragmentAdapter;
    ImageView backdrop;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Disable dark mode ONLY ONCE
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        //Linking xml objects to java objects
        tabLayout = findViewById(R.id.tab_view);
        viewPage2 = findViewById(R.id.viewpager);
        add = findViewById(R.id.add_button);
        nst = findViewById(R.id.nest_scrollview);
        backdrop = findViewById(R.id.backdrop);

        //Link ActionBar, set it and change its color to transparent
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        //OnClick Listener
        add.setOnClickListener(this);

        //Fragments setup
        FragmentManager fm = getSupportFragmentManager();
        ArrayList<Fragment> list = new ArrayList<Fragment>();
        list.add(new UpcomingFragment());
        list.add(new PastFragment());
        fragmentAdapter = new FragmentAdapter(fm, getLifecycle(), list);
        viewPage2.setAdapter(fragmentAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Past"));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPage2.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

        viewPage2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

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
                Intent intent = new Intent(this, Profile.class);
                startActivity(intent);
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
            Intent intent = new Intent(this, NewTrip.class);
            startActivity(intent);
        }
    }

}