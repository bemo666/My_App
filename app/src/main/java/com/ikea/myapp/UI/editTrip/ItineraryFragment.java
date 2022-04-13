package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.PlanActivity;
import com.ikea.myapp.models.PlanFlight;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanHotel;
import com.ikea.myapp.models.PlanNote;
import com.ikea.myapp.models.PlanRental;
import com.ikea.myapp.R;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.utils.getCorrectDate;

import java.util.Date;
import java.util.TimeZone;

public class ItineraryFragment extends Fragment {
    private TextView dates;
    private final String id;
    private MyTrip trip;
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



    public ItineraryFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);
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
            Pair<Long, Long> currentRange = new Pair<>(Long.parseLong(trip.getStartStamp()), Long.parseLong(trip.getEndStamp()));
            MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker().setTitleText("Select new Date Range").setSelection(currentRange).setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                Pair<Date, Date> rangeDate = new Pair<>(new Date((Long) selection.first), new Date((Long) ((Pair) selection).second));
                TimeZone tz = TimeZone.getTimeZone(trip.getTimeZone());
                trip.setStartStamp(String.valueOf(rangeDate.first.getTime() - tz.getOffset(rangeDate.first.getTime())));
                trip.setEndStamp(String.valueOf(rangeDate.second.getTime() - tz.getOffset(rangeDate.first.getTime()) + 86399999));
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
        new Handler(Looper.getMainLooper()).postDelayed(() -> ((ItineraryRVAdapter.HeaderViewHolder) itineraryRV.findViewHolderForAdapterPosition(index)).forceExpand(), 200);
    }
}