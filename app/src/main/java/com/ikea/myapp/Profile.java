package com.ikea.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.material.transition.platform.MaterialElevationScale;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    Button signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Setting the Actionbar attributes
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Linking xml objects to java objects
        signInButton = findViewById(R.id.sign_in_button);

        //OnClick Listeners
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        getWindow().setExitTransition(new MaterialElevationScale(true));
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}