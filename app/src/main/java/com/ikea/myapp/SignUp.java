package com.ikea.myapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputEditText;

public class SignUp extends AppCompatActivity {
    //Declaring Variables
    TextInputEditText birthday, name, username, email, password;
    ActionBar actionBar;
    DatePickerDialog datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Slide());

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //getSupportActionBar().hide();

        //Setting the Actionbar attributes
        setTitle("Sign Up");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Linking xml objects to java objects
        name = findViewById(R.id.inputName);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        username = findViewById(R.id.inputUsername);
        birthday = findViewById(R.id.inputBirthday);

        //Setting 'enter as next' for input fields
        name.setInputType(InputType.TYPE_CLASS_TEXT);
        email.setInputType(InputType.TYPE_CLASS_TEXT);
        password.setInputType(InputType.TYPE_CLASS_TEXT);
        username.setInputType(InputType.TYPE_CLASS_TEXT);

        //Initializing the date picker dialog
        datePicker = new DatePickerDialog(this);

        //OnClick listeners
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show();
            }
        });

        datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 += 1;
                String date = i2 + "/" + i1 + "/" + i;
                birthday.setText(date);

            }
        });

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