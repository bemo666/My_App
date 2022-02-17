package com.ikea.myapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.transition.platform.MaterialElevationScale;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ikea.myapp.Managers.FirebaseRequestManager;
import com.ikea.myapp.R;
import com.ikea.myapp.UserData;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    private Button signInButton;
    private TextView accountEmail;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //
        if (FirebaseRequestManager.loggedIn()) {
            userRef = FirebaseDatabase.getInstance().getReference("UserData/" + FirebaseRequestManager.getUid());
            updateData();
        }

        //Setting the Actionbar attributes
        setTitle(getString(R.string.ui_profile));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Linking xml objects to java variables
        accountEmail = findViewById(R.id.accountEmail);
        signInButton = findViewById(R.id.firebase_button);

        //OnClick Listeners
        signInButton.setOnClickListener(this);
        accountEmail.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.firebase_button) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.accountEmail) {
            if (FirebaseRequestManager.loggedIn()) {
                FirebaseRequestManager.SignOut();
                accountEmail.setText("Account email: ");
                Toast.makeText(ProfileActivity.this, getString(R.string.login_logout_successful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateData() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!FirebaseRequestManager.loggedIn()) {
                    userRef.removeEventListener(this);
                } else {
                    UserData value = snapshot.getValue(UserData.class);
                    accountEmail.setText("Account email: " + Objects.requireNonNull(value).getEmail());
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (!FirebaseRequestManager.loggedIn()) {
                    userRef.removeEventListener(this);
                } else
                    Toast.makeText(getApplicationContext(), "Failed to update account info.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override

    public void onBackPressed() {
        super.onBackPressed();

    }
}