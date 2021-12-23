package com.ikea.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    TextView createAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();
        createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == createAccount){
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent);
        }

    }
}