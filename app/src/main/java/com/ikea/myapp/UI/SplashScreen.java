package com.ikea.myapp.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ikea.myapp.R;

public class SplashScreen extends AppCompatActivity {

    Animation animTop, animBottom;
    ImageView logo;
    TextView appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
        appName =findViewById(R.id.app_name);

        animTop = AnimationUtils.loadAnimation(this, R.anim.slide_from_top);
        animBottom = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom);

        logo.startAnimation(animTop);
        appName.startAnimation(animBottom);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            getWindow().setExitTransition(new AutoTransition());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this).toBundle());
        }, 2250);

    }
}