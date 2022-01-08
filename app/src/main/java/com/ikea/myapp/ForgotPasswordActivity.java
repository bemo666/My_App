package com.ikea.myapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    //Declaring Variables
    TextInputEditText inputEmail;
    TextInputLayout layoutInputEmail;
    Button resetPasswordButton;
    ProgressBar reset_progress;
    FirebaseAuth firebaseAuth;
    String resetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Linking xml objects to java ints
        inputEmail = findViewById(R.id.inputEmail);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        reset_progress = findViewById(R.id.resetPassword_progress);
        layoutInputEmail = findViewById(R.id.layoutInputEmail);

        //Setting 'enter as next' for input fields
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);

        //Text Box validations
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

        //Get firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        //Resetting password
        resetPasswordButton.setOnClickListener(v -> {
            if (validateEmail()) {
                reset_progress.setVisibility(View.VISIBLE);
                firebaseAuth.sendPasswordResetEmail(resetEmail)
                        .addOnCompleteListener(task -> {
                            reset_progress.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, R.string.ui_reset_link_sent, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this).toBundle());
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, R.string.ui_incorrect_email, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private boolean validateEmail() {
        resetEmail = Objects.requireNonNull(inputEmail.getText()).toString();
        if (TextUtils.isEmpty(resetEmail)) {
            layoutInputEmail.setError(getString(R.string.ui_required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
            layoutInputEmail.setError(getString(R.string.ui_not_valid_email));
            return false;
        } else {
            layoutInputEmail.setError(null);
            layoutInputEmail.setErrorEnabled(false);
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