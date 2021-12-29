package com.ikea.myapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NavUtils;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.transition.platform.MaterialElevationScale;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    TextView createAccount;
    Button signInButton;
    TextInputEditText email, password;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Setting the Actionbar attributes
        setTitle("Sign In");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Linking xml objects to java objects
        signInButton = findViewById(R.id.sign_in_button);
        createAccount = findViewById(R.id.createAccount);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);

        //Setting 'enter as next' for input fields
        email.setInputType(InputType.TYPE_CLASS_TEXT);
        password.setInputType(InputType.TYPE_CLASS_TEXT);

        //OnClick Listeners
        createAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == createAccount){
            getWindow().setExitTransition(new Slide(Gravity.LEFT));
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
}