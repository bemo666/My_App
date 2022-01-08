package com.ikea.myapp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.util.Patterns;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    TextView createAccount, forgotPasswordButton;
    Button signInButton;
    TextInputEditText  inputEmail, inputPassword;
    ActionBar actionBar;
    String loginEmail, loginPassword;
    TextInputLayout layoutInputEmail, layoutInputPassword;
    ProgressBar login_progress;
    private FirebaseAuth mAuth;

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
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Linking xml objects to java ints
        signInButton = findViewById(R.id.sign_in_button);
        forgotPasswordButton = findViewById(R.id.forgot_password_button);
        createAccount = findViewById(R.id.createAccount);
        login_progress = findViewById(R.id.login_progress);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        layoutInputEmail = findViewById(R.id.layoutInputEmail);
        layoutInputPassword = findViewById(R.id.layoutInputPassword);

        //Setting 'enter as next' for input fields
        inputEmail.setInputType(InputType.TYPE_CLASS_TEXT);

        //OnClick Listeners
        createAccount.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);

        //Text Boxes validations on several occasions
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

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
        case R.id.createAccount:
            getWindow().setExitTransition(new Slide(Gravity.START));
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        case R.id.sign_in_button:
        if (!validateEmail() | !validatePassword()) {
                return;
            }
            //    progressbar VISIBLE
            login_progress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).
                    addOnCompleteListener(task -> {
                        //    progressbar GONE
                        login_progress.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, R.string.ui_signed_in, Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent2);
                            finish();
                        } else {
                            //    progressbar GONE
                            login_progress.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, R.string.ui_incorrect_details, Toast.LENGTH_SHORT).show();
                        }
                    });
            case R.id.forgot_password_button:
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


    private boolean validateEmail() {
        loginEmail = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        if (TextUtils.isEmpty(loginEmail)) {
            layoutInputEmail.setError(getString(R.string.ui_required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
            layoutInputEmail.setError(getString(R.string.ui_not_valid_email));
            return false;
        } else {
            layoutInputEmail.setError(null);
            layoutInputEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        loginPassword = Objects.requireNonNull(inputPassword.getText()).toString();

        if (TextUtils.isEmpty(loginPassword)) {
            layoutInputPassword.setError(getString(R.string.ui_required_field));
            return false;
        } else if (loginPassword.contains(" ")) {
            layoutInputPassword.setError(getString(R.string.ui_password_has_space));
            return false;
        }  else if (loginPassword.length() < 6) {
            layoutInputPassword.setError(getString(R.string.ui_password_minimum));
            return false;
        }else {
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