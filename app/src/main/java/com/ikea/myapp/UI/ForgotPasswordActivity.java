package com.ikea.myapp.UI;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.ikea.myapp.R;
import com.ikea.myapp.Utils;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    TextInputEditText inputEmail;
    TextInputLayout layoutInputEmail;
    Button resetPasswordButton;
    ActionBar actionBar;
    FirebaseAuth firebaseAuth;
    String resetEmail;
    RelativeLayout relativeLayout;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Linking xml objects to java ints
        inputEmail = findViewById(R.id.inputEmail);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        layoutInputEmail = findViewById(R.id.layoutInputEmail);
        relativeLayout = findViewById(R.id.forgotRelativeLayout);

        //Setting the Actionbar attributes
        setTitle("Forgot Password");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

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
        inputEmail.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    onClick(resetPasswordButton);
                }
            }
            return false;
        });

        //Initializing the progress dialog
        progressDialog = new ProgressDialog(this);

        //Get firebase instance
        firebaseAuth = FirebaseAuth.getInstance();

        //Resetting password
        resetPasswordButton.setOnClickListener(this);
    }

    private boolean validateEmail() {
        resetEmail = Objects.requireNonNull(inputEmail.getText()).toString();
        if (TextUtils.isEmpty(resetEmail)) {
            layoutInputEmail.setError(getString(R.string.login_required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
            layoutInputEmail.setError(getString(R.string.login_not_valid_email));
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
            finishAfterTransition();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        if (validateEmail()) {
            showProgressDialogWithTitle("Sending Reset Link");
            firebaseAuth.sendPasswordResetEmail(resetEmail)
                    .addOnCompleteListener(task -> {
                        hideProgressDialogWithTitle();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.login_reset_link_sent, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this).toBundle());
                        } else if (!Utils.isNetworkConnected(this)) {
                            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                                task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), R.string.login_incorrect_email, Toast.LENGTH_SHORT).show();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(relativeLayout, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });
        }
    }

    private void showProgressDialogWithTitle(String substring) {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(substring);
        progressDialog.show();
    }

    private void hideProgressDialogWithTitle() {
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.dismiss();
    }
}