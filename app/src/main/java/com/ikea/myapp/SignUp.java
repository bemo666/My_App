package com.ikea.myapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {
    TextInputEditText birthday, name, username, email, password;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //getSupportActionBar().hide();
        setTitle("Sign Up");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        name = findViewById(R.id.inputName);
        email = findViewById(R.id.inputEmail);
        password = findViewById(R.id.inputPassword);
        username = findViewById(R.id.inputUsername);

        name.setInputType(InputType.TYPE_CLASS_TEXT);
        email.setInputType(InputType.TYPE_CLASS_TEXT);
        password.setInputType(InputType.TYPE_CLASS_TEXT);
        username.setInputType(InputType.TYPE_CLASS_TEXT);

        birthday = findViewById(R.id.inputBirthday);

        DatePickerDialog datePicker = new DatePickerDialog(this);

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
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}