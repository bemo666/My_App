package com.ikea.myapp.UI.editTrip;

import static com.ikea.myapp.utils.Utils.prettyPrint;

import android.content.Context;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.ikea.myapp.R;
import com.ikea.myapp.models.CustomCurrency;
import com.ikea.myapp.models.Expense;
import com.ikea.myapp.models.ExpenseTypes;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.models.PlanViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

public class ItineraryInternalRVAdapter extends RecyclerView.Adapter<PlanViewHolder> {

    private final List<Plan> plans;
    private final PlanType type;
    private final ItineraryRVAdapter parentAdapter;
    private final InputMethodManager imm;
    private final ItineraryFragment fragment;
    private final CustomCurrency currency;

    public ItineraryInternalRVAdapter(ItineraryRVAdapter parentAdapter, PlanHeader header, ItineraryFragment fragment, CustomCurrency currency) {
        this.plans = header.getPlans();
        this.type = header.getType();
        this.parentAdapter = parentAdapter;
        this.fragment = fragment;
        this.imm = (InputMethodManager) fragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        this.currency = currency;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (type) {
            case Note:
                return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
            case Hotel:
                return new HotelViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
            case Flight:
                return new FlightViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
            case Rental:
                return new RentalViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
            case Activity:
                return new ActivityViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        holder.setDetails(plans.get(position));
    }


    @Override
    public int getItemCount() {
        return plans.size();
    }

    class NoteViewHolder extends PlanViewHolder {

        private final EditText text;
        private final ImageView delete;
        private final ImageView confirmDelete;
        private boolean deletePressed = false;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.note_text_edit_text);
            delete = itemView.findViewById(R.id.note_delete_ic);
            confirmDelete = itemView.findViewById(R.id.note_confirm_delete);

        }

