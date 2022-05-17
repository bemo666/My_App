package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.ikea.myapp.R;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.utils.getCorrectDate;

import java.util.Date;
import java.util.Objects;

public class ItineraryFragment extends Fragment {
    private TextView dates;
    private final String id;
    protected MyTrip trip;
    private EditText nickname;
    private EditTripViewModel viewModel;
    private RecyclerView itineraryRV;
    private ItineraryRVAdapter rvAdapter;
    private Observer<MyTrip> observer;
    private getCorrectDate date;
    private LinearLayout mainLayout;
    private CardView newCard;
    private boolean nicknameChanged;
    private InputMethodManager imm;
    private View divider;
    private EditTripActivity editTripActivity;
    private View view;



    public ItineraryFragment(String id, EditTripActivity editTripActivity) {
        this.id = id;
        this.editTripActivity = editTripActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_itinerary, container, false);
        dates = view.findViewById(R.id.itinerary_dates);
        itineraryRV = view.findViewById(R.id.itinerary_recycler_view);
        nickname = view.findViewById(R.id.itinerary_nickname);
        mainLayout = view.findViewById(R.id.itinerary_layout);
        newCard = view.findViewById(R.id.itinerary_new_card);
        divider = view.findViewById(R.id.itinerary_divider);

        rvAdapter = new ItineraryRVAdapter(this);
        itineraryRV.setAdapter(rvAdapter);
        itineraryRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        observer = new Observer<MyTrip>() {
            @Override
            public void onChanged(MyTrip myTrip) {
                trip = myTrip;
                rvAdapter.setList(trip);
                showNewCard();
                date = new getCorrectDate(trip);
                dates.setText(date.getStartDatelongFormat() + getResources().getString(R.string.ui_dash) + date.getEndDateLongFormat());
                nickname.setText(trip.getNickname());
                viewModel.getTrip().removeObserver(this);
            }
        };
        viewModel = ViewModelProviders.of(requireActivity()).get(EditTripViewModel.class);
        viewModel.getTrip().observe(getViewLifecycleOwner(), observer);

        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nicknameChanged = true;
            }
        });
        nickname.setOnKeyListener((view2, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    updateNickname();
                    view2.clearFocus();
                    imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
                }
            }
            return false;
        });
        mainLayout.setOnClickListener(view2 -> {
            if(nicknameChanged){
                updateNickname();
            }
            view2.clearFocus();
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        });
        dates.setOnClickListener(view2 -> {
            Pair<Long, Long> currentRange = new Pair<>(trip.getStartStamp(), trip.getEndStamp());
            MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select new Date Range").setSelection(currentRange).setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selection.first), new Date((Long) ((Pair) selection).second));
                trip.setStartStamp(rangeDate.first.getTime());
                trip.setEndStamp(rangeDate.second.getTime() + 86399999);
                view2.clearFocus();
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                viewModel.updateTrip(trip);
                dates.setText(date.getStartDatelongFormat() + getResources().getString(R.string.ui_dash) + date.getEndDateLongFormat());
            });
            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });

        return view;
    }


    public void showNewCard() {
        if (trip.getPlans() != null) {
            if (trip.getPlans().size() == 0) {
                newCard.setVisibility(View.VISIBLE);
                divider.setVisibility(View.GONE);
            } else {
                newCard.setVisibility(View.GONE);
                divider.setVisibility(View.VISIBLE);

            }
        }   else {
            newCard.setVisibility(View.VISIBLE);
            divider.setVisibility(View.GONE);
        }
    }

    private void updateNickname() {
        nicknameChanged = false;
        trip.setNickname(nickname.getText().toString());
        updateTrip(trip);
    }

    public void updateTrip(MyTrip trip) {
        this.trip = trip;
        showNewCard();
        viewModel.updateTrip(trip);
    }

    public void checkForAddHeader(PlanType type){
        rvAdapter.checkForAddHeader(type);
    }

    public void expandHeader(int index) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> ((ItineraryRVAdapter.HeaderViewHolder) Objects.requireNonNull(itineraryRV.findViewHolderForAdapterPosition(index))).forceExpand(), 200);
    }

    public long getStartMonth() {
        return trip.getStartStamp();
    }

    public long getEndMonth() {
        return trip.getEndStamp();
    }

    public void openMap() {
        editTripActivity.openMapsFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        view.clearFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        rvAdapter.onActivityResult(requestCode, resultCode, data);
    }

    public boolean verifyPlan(Plan plan) {
        return trip.getPlans().contains(plan);
    }
}