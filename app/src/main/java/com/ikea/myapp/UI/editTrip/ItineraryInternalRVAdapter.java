package com.ikea.myapp.UI.editTrip;

import static android.app.Activity.RESULT_OK;
import static com.ikea.myapp.utils.Utils.prettyPrint;

import android.content.Context;
import android.content.Intent;
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
        if(result.getOpeningHours() != null) {
            List<String> list = new ArrayList<>(result.getOpeningHours().getWeekdayText());
            plan.setStartLocationTimes(list);
        }
        return plan;
    }

    private Plan putResultsInEnd(Place result, Plan plan) {
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
        if(result.getOpeningHours() != null) {
            List<String> list = new ArrayList<>(result.getOpeningHours().getWeekdayText());
            plan.setEndLocationTimes(list);
        }
        return plan;
    }

    class NoteViewHolder extends PlanViewHolder {

        private final EditText text;
        private final ImageView delete;
        private final ImageView confirmDelete;
        private final ImageView save2;
        private boolean deletePressed, textChanged, saved;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.note_text_edit_text);
            delete = itemView.findViewById(R.id.note_delete_ic);
            confirmDelete = itemView.findViewById(R.id.note_confirm_delete);
            save2 = itemView.findViewById(R.id.note_save_ic);

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
                    textChanged = true;
                    changeButtons();
                }
            });
            save2.setOnClickListener(view -> {
                imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
                plan.setNote(text.getText().toString());
                parentAdapter.editPlan(plan, plans.indexOf(plan));
                saved = true;
                changeButtons();
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

        private void changeButtons() {
            if (textChanged) {
                save2.setVisibility(View.VISIBLE);
                delete.setVisibility(View.GONE);
                confirmDelete.setVisibility(View.GONE);
            } else {
                save2.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
            }
            if (saved) {
                saved = false;
                save2.setVisibility(View.GONE);
                delete.setVisibility(View.VISIBLE);
            }
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
        final int PICKUP_ADDRESS_REQUEST = 100;
        final int DROPOFF_ADDRESS_REQUEST = 101;


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
            dropoffAddressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            pickupAddressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            pickupAddress.setOnClickListener(view -> fragment.startActivityForResult(intent, PICKUP_ADDRESS_REQUEST));
            dropoffAddress.setOnClickListener(view -> fragment.startActivityForResult(intent, DROPOFF_ADDRESS_REQUEST));
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
            setStartAddress();
            setEndAddress();

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

        @Override
        public void onResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == PICKUP_ADDRESS_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInStart(result, plan);
                    setStartAddress();
//                    Log.d("tag", "addresscomponents: " + result.getAddressComponents());
//                    Log.d("tag", "attributions: " + result.getAttributions());
//                    Log.d("tag", "pricelevel: " + result.getPriceLevel());
                } else if (requestCode == DROPOFF_ADDRESS_REQUEST) {
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
            if (!editable.equals("")) {
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
        final int DEPARTURE_AIRPORT_REQUEST = 102;
        final int ARRIVAL_AIRPORT_REQUEST = 103;

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
            departureAirport.setOnClickListener(view -> fragment.startActivityForResult(intent, DEPARTURE_AIRPORT_REQUEST));
            arrivalAirport.setOnClickListener(view -> fragment.startActivityForResult(intent, ARRIVAL_AIRPORT_REQUEST));
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
            setStartAddress();
            setEndAddress();

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
            if (!editable.equals("")) {
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

        TextInputEditText address, checkInTime, checkInDate, checkOutTime, checkOutDate, cost, confirmationNumber, notes;
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
        final int HOTEL_ADDRESS_REQUEST = 104;


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
            address.setOnClickListener(view -> fragment.startActivityForResult(intent, HOTEL_ADDRESS_REQUEST));

            confirmationNumber.setText(plan.getConfirmationNumber());
            notes.setText(plan.getNote());
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
            setAddress();


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

        @Override
        public void onResult(int requestCode, int resultCode, Intent data) {
            if (resultCode == RESULT_OK) {
                if (requestCode == HOTEL_ADDRESS_REQUEST) {
                    Place result = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                    plan = putResultsInStart(result, plan);
                    setAddress();
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

        private void savePlan() {
            plan.setNote(Objects.requireNonNull(notes.getText()).toString());
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
            if (!editable.equals("")) {
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
        final int ACTIVITY_ADDRESS_REQUEST = 105;

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
                fragment.requireActivity().startService(new Intent(fragment.requireActivity(), VibrationService.class));
                plan.setHasEnd(b);
                setSwitch();
            });
            setSwitch();
            endSwitch.setChecked(plan.isHasEnd());
            addressLayout.setEndIconOnClickListener(view -> fragment.openMap());
            address.setOnClickListener(view -> fragment.startActivityForResult(intent, ACTIVITY_ADDRESS_REQUEST));
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
            setAddress();
            setName();

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
            if (!editable.equals("")) {
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