        public void setDetails(Plan plan) {
            text.setText(plan.getNote());
            text.setOnKeyListener((view, i, keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_ENTER) {
                        imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
                        plan.setNote(text.getText().toString());
                        parentAdapter.editPlan(plan, plans.indexOf(plan));
                    }
                }
                return false;
            });

            delete.setOnClickListener(view -> {
                if (deletePressed) {
                    notifyItemRemoved(plans.indexOf(plan));
                    plans.remove(plan);

                    parentAdapter.deletePlan(plan);
                    deletePressed = false;
                } else {
                    deletePressed = true;
                    confirmDelete.setVisibility(View.VISIBLE);
                    Handler handler = new android.os.Handler();
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);

                }
            });
        }

    }

    class RentalViewHolder extends PlanViewHolder {

        TextInputEditText pickupAddress, pickupTime, pickupDate, dropoffAddress, dropoffTime, dropoffDate, carDetails, confirmationNum, cost;
        TextInputLayout pickupAddressLayout, dropoffAddressLayout;
        Plan plan;
        Button save, delete;
        boolean deletePressed = false;
        CardView confirmDelete;
        TextView currencySymbol;
        ImageView saveCheck;
        Animation fadeOut;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");


        RentalViewHolder(@NonNull View itemView) {
            super(itemView);

            pickupAddress = itemView.findViewById(R.id.rental_pick_up_address);
            pickupTime = itemView.findViewById(R.id.rental_pick_up_time);
            pickupDate = itemView.findViewById(R.id.rental_pick_up_date);
            dropoffAddress = itemView.findViewById(R.id.rental_drop_off_address);
            dropoffTime = itemView.findViewById(R.id.rental_drop_off_time);
            dropoffDate = itemView.findViewById(R.id.rental_drop_off_date);
            carDetails = itemView.findViewById(R.id.rental_car_details);
            confirmationNum = itemView.findViewById(R.id.rental_confirmation_number);
            cost = itemView.findViewById(R.id.rental_cost);
            currencySymbol = itemView.findViewById(R.id.rental_money_symbol);
            saveCheck = itemView.findViewById(R.id.rental_saved_check);

            pickupAddressLayout = itemView.findViewById(R.id.rental_pick_up_address_layout);
            dropoffAddressLayout = itemView.findViewById(R.id.rental_drop_off_address_layout);

            save = itemView.findViewById(R.id.rental_save);
            delete = itemView.findViewById(R.id.rental_delete);
            confirmDelete = itemView.findViewById(R.id.rental_confirm_delete);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));

            fadeOut = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.fade_out);
        }

        public void setDetails(Plan plan) {
            this.plan = plan;
            pickupAddress.setText(plan.getStartLocationAddress());
            dropoffAddress.setText(plan.getEndLocationAddress());
            carDetails.setText(plan.getNote());
            confirmationNum.setText(plan.getConfirmationNumber());
            CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
            constraints.setStart(fragment.getStartMonth());
            constraints.setEnd(fragment.getEndMonth());
            Long startTime, endTime;
            if (plan.getStartTime() != null) {
                startTime = plan.getStartTime();
            } else {
                startTime = null;
            }
            if (plan.getEndTime() != null) {
                endTime = plan.getEndTime();
            } else {
                endTime = null;
            }
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(startTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                pickupDate.setText(datePicker.getHeaderText());
                plan.setStartDate(selection);
            });
            pickupDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                dropoffDate.setText(datePicker2.getHeaderText());
                plan.setEndDate(selection);
            });
            dropoffDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                setStartTime();
            });
            pickupTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                setEndTime();
            });
            dropoffTime.setOnClickListener(view -> timePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            if (plan.getCost() != null) {
                currencySymbol.setText(currency.getSymbol());
                cost.setText(prettyPrint(plan.getCost().getPrice()));
            }

            setStartTime();
            setEndTime();
            setStartDate();
            setEndDate();

            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
                    savePlan();
                }
                return false;
            });
            save.setOnClickListener(view -> savePlan());
            delete.setOnClickListener(view -> {
                if (deletePressed) {
                    notifyItemRemoved(plans.indexOf(plan));
                    plans.remove(plan);
                    parentAdapter.deletePlan(plan);
                    deletePressed = false;
                } else {
                    deletePressed = true;
                    confirmDelete.setVisibility(View.VISIBLE);
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        private void setEndDate() {
            if (plan.getEndDate() != null) {
                Date end = new Date(plan.getEndDate());
                dropoffDate.setText(date.format(end));
            }
        }

        private void setStartDate() {
            if (plan.getStartDate() != null) {
                Date start = new Date(plan.getStartDate());
                pickupDate.setText(date.format(start));
            }
        }

        private void setEndTime() {
            if (plan.getEndTime() != null) {
                Date end = new Date(plan.getEndTime());
                dropoffTime.setText(time.format(end));
            }
        }

        private void setStartTime() {
            if (plan.getStartTime() != null) {
                Date start = new Date(plan.getStartTime());
                pickupTime.setText(time.format(start));
            }
        }

        private void savePlan() {
            plan.setStartLocationAddress(Objects.requireNonNull(pickupAddress.getText()).toString());
            plan.setEndLocationAddress(Objects.requireNonNull(dropoffAddress.getText()).toString());
            plan.setNote(Objects.requireNonNull(carDetails.getText()).toString());
            plan.setConfirmationNumber(Objects.requireNonNull(confirmationNum.getText()).toString());
            updateCost(Objects.requireNonNull(cost.getText()).toString());
            saveCheck.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> saveCheck.startAnimation(fadeOut), 700);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    saveCheck.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (plan.getCost() != null) {
                parentAdapter.editBudget(plan.getCost());
            }
            parentAdapter.editPlan(plan, plans.indexOf(plan));
        }

        private void updateCost(String editable) {
            if(!editable.equals("")) {
                if (plan.getCost() != null) {
                    if (!plan.getCost().hasId()) {
                        plan.getCost().setId(UUID.randomUUID().toString());
                    }
                    if (editable.equals("")) {
                        plan.getCost().setPrice(0.0);
                    } else
                        plan.getCost().setPrice(Double.parseDouble(editable));
                } else {
                    Expense newCost = new Expense(ExpenseTypes.Rental, Double.parseDouble(editable));
                    newCost.setId(UUID.randomUUID().toString());
                    plan.setCost(newCost);
                }
            }
        }


    }

    class FlightViewHolder extends PlanViewHolder {

        TextInputEditText departureAirport, departureTime, departureDate, arrivalAirport, arrivalTime, arrivalDate, cost, confirmationNumber, airline, flightNumber;
        TextInputLayout departureAirportLayout, departureTimeLayout, departureDateLayout, arrivalAirportLayout, arrivalTimeLayout, arrivalDateLayout;
        Plan plan;
        Button save, delete;
        boolean deletePressed = false;
        CardView confirmDelete;
        TextView currencySymbol;
        ImageView saveCheck;
        Animation fadeOut;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");


        FlightViewHolder(@NonNull View itemView) {
            super(itemView);

            departureAirport = itemView.findViewById(R.id.flight_departure_airport);
            departureTime = itemView.findViewById(R.id.flight_departure_time);
            departureDate = itemView.findViewById(R.id.flight_departure_date);
            arrivalAirport = itemView.findViewById(R.id.flight_arrival_airport);
            arrivalTime = itemView.findViewById(R.id.flight_arrival_time);
            arrivalDate = itemView.findViewById(R.id.flight_arrival_date);
            airline = itemView.findViewById(R.id.flight_airline);
            flightNumber = itemView.findViewById(R.id.flight_flight_number);
            confirmationNumber = itemView.findViewById(R.id.flight_confirmation_number);
            cost = itemView.findViewById(R.id.flight_cost);
            currencySymbol = itemView.findViewById(R.id.flight_money_symbol);
            saveCheck = itemView.findViewById(R.id.flight_saved_check);

            departureAirportLayout = itemView.findViewById(R.id.flight_departure_airport_layout);
            departureTimeLayout = itemView.findViewById(R.id.flight_departure_time_layout);
            departureDateLayout = itemView.findViewById(R.id.flight_departure_date_layout);
            arrivalAirportLayout = itemView.findViewById(R.id.flight_arrival_airport_layout);
            arrivalTimeLayout = itemView.findViewById(R.id.flight_arrival_time_layout);
            arrivalDateLayout = itemView.findViewById(R.id.flight_arrival_date_layout);

            save = itemView.findViewById(R.id.flight_save);
            delete = itemView.findViewById(R.id.flight_delete);
            confirmDelete = itemView.findViewById(R.id.flight_confirm_delete);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));

            fadeOut = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.fade_out);
        }


        public void setDetails(Plan plan) {
            this.plan = plan;
            departureAirportLayout.setEndIconOnClickListener(view -> fragment.openMap());
            arrivalAirportLayout.setEndIconOnClickListener(view -> fragment.openMap());
            departureAirport.setText(plan.getStartLocationAddress());
            arrivalAirport.setText(plan.getEndLocationAddress());
            flightNumber.setText(plan.getFlightCode());
            airline.setText(plan.getAirline());
            confirmationNumber.setText(plan.getConfirmationNumber());
            CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
            constraints.setStart(fragment.getStartMonth());
            constraints.setEnd(fragment.getEndMonth());
            Long startTime, endTime;
            if (plan.getStartTime() != null) {
                startTime = plan.getStartTime();
            } else {
                startTime = null;
            }
            if (plan.getEndTime() != null) {
                endTime = plan.getEndTime();
            } else {
                endTime = null;
            }
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(startTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                plan.setStartDate(selection);
                setStartDate();
            });
            departureDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                plan.setEndDate(selection);
                setEndDate();
            });
            arrivalDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                setStartTime();
            });
            departureTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                setEndTime();
            });
            arrivalTime.setOnClickListener(view -> timePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            if (plan.getCost() != null) {
                currencySymbol.setText(currency.getSymbol());
                cost.setText(prettyPrint(plan.getCost().getPrice()));
            }

            setStartTime();
            setEndTime();
            setStartDate();
            setEndDate();

            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
                    savePlan();
                }
                return false;
            });
            save.setOnClickListener(view -> savePlan());
            delete.setOnClickListener(view -> {
                if (deletePressed) {
                    notifyItemRemoved(plans.indexOf(plan));
                    plans.remove(plan);
                    parentAdapter.deletePlan(plan);
                    deletePressed = false;
                } else {
                    deletePressed = true;
                    confirmDelete.setVisibility(View.VISIBLE);
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        private void setEndDate() {
            if (plan.getEndDate() != null) {
                Date end = new Date(plan.getEndDate());
                arrivalDate.setText(date.format(end));
            }
        }

        private void setStartDate() {
            if (plan.getStartDate() != null) {
                Date start = new Date(plan.getStartDate());
                departureDate.setText(date.format(start));
            }
        }

        private void setEndTime() {
            if (plan.getEndTime() != null) {
                Date end = new Date(plan.getEndTime());
                arrivalTime.setText(time.format(end));
            }
        }

        private void setStartTime() {
            if (plan.getStartTime() != null) {
                Date start = new Date(plan.getStartTime());
                departureTime.setText(time.format(start));
            }
        }

        private void savePlan() {
            updateCost(Objects.requireNonNull(cost.getText()).toString());
            plan.setStartLocationAddress(Objects.requireNonNull(departureAirport.getText()).toString());
            plan.setEndLocationAddress(Objects.requireNonNull(arrivalAirport.getText()).toString());
            plan.setFlightCode(Objects.requireNonNull(flightNumber.getText()).toString());
            plan.setAirline(Objects.requireNonNull(airline.getText()).toString());
            plan.setConfirmationNumber(Objects.requireNonNull(confirmationNumber.getText()).toString());
            saveCheck.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> saveCheck.startAnimation(fadeOut), 700);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    saveCheck.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (plan.getCost() != null) {
                parentAdapter.editBudget(plan.getCost());
            }
            parentAdapter.editPlan(plan, plans.indexOf(plan));
        }

        private void updateCost(String editable) {
            if(!editable.equals("")) {
                if (plan.getCost() != null) {
                    if (!plan.getCost().hasId()) {
                        plan.getCost().setId(UUID.randomUUID().toString());
                    }
                    if (editable.equals("")) {
                        plan.getCost().setPrice(0.0);
                    } else
                        plan.getCost().setPrice(Double.parseDouble(editable));
                } else {
                    Expense newCost = new Expense(ExpenseTypes.Flight, Double.parseDouble(editable));
                    newCost.setId(UUID.randomUUID().toString());
                    plan.setCost(newCost);
                }
            }
        }
    }

    class HotelViewHolder extends PlanViewHolder {

        TextInputEditText address, checkInTime, checkInDate, checkOutTime, checkOutDate, cost, confirmationNumber;
        TextInputLayout addressLayout;
        Plan plan;
        Button save, delete;
        boolean deletePressed = false;
        CardView confirmDelete;
        TextView currencySymbol;
        ImageView saveCheck;
        Animation fadeOut;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");


        HotelViewHolder(@NonNull View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.hotel_address);
            checkInTime = itemView.findViewById(R.id.hotel_check_in_time);
            checkInDate = itemView.findViewById(R.id.hotel_check_in_date);
            checkOutTime = itemView.findViewById(R.id.hotel_check_out_time);
            checkOutDate = itemView.findViewById(R.id.hotel_check_out_date);
            confirmationNumber = itemView.findViewById(R.id.hotel_confirmation_number);
            cost = itemView.findViewById(R.id.hotel_cost);
            currencySymbol = itemView.findViewById(R.id.hotel_money_symbol);

            addressLayout = itemView.findViewById(R.id.hotel_address_layout);

            save = itemView.findViewById(R.id.hotel_save);
            delete = itemView.findViewById(R.id.hotel_delete);
            confirmDelete = itemView.findViewById(R.id.hotel_confirm_delete);
            saveCheck = itemView.findViewById(R.id.hotel_saved_check);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));

            fadeOut = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.fade_out);
        }


        public void setDetails(Plan plan) {
            this.plan = plan;
            addressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            address.setText(plan.getStartLocationAddress());
            confirmationNumber.setText(plan.getConfirmationNumber());
            if (plan.getCost() != null) {
                currencySymbol.setText(currency.getSymbol());
                cost.setText(prettyPrint(plan.getCost().getPrice()));
            }
            CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
            constraints.setStart(fragment.getStartMonth());
            constraints.setEnd(fragment.getEndMonth());
            Long startTime, endTime;
            if (plan.getStartTime() != null) {
                startTime = plan.getStartTime();
            } else {
                startTime = null;
            }
            if (plan.getEndTime() != null) {
                endTime = plan.getEndTime();
            } else {
                endTime = null;
            }
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(startTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                plan.setStartDate(selection);
                setStartDate();
            });
            checkInDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                plan.setEndDate(selection);
                setEndDate();
            });
            checkOutDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                setStartTime();
            });
            checkInTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                setEndTime();
            });
            checkOutTime.setOnClickListener(view -> timePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            setStartTime();
            setEndTime();
            setStartDate();
            setEndDate();

            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
                    savePlan();
                }
                return false;
            });
            save.setOnClickListener(view -> savePlan());
            delete.setOnClickListener(view -> {
                if (deletePressed) {
                    notifyItemRemoved(plans.indexOf(plan));
                    plans.remove(plan);
                    parentAdapter.deletePlan(plan);
                    deletePressed = false;
                } else {
                    deletePressed = true;
                    confirmDelete.setVisibility(View.VISIBLE);
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        private void setEndDate() {
            if (plan.getEndDate() != null) {
                Date end = new Date(plan.getEndDate());
                checkOutDate.setText(date.format(end));
            }
        }

        private void setStartDate() {
            if (plan.getStartDate() != null) {
                Date start = new Date(plan.getStartDate());
                checkInDate.setText(date.format(start));
            }
        }

        private void setEndTime() {
            if (plan.getEndTime() != null) {
                Date end = new Date(plan.getEndTime());
                checkOutTime.setText(time.format(end));
            }
        }

        private void setStartTime() {
            if (plan.getStartTime() != null) {
                Date start = new Date(plan.getStartTime());
                checkInTime.setText(time.format(start));
            }
        }

        private void savePlan() {
            plan.setStartLocationAddress(Objects.requireNonNull(address.getText()).toString());
            plan.setConfirmationNumber(Objects.requireNonNull(confirmationNumber.getText()).toString());
            updateCost(Objects.requireNonNull(cost.getText()).toString());
            saveCheck.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> saveCheck.startAnimation(fadeOut), 700);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    saveCheck.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (plan.getCost() != null) {
                parentAdapter.editBudget(plan.getCost());
            }
            parentAdapter.editPlan(plan, plans.indexOf(plan));
        }

        private void updateCost(String editable) {
            if(!editable.equals("")) {
                if (plan.getCost() != null) {
                    if (!plan.getCost().hasId()) {
                        plan.getCost().setId(UUID.randomUUID().toString());
                    }
                    if (editable.equals("")) {
                        plan.getCost().setPrice(0.0);
                    } else

                        plan.getCost().setPrice(Double.parseDouble(editable));
                } else {
                    Expense newCost = new Expense(ExpenseTypes.Flight, Double.parseDouble(editable));
                    newCost.setId(UUID.randomUUID().toString());
                    plan.setCost(newCost);
                }
            }
        }
    }

    class ActivityViewHolder extends PlanViewHolder {


        TextInputEditText name, address, startTime, startDate, endTime, endDate, cost, confirmationNumber, notes;
        TextInputLayout addressLayout;
        SwitchMaterial endSwitch;
        Plan plan;
        Button save, delete;
        boolean deletePressed = false;
        CardView confirmDelete;
        TextView currencySymbol;
        ImageView saveCheck;
        LinearLayout endLayout;
        Animation fadeOut;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");


        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.activity_name);
            address = itemView.findViewById(R.id.activity_address);
            startTime = itemView.findViewById(R.id.activity_start_time);
            startDate = itemView.findViewById(R.id.activity_start_date);
            endTime = itemView.findViewById(R.id.activity_end_time);
            endDate = itemView.findViewById(R.id.activity_end_date);
            confirmationNumber = itemView.findViewById(R.id.activity_confirmation_number);
            cost = itemView.findViewById(R.id.activity_cost);
            currencySymbol = itemView.findViewById(R.id.activity_money_symbol);
            notes = itemView.findViewById(R.id.activity_notes);

            addressLayout = itemView.findViewById(R.id.activity_address_layout);
            endLayout = itemView.findViewById(R.id.activity_end_layout);
            endSwitch = itemView.findViewById(R.id.activity_end_switch);

            save = itemView.findViewById(R.id.activity_save);
            delete = itemView.findViewById(R.id.activity_delete);
            confirmDelete = itemView.findViewById(R.id.activity_confirm_delete);
            saveCheck = itemView.findViewById(R.id.activity_saved_check);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));

            fadeOut = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.fade_out);
        }


        public void setDetails(Plan plan) {
            this.plan = plan;
            endSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                plan.setHasEnd(b);
                setSwitch();
            });
            setSwitch();
            endSwitch.setChecked(plan.isHasEnd());
            addressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            address.setText(plan.getStartLocationAddress());
            name.setText(plan.getName());
            notes.setText(plan.getNote());
            confirmationNumber.setText(plan.getConfirmationNumber());
            CalendarConstraints.Builder constraints = new CalendarConstraints.Builder();
            constraints.setStart(fragment.getStartMonth());
            constraints.setEnd(fragment.getEndMonth());
            Long startTimeLong, endTimeLong;
            if (plan.getStartTime() != null) {
                startTimeLong = plan.getStartTime();
            } else {
                startTimeLong = null;
            }
            if (plan.getEndTime() != null) {
                endTimeLong = plan.getEndTime();
            } else {
                endTimeLong = null;
            }
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(startTimeLong)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker.addOnPositiveButtonClickListener(selection -> {
                plan.setStartDate(selection);
                setStartDate();
            });
            startDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTimeLong)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                plan.setEndDate(selection);
                setEndDate();
            });
            endDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                setStartTime();
            });
            startTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                setEndTime();
            });
            endTime.setOnClickListener(view -> timePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            if (plan.getCost() != null) {
                currencySymbol.setText(currency.getSymbol());
                cost.setText(prettyPrint(plan.getCost().getPrice()));
            }

            setStartTime();
            setEndTime();
            setStartDate();
            setEndDate();

            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
                    savePlan();
                }
                return false;
            });
            save.setOnClickListener(view -> savePlan());
            delete.setOnClickListener(view -> {
                if (deletePressed) {
                    notifyItemRemoved(plans.indexOf(plan));
                    plans.remove(plan);
                    parentAdapter.deletePlan(plan);
                    deletePressed = false;
                } else {
                    deletePressed = true;
                    confirmDelete.setVisibility(View.VISIBLE);
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        private void setSwitch() {
            if (plan.isHasEnd()) {
                endLayout.setVisibility(View.VISIBLE);
                setEndDate();
                setEndTime();
            } else {
                endLayout.setVisibility(View.GONE);
                plan.setEndDate(null);
                plan.setEndTime(null);
            }
        }

        private void setEndDate() {
            if (plan.getEndDate() != null) {
                Date end = new Date(plan.getEndDate());
                endDate.setText(date.format(end));
            }
        }

        private void setStartDate() {
            if (plan.getStartDate() != null) {
                Date start = new Date(plan.getStartDate());
                startDate.setText(date.format(start));
            }
        }

        private void setEndTime() {
            if (plan.getEndTime() != null) {
                Date end = new Date(plan.getEndTime());
                endTime.setText(time.format(end));
            }
        }

        private void setStartTime() {
            if (plan.getStartTime() != null) {
                Date start = new Date(plan.getStartTime());
                startTime.setText(time.format(start));
            }
        }

        private void savePlan() {
            plan.setStartLocationAddress(Objects.requireNonNull(address.getText()).toString());
            plan.setName(Objects.requireNonNull(name.getText()).toString());
            plan.setConfirmationNumber(Objects.requireNonNull(confirmationNumber.getText()).toString());
            plan.setNote(Objects.requireNonNull(notes.getText()).toString());
            updateCost(Objects.requireNonNull(cost.getText()).toString());
            saveCheck.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> saveCheck.startAnimation(fadeOut), 700);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    saveCheck.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (plan.getCost() != null) {
                parentAdapter.editBudget(plan.getCost());
            }
            parentAdapter.editPlan(plan, plans.indexOf(plan));
        }

        private void updateCost(String editable) {
            if(!editable.equals("")) {
                if (plan.getCost() != null) {
                    if (!plan.getCost().hasId()) {
                        plan.getCost().setId(UUID.randomUUID().toString());
                    }
                    if (editable.equals("")) {
                        plan.getCost().setPrice(0.0);
                    } else

                        plan.getCost().setPrice(Double.parseDouble(editable));
                } else {
                    Expense newCost = new Expense(ExpenseTypes.Flight, Double.parseDouble(editable));
                    newCost.setId(UUID.randomUUID().toString());
                    plan.setCost(newCost);
                }
            }
        }
    }

}
