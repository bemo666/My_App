package com.ikea.myapp.UI.profile;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
    private TextInputEditText accountEmail, accountFirstName, accountCurrency;
    private LinearLayout signInLayout, accountInfoLayout;
    private boolean nameChanged = false, currencyChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Initializing the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //Setting the Actionbar attributes
        setTitle(getString(R.string.ui_account));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Linking xml objects to java variables
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        accountEmail = findViewById(R.id.accountEmail);
        accountFirstName = findViewById(R.id.accountFirstName);
        accountCurrency = findViewById(R.id.accountCurrency);
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
        accountCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currencyChanged = true;
                Log.d("tag", "Currency changed");
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
                Log.d("tag", "Name changed");
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
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.sign_out_button) {
            if (FirebaseManager.loggedIn()) {
                FirebaseManager.SignOut();
                Toast.makeText(ProfileActivity.this, getString(R.string.login_logout_successful), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            }
        } else if (id == R.id.delete_account_button) {
            if (FirebaseManager.loggedIn()) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(R.string.profile_delete_account);
                alertDialog.setMessage(R.string.profile_delete_account_dialog_text);

                alertDialog.setPositiveButton("Delete", (dialog, which) ->
                        FirebaseManager.DeleteAccount().addOnCompleteListener(task -> {
                            //userdata.removeValue()
                            if(task.isSuccessful()) {
                                Toast.makeText(this, R.string.profile_account_deleted, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                            } else{
                                Toast.makeText(this, task.getException().toString(), Toast.LENGTH_LONG).show();
                                Log.d("tag", "Account deletion failed: " + task.getException().toString());
                            }
                        }));
                alertDialog.setNegativeButton("Never Mind", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        } else if (id == R.id.save_button) {
            if (nameChanged) {
                nameChanged = false;
                saveButton.setEnabled(false);
                viewModel.setName(Objects.requireNonNull(accountFirstName.getText()).toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Name updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update Name", Toast.LENGTH_SHORT).show();
                    }
                });

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


}