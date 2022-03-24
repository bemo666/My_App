package com.ikea.myapp.UI.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.LoginActivity;
import com.ikea.myapp.UI.main.MainActivity;
import com.ikea.myapp.data.remote.FirebaseManager;

import java.util.Currency;
import java.util.Objects;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    //Declaring Variables
    private ProfileViewModel viewModel;
    private MaterialButton signInButton, signOutButton, deleteAccountButton, saveButton;
    private TextInputEditText accountEmail, accountFirstName;
    private LinearLayout signInLayout, accountInfoLayout;
    private boolean nameChanged = false;
    private FirebaseManager firebaseManager;

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
        signInButton = findViewById(R.id.firebase_button);
        signInLayout = findViewById(R.id.sign_in_linear_layout);
        accountInfoLayout = findViewById(R.id.account_info_linear_layout);
        signOutButton = findViewById(R.id.sign_out_button);
        deleteAccountButton = findViewById(R.id.delete_account_button);
        saveButton = findViewById(R.id.save_button);
        firebaseManager = new FirebaseManager();

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
                firebaseManager.SignOut();
                Toast.makeText(ProfileActivity.this, getString(R.string.login_logout_successful), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
            }
        } else if (id == R.id.delete_account_button) {
            if (FirebaseManager.loggedIn()) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme_Delete);
                alertDialog.setTitle(R.string.profile_delete_account);
                alertDialog.setMessage(R.string.profile_delete_account_dialog_text);

                alertDialog.setPositiveButton("Delete", (dialog, which) ->
                        firebaseManager.DeleteAccount().addOnCompleteListener(task -> {
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
                        Log.d("tag", "Firebase Error: " + task.getException());
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
            nameChanged = false;
        });
        accountEmail.setText(viewModel.getEmail(), TextView.BufferType.EDITABLE);
    }

}