package com.ikea.myapp;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    ActionBar actionBar;
    DatePickerDialog datePicker;
    Button button;
    TextInputEditText inputName, inputBirthday, inputEmail, inputPassword;
    TextInputLayout layoutInputName, layoutInputBirthday, layoutInputEmail, layoutInputPassword;
    ProgressBar signUp_progress;
    private boolean isDataSet;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private String name, birthday, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Slide());

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initiating firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserData");

        //Setting the Actionbar attributes
        setTitle("Sign Up");
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Linking xml objects to java ints
        button = findViewById(R.id.sign_up_button);
        inputName = findViewById(R.id.inputName);
        inputBirthday = findViewById(R.id.inputBirthday);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        layoutInputName = findViewById(R.id.layoutInputName);
        layoutInputBirthday = findViewById(R.id.layoutInputBirthday);
        layoutInputEmail = findViewById(R.id.layoutInputEmail);
        layoutInputPassword = findViewById(R.id.layoutInputPassword);
        signUp_progress = findViewById(R.id.signUp_progress);

        //Setting 'enter as next' for input fields
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);


        //Initializing the date picker dialog
        datePicker = new DatePickerDialog(this);
        isDataSet = false;  // this is used by the onDismiss handler
        datePicker.setOnDateSetListener((datePicker, i, i1, i2) -> {
            i1 += 1;
            String date = i2 + "/" + i1 + "/" + i;
            inputBirthday.setText(date);

        });
        datePicker.setOnDismissListener(dialogInterface -> {
            if (!isDataSet) {
                validateBirthday();
            }
        });

        //OnClick listeners
        button.setOnClickListener(this);
        inputBirthday.setOnClickListener(this);

        //Text Boxes validations on several occasions
        inputName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateName();
            }
        });
        inputEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validateEmail();
            }
        });
        inputPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                validatePassword();
            }
        });
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateName();
            }
        });
        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEmail();
            }
        });
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePassword();
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == inputBirthday) {
            datePicker.getDatePicker().setMaxDate(new Date().getTime());
            datePicker.show();
        }
        //Signing up process
        if (view == button) {
            if (!validateName() | !validateEmail() | !validatePassword() | !validateBirthday()) {
                return;
            }
            //    progressbar VISIBLE
            signUp_progress.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                    (task -> {
                        if (task.isSuccessful()) {
                            UserData data = new UserData(name, birthday, email);
                            FirebaseDatabase.getInstance().getReference("UserData")
                                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(data).
                                    addOnCompleteListener(task1 -> {
                                        //    progressbar GONE
                                        signUp_progress.setVisibility(View.GONE);
                                        Toast.makeText(getApplicationContext(), R.string.ui_account_created_successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this).toBundle());
                                    });
                        } else {
                            //    progressbar GONE
                            signUp_progress.setVisibility(View.GONE);
                            AlertDialog.Builder b = new AlertDialog.Builder(SignUpActivity.this);
                            b.setTitle(R.string.ui_encountered_problem);
                            b.setMessage(R.string.ui_email_in_use);

                            b.setPositiveButton("Sign In", (dialog, which) -> {
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this).toBundle());
                            });
                            b.setNegativeButton("Try again", (dialog, which) -> dialog.cancel());
                            AlertDialog dialog = b.create();
                            dialog.show();
                        }
                    });
        }
    }

    private boolean validateName() {
        name = Objects.requireNonNull(inputName.getText()).toString().trim();
        if (TextUtils.isEmpty(name)) {
            layoutInputName.setError(getString(R.string.ui_required_field));
            return false;
        } else {
            layoutInputName.setError(null);
            layoutInputName.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateBirthday() {
        birthday = Objects.requireNonNull(inputBirthday.getText()).toString().trim();
        if (TextUtils.isEmpty(birthday)) {
            layoutInputBirthday.setError(getString(R.string.ui_required_field));
            return false;
        } else {
            layoutInputBirthday.setError(null);
            layoutInputBirthday.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        if (TextUtils.isEmpty(email)) {
            layoutInputEmail.setError(getString(R.string.ui_required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutInputEmail.setError(getString(R.string.ui_not_valid_email));
            return false;
        } else {
            layoutInputEmail.setError(null);
            layoutInputEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        password = Objects.requireNonNull(inputPassword.getText()).toString();

        if (TextUtils.isEmpty(password)) {
            layoutInputPassword.setError(getString(R.string.ui_required_field));
            return false;
        } else if (password.contains(" ")) {
            layoutInputPassword.setError(getString(R.string.ui_password_has_space));
            return false;
        } else if (password.length() < 6) {
            layoutInputPassword.setError(getString(R.string.ui_password_minimum));
            return false;
        } else {
            layoutInputPassword.setError(null);
            layoutInputPassword.setErrorEnabled(false);
            return true;
        }
    }


    //    if the user already logged in then it will automatically send on Dashboard/MainActivity activity.
    @Override
    public void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }
}