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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ikea.myapp.R;
import com.ikea.myapp.UserData;
import com.ikea.myapp.Utils;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    ActionBar actionBar;
    Button sign_up_button;
    TextInputEditText inputName, inputEmail, inputPassword;
    TextInputLayout layoutInputName, layoutInputEmail, layoutInputPassword;
    RelativeLayout relativeLayout;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String name, email, password;
    ProgressDialog progressDialog;


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
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserData");

        //Setting the Actionbar attributes
        setTitle("Sign Up");
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Linking xml objects to java ints
        sign_up_button = findViewById(R.id.sign_up_button);
        relativeLayout = findViewById(R.id.signUpRelativeLayout);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        layoutInputName = findViewById(R.id.layoutInputName);
        layoutInputEmail = findViewById(R.id.layoutInputEmail);
        layoutInputPassword = findViewById(R.id.layoutInputPassword);

        //Setting 'enter as next' for input fields
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);


        //Initializing the progress dialog
        progressDialog = new ProgressDialog(this);

        //OnClick listeners
        sign_up_button.setOnClickListener(this);

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
        inputPassword.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    onClick(sign_up_button);
                }
            }
            return false;
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

        //Signing up process
        if (view == sign_up_button) {
            if (!validateName() | !validateEmail() | !validatePassword()) {
                return;
            }
            showProgressDialogWithTitle("Signing Up");
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener
                    (task -> {
                        if (task.isSuccessful()) {
                            UserData data = new UserData(name, email);
                            databaseReference.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).setValue(data).
                                    addOnCompleteListener(task1 -> {
                                        hideProgressDialogWithTitle();
                                        Toast.makeText(getApplicationContext(), R.string.login_account_created_successfully, Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this).toBundle());
                                    });
                        } else {
                            hideProgressDialogWithTitle();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
                                alertDialog.setTitle(R.string.login_encountered_problem);
                                alertDialog.setMessage(R.string.login_email_in_use);

                                alertDialog.setPositiveButton("Sign In", (dialog, which) -> {
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this).toBundle());
                                });
                                alertDialog.setNegativeButton("Try again", (dialog, which) -> dialog.cancel());
                                AlertDialog dialog = alertDialog.create();
                                dialog.show();
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
                        }
                    });
        }
    }

    private boolean validateName() {
        name = Objects.requireNonNull(inputName.getText()).toString().trim();
        if (TextUtils.isEmpty(name)) {
            layoutInputName.setError(getString(R.string.login_required_field));
            return false;
        } else {
            layoutInputName.setError(null);
            layoutInputName.setErrorEnabled(false);
            return true;
        }
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