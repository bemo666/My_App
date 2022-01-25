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
import android.transition.Slide;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.ikea.myapp.R;
import com.ikea.myapp.Utils;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    TextView createAccountButton, forgotPasswordButton;
    Button signInButton;
    TextInputEditText inputEmail, inputPassword;
    ActionBar actionBar;
    String email, password;
    TextInputLayout layoutInputEmail, layoutInputPassword;
    private FirebaseAuth mAuth;
    RelativeLayout relativeLayout;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Initiating firebase
        mAuth = FirebaseAuth.getInstance();

        //Setting the Actionbar attributes
        setTitle("Sign In");
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Linking xml objects to java ints
        signInButton = findViewById(R.id.sign_in_button);
        forgotPasswordButton = findViewById(R.id.forgot_password_button);
        createAccountButton = findViewById(R.id.createAccount);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        layoutInputEmail = findViewById(R.id.layoutInputEmail);
        layoutInputPassword = findViewById(R.id.layoutInputPassword);
        relativeLayout = findViewById(R.id.signInRelativeLayout);

        //Setting 'enter as next' for input fields
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

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
        inputPassword.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    onClick(signInButton);
                }
            }
            return false;
        });

        //OnClick Listeners
        createAccountButton.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);

        //Initializing the progress dialog
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.createAccount) {
            getWindow().setExitTransition(new Slide(Gravity.START));
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.sign_in_button) {
            if (!validateEmail() | !validatePassword()) {
                return;
            }
            showProgressDialogWithTitle("Signing In");

            mAuth.signInWithEmailAndPassword(email, password).
                    addOnCompleteListener(task -> {
                        hideProgressDialogWithTitle();
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, R.string.login_signed_in, Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent2);
                            finish();
                        } else if (!Utils.isNetworkConnected(this)) {
                            Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                                task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(getApplicationContext(), R.string.login_incorrect_details, Toast.LENGTH_SHORT).show();
                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(relativeLayout, task.getException().toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });

        } else if (id == R.id.forgot_password_button) {
            Intent intent2 = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(SignInActivity.this).toBundle());
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


    //    if the user already logged in then it will automatically send on Dashboard/MainActivity activity.
    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
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

    private boolean validateEmail() {
        email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        if (TextUtils.isEmpty(email)) {
            layoutInputEmail.setError(getString(R.string.login_required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutInputEmail.setError(getString(R.string.login_not_valid_email));
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
            layoutInputPassword.setError(getString(R.string.login_required_field));
            return false;
        } else if (password.contains(" ")) {
            layoutInputPassword.setError(getString(R.string.login_password_has_space));
            return false;
        } else if (password.length() < 6) {
            layoutInputPassword.setError(getString(R.string.login_password_minimum));
            return false;
        } else {
            layoutInputPassword.setError(null);
            layoutInputPassword.setErrorEnabled(false);
            return true;
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