package com.ikea.myapp.UI.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

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
import com.ikea.myapp.MyApp;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.profile.ProfileActivity;
import com.ikea.myapp.UI.newTrip.NewTripActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    private FloatingActionButton addFab;
    private TabLayout tabLayout;
    private ViewPager2 fragments;
    private Toolbar toolbar;
    private FragmentAdapter fragmentAdapter;
    private ImageView backdrop;
    private AppBarLayout appBarLayout;

    protected void onCreate(Bundle savedInstanceState) {

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
        } else if (id == R.id.search) {
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