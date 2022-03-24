package com.ikea.myapp.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.ikea.myapp.CustomProgressDialog;
import com.ikea.myapp.NetworkChangeReceiver;
import com.ikea.myapp.UI.main.MainActivity;
import com.ikea.myapp.data.TripRepo;
import com.ikea.myapp.data.remote.FirebaseManager;
import com.ikea.myapp.R;
import com.ikea.myapp.utils.Utils;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static TabLayout tabLayout;
    //Declaring Variables
    private Button firebaseButton;
    private TextInputEditText inputName, inputEmail, inputPassword;
    private ActionBar actionBar;
    private String name, email, password;
    private TextInputLayout layoutInputName, layoutInputEmail, layoutInputPassword;
    private RelativeLayout relativeLayout;
    private CustomProgressDialog progressDialog;
    private boolean bSignIn = true, bSignUp, bForgotPassword;
    private FirebaseManager firebaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Setting the Actionbar attributes
        setTitle("Login");
        actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setElevation(0);

        //Linking xml objects to java ints
        firebaseButton = findViewById(R.id.firebase_button);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        layoutInputName = findViewById(R.id.layoutInputName);
        layoutInputEmail = findViewById(R.id.layoutInputEmail);
        layoutInputPassword = findViewById(R.id.layoutInputPassword);
        relativeLayout = findViewById(R.id.signInRelativeLayout);
        tabLayout = findViewById(R.id.login_switcher);
        firebaseManager = new FirebaseManager();


        LinearLayout linearLayout = findViewById(R.id.login_linear_layout);
        linearLayout.setOnClickListener(view -> {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
        tabLayout.addTab(tabLayout.newTab().setText("Sign In"));
        tabLayout.addTab(tabLayout.newTab().setText("Sign Up"));
        tabLayout.addTab(tabLayout.newTab().setText("Reset Password"));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        signIn();
                        break;
                    case 1:
                        signUp();
                        break;
                    case 2:
                        forgotPassword();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        bSignIn = false;
                        break;
                    case 1:
                        bSignUp = false;
                        break;
                    case 2:
                        bForgotPassword = false;
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Setting 'enter as next' for input fields
        inputName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);
        inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        inputPassword.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    onClick(firebaseButton);
                }
            }
            return false;
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
                if (editable.toString().isEmpty())
                    layoutInputEmail.setError(getString(R.string.login_required_field));
                else {
                    layoutInputEmail.setError(null);
                    layoutInputEmail.setErrorEnabled(false);
                }
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
                if (editable.toString().isEmpty())
                    layoutInputPassword.setError(getString(R.string.login_required_field));
                else {
                    layoutInputPassword.setError(null);
                    layoutInputPassword.setErrorEnabled(false);
                }
            }
        });

        //OnClick Listeners
        firebaseButton.setOnClickListener(this);

    }

    private void signIn() {
        bSignIn = true;
        layoutInputName.setVisibility(View.GONE);
        layoutInputPassword.setVisibility(View.VISIBLE);
        firebaseButton.setText(R.string.ui_sign_in);
    }


    private void signUp() {
        bSignUp = true;
        layoutInputName.setVisibility(View.VISIBLE);
        layoutInputPassword.setVisibility(View.VISIBLE);
        firebaseButton.setText(R.string.login_sign_up);

    }

    private void forgotPassword() {
        bForgotPassword = true;
        layoutInputName.setVisibility(View.GONE);
        layoutInputPassword.setVisibility(View.GONE);
        firebaseButton.setText(R.string.login_forgot_password);
        inputEmail.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    onClick(firebaseButton);
                }
            }
            return false;
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.firebase_button) {
            if (NetworkChangeReceiver.hasConnection("Signing in/up is currently unavailable due to a lack of an internet connection")) {
                if (bSignIn) {
                    if (!validateEmail() | !validatePassword()) {
                    } else {
                        progressDialog = new CustomProgressDialog(this, "Signing In");
                        progressDialog.show();
                        firebaseManager.SignIn(email, password).addOnCompleteListener(task -> {
                            new TripRepo(getApplication()).deleteTable();

                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, R.string.login_signed_in, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } else if (!Utils.isNetworkConnected(this)) {
                                Log.d("tag", task.getException().toString());
                                Toast.makeText(this, R.string.ui_no_internet, Toast.LENGTH_SHORT).show();
                            } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                                    task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.d("tag", task.getException().toString());
                                Toast.makeText(this, R.string.login_incorrect_details, Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("tag", task.getException().toString());
                                Snackbar snackbar = Snackbar
                                        .make(relativeLayout, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        });
                    }
                } else if (bSignUp) {
                    if (!validateName() | !validateEmail() | !validatePassword()) {
                    } else {
                        progressDialog = new CustomProgressDialog(this, "Signing Up");
                        progressDialog.show();
                        firebaseManager.SignUp(email, password).addOnCompleteListener
                                (task -> {
                                    if (task.isSuccessful()) {
                                        firebaseManager.setFirstName(name).
                                                addOnCompleteListener(task1 -> {
                                                    progressDialog.hide();
                                                    Toast.makeText(this, R.string.login_account_created_successfully, Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                });
                                    } else {
                                        progressDialog.hide();
                                        if (!Utils.isNetworkConnected(this)) {
                                            Toast.makeText(this, R.string.ui_no_internet, Toast.LENGTH_SHORT).show();
                                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                                            alertDialog.setTitle(R.string.login_encountered_problem);
                                            alertDialog.setMessage(R.string.login_email_in_use);

                                            alertDialog.setPositiveButton("Sign In", (dialog, which) -> {
                                                setTabPosition(0);
                                            });
                                            alertDialog.setNegativeButton("Try again", (dialog, which) -> dialog.cancel());
                                            AlertDialog dialog = alertDialog.create();
                                            dialog.show();
                                        } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                                                task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            Toast.makeText(this, R.string.login_incorrect_email, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Snackbar snackbar = Snackbar
                                                    .make(relativeLayout, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    }
                                });
                    }
                } else if (bForgotPassword) {
                    if (!validateEmail()) {
                    } else {
                        progressDialog = new CustomProgressDialog(this, "Sending reset link");
                        progressDialog.show();
                        firebaseManager.ForgotPassword(email)
                                .addOnCompleteListener(task -> {
                                    progressDialog.hide();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, R.string.login_reset_link_sent, Toast.LENGTH_LONG).show();
                                        setTabPosition(0);
                                    } else if (!Utils.isNetworkConnected(this)) {
                                        Toast.makeText(this, R.string.ui_no_internet, Toast.LENGTH_SHORT).show();
                                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException ||
                                            task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        Toast.makeText(this, R.string.login_incorrect_email, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar snackbar = Snackbar
                                                .make(relativeLayout, Objects.requireNonNull(task.getException()).toString(), Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                    }
                                });
                    }
                }
            }
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
    public void onPostResume() {
        super.onPostResume();
        if (FirebaseManager.loggedIn())
            finish();
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

    public void setTabPosition(int pos) {
        tabLayout.selectTab(tabLayout.getTabAt(pos));
    }
}