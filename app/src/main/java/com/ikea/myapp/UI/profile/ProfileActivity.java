package com.ikea.myapp.UI.profile;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.LoginActivity;
import com.ikea.myapp.UI.main.MainActivity;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    private ProfileViewModel viewModel;
    private MaterialButton signInButton, signOutButton, deleteAccountButton, saveButton;
    private TextInputEditText accountEmail, accountFirstName;
    private TextInputLayout layoutAccountEmail;
    private LinearLayout signInLayout, accountInfoLayout;
    private String email;
    private boolean emailChanged = false, nameChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Enable Activity Transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.action_bar_container), true);
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //


        //Setting the Actionbar attributes
        setTitle(getString(R.string.ui_account));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Linking xml objects to java variables
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        accountEmail = findViewById(R.id.accountEmail);
        layoutAccountEmail = findViewById(R.id.layoutAccountEmail);
        accountFirstName = findViewById(R.id.accountFirstName);
        signInButton = findViewById(R.id.firebase_button);
        signInLayout = findViewById(R.id.sign_in_linear_layout);
        accountInfoLayout = findViewById(R.id.account_info_linear_layout);
        signOutButton = findViewById(R.id.sign_out_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        saveButton = findViewById(R.id.save_button);


        //OnClick Listeners
        signInButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        LinearLayout linearLayout = findViewById(R.id.profile_linear_layout);
        linearLayout.setOnClickListener(view -> {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });

        accountEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    layoutAccountEmail.setError(getString(R.string.login_required_field));
                else {
                    layoutAccountEmail.setError(null);
                    layoutAccountEmail.setErrorEnabled(false);
                }
                emailChanged = true;
                saveButton.setEnabled(true);
            }
        });
        accountFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameChanged = true;
                saveButton.setEnabled(true);
            }
        });


        if (FirebaseManager.loggedIn()) {
            accountInfoLayout.setVisibility(View.VISIBLE);
            signInLayout.setVisibility(View.GONE);
            updateData();
            saveButton.setEnabled(false);
        } else {
            accountInfoLayout.setVisibility(View.GONE);
            signInLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.firebase_button) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.sign_out_button) {
            if (FirebaseManager.loggedIn()) {
                FirebaseManager.SignOut();
                Toast.makeText(ProfileActivity.this, getString(R.string.login_logout_successful), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.delete_account_button) {
            if (FirebaseManager.loggedIn()) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.profile_delete_account);
                alertDialog.setMessage(R.string.profile_delete_account_dialog_text);

                alertDialog.setPositiveButton("Delete", (dialog, which) -> FirebaseManager.DeleteAccount(this));
                alertDialog.setNegativeButton("Never Mind", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        } else if (id == R.id.save_button) {
            if (nameChanged) {
                nameChanged = false;
                saveButton.setEnabled(false);

            }
            if (emailChanged) {
                emailChanged = false;
                if (validateEmail()) {
                    saveButton.setEnabled(false);

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

    private void updateData() {
        viewModel.getName().observe(this, s -> {
            accountFirstName.setText(s, TextView.BufferType.EDITABLE);
            saveButton.setEnabled(false);
        });
        accountEmail.setText(viewModel.getEmail(), TextView.BufferType.EDITABLE);
    }


    private boolean validateEmail() {
        email = Objects.requireNonNull(accountEmail.getText()).toString().trim();
        if (TextUtils.isEmpty(email)) {
            layoutAccountEmail.setError(getString(R.string.login_required_field));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutAccountEmail.setError(getString(R.string.login_not_valid_email));
            return false;
        } else {
            layoutAccountEmail.setError(null);
            layoutAccountEmail.setErrorEnabled(false);
            return true;
        }
    }
}