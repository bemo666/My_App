package com.ikea.myapp.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.transition.platform.MaterialElevationScale;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ikea.myapp.FirebaseManager;
import com.ikea.myapp.R;
import com.ikea.myapp.UserData;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    Button signInButton;
    TextView accountEmail;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        if (firebaseUser != null) {
            userRef = firebaseDatabase.getReference("UserData/" + firebaseUser.getUid());
            updateData();
        }

        //Setting the Actionbar attributes
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Linking xml objects to java variables
        accountEmail = findViewById(R.id.accountEmail);
        signInButton = findViewById(R.id.sign_in_button);

        //OnClick Listeners
        signInButton.setOnClickListener(this);
        accountEmail.setOnClickListener(this);

        if (mAuth.getCurrentUser() != null)
            accountEmail.setText("Account Email: " + firebaseUser.getEmail());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                getWindow().setExitTransition(new MaterialElevationScale(true));
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            case R.id.accountEmail:
                if (FirebaseManager.loggedIn()) {
                    mAuth.signOut();
                    accountEmail.setText("Account email: ");
                    Toast.makeText(ProfileActivity.this, "Logout Successful ", Toast.LENGTH_SHORT).show();
                }
                break;
        }

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

    private void updateData() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mAuth == null){
                    userRef.removeEventListener(this);
                } else {
                    UserData value = snapshot.getValue(UserData.class);
                    accountEmail.setText("Account email: " + value.getEmail());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}