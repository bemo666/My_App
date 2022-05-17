package com.ikea.myapp.UI.editTrip;

import static android.app.Activity.RESULT_OK;
import static com.ikea.myapp.utils.Utils.getNumber;
import static com.ikea.myapp.utils.Utils.prettyPrint;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.ikea.myapp.R;
import com.ikea.myapp.VibrationService;
import com.ikea.myapp.models.CustomCurrency;
import com.ikea.myapp.models.Expense;
import com.ikea.myapp.models.ExpenseTypes;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.models.PlanViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private List<PlanViewHolder> viewHolders;
    List<Place.Field> fieldList;
    Intent intent;

    public ItineraryInternalRVAdapter(ItineraryRVAdapter parentAdapter, PlanHeader header, ItineraryFragment fragment, CustomCurrency currency) {
        this.plans = header.getPlans();
        this.type = header.getType();
        this.parentAdapter = parentAdapter;
        this.fragment = fragment;
        this.imm = (InputMethodManager) fragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        this.currency = currency;
        if (!Places.isInitialized()) {
            Places.initialize(fragment.requireContext(), fragment.getString(R.string.g_apiKey));
        }
        fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID, Place.Field.ADDRESS_COMPONENTS,
                Place.Field.OPENING_HOURS, Place.Field.PHONE_NUMBER, Place.Field.PRICE_LEVEL, Place.Field.RATING, Place.Field.WEBSITE_URI, Place.Field.BUSINESS_STATUS,
                Place.Field.TYPES, Place.Field.USER_RATINGS_TOTAL);
        intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                , fieldList).build(fragment.requireContext());
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlanViewHolder viewHolder = null;
        switch (type) {
            case Note:
                viewHolder = new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
                break;
            case Hotel:
                viewHolder = new HotelViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
                break;
            case Flight:
                viewHolder = new FlightViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
                break;
            case Rental:
                viewHolder = new RentalViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
                break;
            case Activity:
                viewHolder = new ActivityViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
                break;
        }
        if (viewHolders == null) {
            viewHolders = new ArrayList<>();
        }
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        holder.setDetails(plans.get(position));
    }


    @Override
    public int getItemCount() {
        return plans.size();
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        if (viewHolders != null)
            for (PlanViewHolder viewHolder : viewHolders) {
                viewHolder.onResult(requestCode, resultCode, data);
            }
    }

    private Plan putResultsInStart(Place result, Plan plan) {
        int num = plans.indexOf(plan);
        if (result.getName() != null)
            plan.setStartLocation(result.getName());
        if (result.getId() != null)
            plan.setStartLocationId(result.getId());
        if (result.getAddress() != null)
            plan.setStartLocationAddress(result.getAddress());
        if (result.getBusinessStatus() != null)
            plan.setStartLocationStatus(result.getBusinessStatus().name());
        if (result.getLatLng() != null)
            plan.setStartLocationLat(result.getLatLng().latitude);
        if (result.getLatLng() != null)
            plan.setStartLocationLong(result.getLatLng().longitude);
        if (result.getPhoneNumber() != null)
            plan.setStartLocationPhoneNumber(result.getPhoneNumber());
        if (result.getRating() != null)
            plan.setStartLocationRating(result.getRating());
        if (result.getUserRatingsTotal() != null)
            plan.setStartLocationRatingCount(result.getUserRatingsTotal());
        if (result.getWebsiteUri() != null)
            plan.setStartLocationUrl(result.getWebsiteUri().toString());
        if (result.getTypes() != null) {
            List<String> list = new ArrayList<>();
            for (Place.Type t : result.getTypes()) {
                String type = t.toString().replaceAll("_", " ").toLowerCase();
                list.add(type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
            }
            plan.setStartEstablishmentTypes(list);
        }
        if (result.getOpeningHours() != null) {
            List<String> list = new ArrayList<>(result.getOpeningHours().getWeekdayText());
            plan.setStartLocationTimes(list);
        }
        if (num < plans.size() && num != -1) {
            parentAdapter.editPlan(plan);
        }
        return plan;
    }

    private Plan putResultsInEnd(Place result, Plan plan) {
        int num = plans.indexOf(plan);
        if (result.getName() != null)
            plan.setEndLocation(result.getName());
        if (result.getId() != null)
            plan.setEndLocationId(result.getId());
        if (result.getAddress() != null)
            plan.setEndLocationAddress(result.getAddress());
        if (result.getBusinessStatus() != null)
            plan.setEndLocationStatus(result.getBusinessStatus().name());
        if (result.getLatLng() != null)
            plan.setEndLocationLat(result.getLatLng().latitude);
        if (result.getLatLng() != null)
            plan.setEndLocationLong(result.getLatLng().longitude);
        if (result.getPhoneNumber() != null)
            plan.setEndLocationPhoneNumber(result.getPhoneNumber());
        if (result.getRating() != null)
            plan.setEndLocationRating(result.getRating());
        if (result.getUserRatingsTotal() != null)
            plan.setEndLocationRatingCount(result.getUserRatingsTotal());
        if (result.getWebsiteUri() != null)
            plan.setEndLocationUrl(result.getWebsiteUri().toString());
        if (result.getTypes() != null) {
            List<String> list = new ArrayList<>();
            for (Place.Type t : result.getTypes()) {
                String type = t.toString().replaceAll("_", " ").toLowerCase();
                list.add(type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase());
            }
            plan.setEndEstablishmentTypes(list);
        }
        if (result.getOpeningHours() != null) {
            List<String> list = new ArrayList<>(result.getOpeningHours().getWeekdayText());
            plan.setEndLocationTimes(list);
        }
        if (num < plans.size() && num != -1) {
            parentAdapter.editPlan(plan);
        }
        return plan;
    }

    class NoteViewHolder extends PlanViewHolder {

        private final EditText text;
        private final ImageView delete, confirmDelete;
        private boolean deletePressed;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.note_text_edit_text);
            delete = itemView.findViewById(R.id.note_delete_ic);
            confirmDelete = itemView.findViewById(R.id.note_confirm_delete);
        }

        public void setDetails(Plan plan) {
            text.setText(plan.getNote());
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setNote(editable.toString());
                    parentAdapter.editPlan(plan);
                }
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

        @Override
        public void onResult(int requestCode, int resultCode, Intent data) {
        }
    }

    class RentalViewHolder extends PlanViewHolder {

        TextInputEditText pickupName, pickupAddress, pickupTime, pickupDate, dropoffName, dropoffAddress, dropoffTime, dropoffDate, carDetails, confirmationNum, cost;
        TextInputLayout pickupAddressLayout, dropoffAddressLayout;
        Plan plan;
        ImageView delete;
        boolean deletePressed = false;
        ImageView confirmDelete;
        TextView currencySymbol;
        Animation fadeOut;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");
        final int PICKUP_ADDRESS_REQUEST = getNumber();
        final int DROPOFF_ADDRESS_REQUEST = getNumber();


        RentalViewHolder(@NonNull View itemView) {
            super(itemView);

            pickupName = itemView.findViewById(R.id.rental_pick_up);
            pickupAddress = itemView.findViewById(R.id.rental_pick_up_address);
            pickupTime = itemView.findViewById(R.id.rental_pick_up_time);
            pickupDate = itemView.findViewById(R.id.rental_pick_up_date);
            dropoffName = itemView.findViewById(R.id.rental_drop_off);
            dropoffAddress = itemView.findViewById(R.id.rental_drop_off_address);
            dropoffTime = itemView.findViewById(R.id.rental_drop_off_time);
            dropoffDate = itemView.findViewById(R.id.rental_drop_off_date);
            carDetails = itemView.findViewById(R.id.rental_car_details);
            confirmationNum = itemView.findViewById(R.id.rental_confirmation_number);
            cost = itemView.findViewById(R.id.rental_cost);
            currencySymbol = itemView.findViewById(R.id.rental_money_symbol);

            pickupAddressLayout = itemView.findViewById(R.id.rental_pick_up_address_layout);
            dropoffAddressLayout = itemView.findViewById(R.id.rental_drop_off_address_layout);

            delete = itemView.findViewById(R.id.rental_delete_ic);
            confirmDelete = itemView.findViewById(R.id.rental_confirm_delete);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));

            fadeOut = AnimationUtils.loadAnimation(fragment.getContext(), R.anim.fade_out);
        }

        public void setDetails(Plan plan) {
            this.plan = plan;
            dropoffAddressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            pickupAddressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            pickupAddress.setOnClickListener(view -> fragment.startActivityForResult(intent, PICKUP_ADDRESS_REQUEST));
            pickupName.setOnClickListener(view -> fragment.startActivityForResult(intent, PICKUP_ADDRESS_REQUEST));
            dropoffAddress.setOnClickListener(view -> fragment.startActivityForResult(intent, DROPOFF_ADDRESS_REQUEST));
            dropoffName.setOnClickListener(view -> fragment.startActivityForResult(intent, DROPOFF_ADDRESS_REQUEST));
            carDetails.setText(plan.getNote());
            carDetails.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int num = plans.indexOf(plan);
                    plan.setNote(editable.toString());
                    if (num < plans.size() && num != -1) {
                        parentAdapter.editPlan(plan);
                    }
                }
            });
            confirmationNum.setText(plan.getConfirmationNumber());
            confirmationNum.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setConfirmationNumber(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateCost(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
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
                parentAdapter.editPlan(plan);
            });
            pickupDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                dropoffDate.setText(datePicker2.getHeaderText());
                plan.setEndDate(selection);
                parentAdapter.editPlan(plan);
            });
            dropoffDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
                setStartTime();
            });
            pickupTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
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
            setStartAddress();
            setEndAddress();
            setStartName();
            setEndName();
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateCost(editable.toString());
                }
            });
            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
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
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        @Override
        public void onResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == PICKUP_ADDRESS_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInStart(result, plan);
                    pickupAddressLayout.setEndIconOnClickListener(view -> fragment.openMap());
                    setStartAddress();
                    setStartName();
                } else if (requestCode == DROPOFF_ADDRESS_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInEnd(result, plan);
                    dropoffAddressLayout.setEndIconOnClickListener(view -> fragment.openMap());
                    setEndAddress();
                    setEndName();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
                Toast.makeText(fragment.requireContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
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

        private void setEndAddress() {
            if (plan.getEndLocationAddress() != null) {
                dropoffAddress.setText(plan.getEndLocationAddress());
            }
        }

        private void setStartAddress() {
            if (plan.getStartLocationAddress() != null) {
                pickupAddress.setText(plan.getStartLocationAddress());
            }
        }

        private void setStartName() {
            if (plan.getStartLocation() != null) {
                pickupAddress.setText(plan.getStartLocation());
            }
        }

        private void setEndName() {
            if (plan.getEndLocation() != null) {
                pickupAddress.setText(plan.getEndLocation());
            }
        }

        private void updateCost(String editable) {
            int num = plans.indexOf(plan);
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
            parentAdapter.editBudget(plan.getCost());
            if (num < plans.size() && num != -1) {
                parentAdapter.editPlan(plan);
            }
        }
    }

    class FlightViewHolder extends PlanViewHolder {

        TextInputEditText departureAirport, departureTime, departureDate, arrivalAirport, arrivalTime, arrivalDate, cost, confirmationNumber, airline, flightNumber;
        TextInputLayout departureAirportLayout, departureTimeLayout, departureDateLayout, arrivalAirportLayout, arrivalTimeLayout, arrivalDateLayout;
        Plan plan;
        ImageView delete;
        boolean deletePressed = false;
        ImageView confirmDelete;
        TextView currencySymbol;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");
        final int DEPARTURE_AIRPORT_REQUEST = getNumber();
        final int ARRIVAL_AIRPORT_REQUEST = getNumber();

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

            departureAirportLayout = itemView.findViewById(R.id.flight_departure_airport_layout);
            departureTimeLayout = itemView.findViewById(R.id.flight_departure_time_layout);
            departureDateLayout = itemView.findViewById(R.id.flight_departure_date_layout);
            arrivalAirportLayout = itemView.findViewById(R.id.flight_arrival_airport_layout);
            arrivalTimeLayout = itemView.findViewById(R.id.flight_arrival_time_layout);
            arrivalDateLayout = itemView.findViewById(R.id.flight_arrival_date_layout);

            delete = itemView.findViewById(R.id.flight_delete_ic);
            confirmDelete = itemView.findViewById(R.id.flight_confirm_delete);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));

        }


        public void setDetails(Plan plan) {
            this.plan = plan;
            departureAirportLayout.setEndIconOnClickListener(view -> fragment.openMap());
            arrivalAirportLayout.setEndIconOnClickListener(view -> fragment.openMap());
            departureAirport.setOnClickListener(view -> fragment.startActivityForResult(intent, DEPARTURE_AIRPORT_REQUEST));
            arrivalAirport.setOnClickListener(view -> fragment.startActivityForResult(intent, ARRIVAL_AIRPORT_REQUEST));
            flightNumber.setText(plan.getFlightCode());
            flightNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setFlightCode(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
            airline.setText(plan.getAirline());
            airline.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setAirline(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
            confirmationNumber.setText(plan.getConfirmationNumber());
            confirmationNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setConfirmationNumber(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
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
                parentAdapter.editPlan(plan);
                setStartDate();
            });
            departureDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                plan.setEndDate(selection);
                parentAdapter.editPlan(plan);
                setEndDate();
            });
            arrivalDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
                setStartTime();
            });
            departureTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
                setEndTime();
            });
            arrivalTime.setOnClickListener(view -> timePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            if (plan.getCost() != null) {
                currencySymbol.setText(currency.getSymbol());
                cost.setText(prettyPrint(plan.getCost().getPrice()));
            }
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateCost(editable.toString());
                    parentAdapter.editPlan(plan);
                    parentAdapter.editBudget(plan.getCost());
                }
            });

            setStartTime();
            setEndTime();
            setStartDate();
            setEndDate();
            setStartAddress();
            setEndAddress();

            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
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
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        @Override
        public void onResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == DEPARTURE_AIRPORT_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInStart(result, plan);
                    setStartAddress();
                } else if (requestCode == ARRIVAL_AIRPORT_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInEnd(result, plan);
                    setEndAddress();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
                Toast.makeText(fragment.requireContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
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

        private void setEndAddress() {
            if (plan.getEndLocation() != null) {
                arrivalAirport.setText(plan.getEndLocation());
            }
        }

        private void setStartAddress() {
            if (plan.getStartLocation() != null) {
                departureAirport.setText(plan.getStartLocation());
            }
        }

        private void updateCost(String editable) {
            int num = plans.indexOf(plan);
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
            parentAdapter.editBudget(plan.getCost());
            if (num < plans.size() && num != -1) {
                parentAdapter.editPlan(plan);
            }

        }
    }

    class HotelViewHolder extends PlanViewHolder {

        TextInputEditText address, checkInTime, checkInDate, checkOutTime, checkOutDate, cost, confirmationNumber, notes, name;
        TextInputLayout addressLayout;
        Plan plan;
        ImageView delete, confirmDelete;
        boolean deletePressed = false;
        TextView currencySymbol;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");
        final int HOTEL_ADDRESS_REQUEST = getNumber();


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
            notes = itemView.findViewById(R.id.hotel_notes);
            name = itemView.findViewById(R.id.hotel_name);

            addressLayout = itemView.findViewById(R.id.hotel_address_layout);

            delete = itemView.findViewById(R.id.hotel_delete_ic);
            confirmDelete = itemView.findViewById(R.id.hotel_confirm_delete);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));

        }


        public void setDetails(Plan plan) {
            this.plan = plan;
            addressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            address.setOnClickListener(view -> fragment.startActivityForResult(intent, HOTEL_ADDRESS_REQUEST));
            name.setOnClickListener(view -> fragment.startActivityForResult(intent, HOTEL_ADDRESS_REQUEST));
            confirmationNumber.setText(plan.getConfirmationNumber());
            confirmationNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setConfirmationNumber(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
            notes.setText(plan.getNote());
            notes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setNote(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
            if (plan.getCost() != null) {
                currencySymbol.setText(currency.getSymbol());
                cost.setText(prettyPrint(plan.getCost().getPrice()));
            }
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateCost(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
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
                parentAdapter.editPlan(plan);
                setStartDate();
            });
            checkInDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTime)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                plan.setEndDate(selection);
                parentAdapter.editPlan(plan);
                setEndDate();
            });
            checkOutDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
                setStartTime();
            });
            checkInTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
                setEndTime();
            });
            checkOutTime.setOnClickListener(view -> timePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            setStartTime();
            setEndTime();
            setStartDate();
            setEndDate();
            setAddress();
            setName();


            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
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
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        @Override
        public void onResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == HOTEL_ADDRESS_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInStart(result, plan);
                    setAddress();
                    setName();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
                Toast.makeText(fragment.requireContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
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

        private void setAddress() {
            if (plan.getStartLocationAddress() != null) {
                address.setText(plan.getStartLocationAddress());
            }
        }

        private void setName() {
            if (plan.getStartLocation() != null) {
                name.setText(plan.getStartLocation());
            }
        }


        private void updateCost(String editable) {
            int num = plans.indexOf(plan);
            if (plan.getCost() != null) {
                if (!plan.getCost().hasId()) {
                    plan.getCost().setId(UUID.randomUUID().toString());
                }
                if (editable.equals("")) {
                    plan.getCost().setPrice(0.0);
                } else
                    plan.getCost().setPrice(Double.parseDouble(editable));
            } else {
                Expense newCost = new Expense(ExpenseTypes.Lodging, Double.parseDouble(editable));
                newCost.setId(UUID.randomUUID().toString());
                plan.setCost(newCost);
            }
            parentAdapter.editBudget(plan.getCost());
            if (num < plans.size() && num != -1) {
                parentAdapter.editPlan(plan);
            }
        }
    }

    class ActivityViewHolder extends PlanViewHolder {


        TextInputEditText name, address, startTime, startDate, endTime, endDate, cost, confirmationNumber, notes;
        TextInputLayout addressLayout;
        SwitchMaterial endSwitch;
        Plan plan;
        ImageView delete;
        boolean deletePressed = false;
        ImageView confirmDelete;
        TextView currencySymbol;
        LinearLayout endLayout;
        Handler handler = new android.os.Handler();
        DateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        DateFormat time = new SimpleDateFormat("HH:mm");
        final int ACTIVITY_ADDRESS_REQUEST = getNumber();

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

            delete = itemView.findViewById(R.id.activity_delete_ic);
            confirmDelete = itemView.findViewById(R.id.activity_confirm_delete);

            date.setTimeZone(TimeZone.getTimeZone("GMT"));
            time.setTimeZone(TimeZone.getTimeZone("GMT"));
        }


        public void setDetails(Plan plan) {
            this.plan = plan;
            endSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                fragment.requireActivity().startService(new Intent(fragment.requireActivity(), VibrationService.class));
                plan.setHasEnd(b);
                parentAdapter.editPlan(plan);
                setSwitch();
            });
            setSwitch();
            endSwitch.setChecked(plan.isHasEnd());
            addressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            address.setOnClickListener(view -> fragment.startActivityForResult(intent, ACTIVITY_ADDRESS_REQUEST));
            name.setOnClickListener(view -> fragment.startActivityForResult(intent, ACTIVITY_ADDRESS_REQUEST));
            name.setText(plan.getStartLocation());
            notes.setText(plan.getNote());
            notes.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setNote(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
            confirmationNumber.setText(plan.getConfirmationNumber());
            confirmationNumber.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    plan.setConfirmationNumber(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });
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
                parentAdapter.editPlan(plan);
                setStartDate();
            });
            startDate.setOnClickListener(view -> datePicker.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialDatePicker<Long> datePicker2 = MaterialDatePicker.Builder.datePicker().setCalendarConstraints(constraints.build()).setSelection(endTimeLong)
                    .setTheme(R.style.ThemeOverlay_App_DatePicker).build();
            datePicker2.addOnPositiveButtonClickListener(selection -> {
                plan.setEndDate(selection);
                parentAdapter.editPlan(plan);
                setEndDate();
            });
            endDate.setOnClickListener(view -> datePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "DATE_PICKER"));

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker.addOnPositiveButtonClickListener(view -> {
                plan.setStartTime(timePicker.getHour() * 3600000L + timePicker.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
                setStartTime();
            });
            startTime.setOnClickListener(view -> timePicker.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            MaterialTimePicker timePicker2 = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTheme(R.style.ThemeOverlay_App_TimePicker).build();
            timePicker2.addOnPositiveButtonClickListener(view -> {
                plan.setEndTime(timePicker2.getHour() * 3600000L + timePicker2.getMinute() * 60000L);
                parentAdapter.editPlan(plan);
                setEndTime();
            });
            endTime.setOnClickListener(view -> timePicker2.show(fragment.requireActivity().getSupportFragmentManager(), "TIME_PICKER"));

            if (plan.getCost() != null) {
                currencySymbol.setText(currency.getSymbol());
                cost.setText(prettyPrint(plan.getCost().getPrice()));
            }
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateCost(editable.toString());
                    parentAdapter.editPlan(plan);
                }
            });

            setStartTime();
            setEndTime();
            setStartDate();
            setEndDate();
            setAddress();
            setName();

            cost.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
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
                    handler.postDelayed(() -> {
                        deletePressed = false;
                        confirmDelete.setVisibility(View.GONE);
                    }, 3000);
                }
            });
        }

        @Override
        public void onResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == ACTIVITY_ADDRESS_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInStart(result, plan);
                    setAddress();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
                Toast.makeText(fragment.requireContext(), status.getStatusMessage(), Toast.LENGTH_LONG).show();
            }
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
                parentAdapter.editPlan(plan);
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

        private void setAddress() {
            if (plan.getStartLocationAddress() != null) {
                address.setText(plan.getStartLocationAddress());
            }
        }

        private void setName() {
            if (plan.getStartLocation() != null) {
                name.setText(plan.getStartLocation());
            }
        }


        private void updateCost(String editable) {
            int num = plans.indexOf(plan);
            if (plan.getCost() != null) {
                if (!plan.getCost().hasId()) {
                    plan.getCost().setId(UUID.randomUUID().toString());
                }
                if (editable.equals("")) {
                    plan.getCost().setPrice(0.0);
                } else
                    plan.getCost().setPrice(Double.parseDouble(editable));
            } else {
                Expense newCost = new Expense(ExpenseTypes.Activity, Double.parseDouble(editable));
                newCost.setId(UUID.randomUUID().toString());
                plan.setCost(newCost);
            }
            parentAdapter.editBudget(plan.getCost());
            if (num < plans.size() && num != -1) {
                parentAdapter.editPlan(plan);
            }
        }
    }

}
