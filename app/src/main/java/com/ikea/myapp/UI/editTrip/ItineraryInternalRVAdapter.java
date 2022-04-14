package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ikea.myapp.R;
import com.ikea.myapp.models.CustomCurrency;
import com.ikea.myapp.models.Expense;
import com.ikea.myapp.models.ExpenseTypes;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.models.PlanViewHolder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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

        TextInputEditText pickupAddress, pickupTime, pickupDate, dropoffAddress, dropoffTime, dropoffDate, carDetails, confiramtionNum, cost;
        TextInputLayout pickupAddressLayout, pickupTimeLayout, pickupDateLayout, dropoffAddressLayout, dropoffTimeLayout, dropoffDateLayout;
        Plan plan;
        Button save, delete;
        boolean deletePressed = false;
        CardView confirmDelete;

        RentalViewHolder(@NonNull View itemView) {
            super(itemView);

            pickupAddress = itemView.findViewById(R.id.rental_pick_up_address);
            pickupTime = itemView.findViewById(R.id.rental_pick_up_time);
            pickupDate = itemView.findViewById(R.id.rental_pick_up_date);
            dropoffAddress = itemView.findViewById(R.id.rental_drop_off_address);
            dropoffTime = itemView.findViewById(R.id.rental_drop_off_time);
            dropoffDate = itemView.findViewById(R.id.rental_drop_off_date);
            carDetails = itemView.findViewById(R.id.rental_car_details);
            confiramtionNum = itemView.findViewById(R.id.rental_confirmation_number);
            cost = itemView.findViewById(R.id.rental_cost);

            pickupAddressLayout = itemView.findViewById(R.id.rental_pick_up_address_layout);
            pickupTimeLayout = itemView.findViewById(R.id.rental_pick_up_time_layout);
            pickupDateLayout = itemView.findViewById(R.id.rental_pick_up_date_layout);
            dropoffAddressLayout = itemView.findViewById(R.id.rental_drop_off_address_layout);
            dropoffTimeLayout = itemView.findViewById(R.id.rental_drop_off_time_layout);
            dropoffDateLayout = itemView.findViewById(R.id.rental_drop_off_date_layout);

            save = itemView.findViewById(R.id.rental_save);
            delete = itemView.findViewById(R.id.rental_delete);
            confirmDelete = itemView.findViewById(R.id.rental_confirm_delete);


        }

        public void setDetails(Plan plan) {
            this.plan = plan;
            pickupAddress.setText(plan.getStartLocationAddress());
            dropoffAddress.setText(plan.getEndLocationAddress());
            carDetails.setText(plan.getNote());
            confiramtionNum.setText(plan.getConfirmationNumber());
            if (plan.getCost() != null)
                cost.setText(currency.getSymbol() + plan.getCost().getPrice());


            if (plan.getStartTime() != null) {
                Date start = new Date(Long.parseLong(plan.getStartTime()));
                Date end = new Date(Long.parseLong(plan.getStartTime()));

                DateFormat date = new SimpleDateFormat("dd-MM");
                DateFormat time = new SimpleDateFormat("hh:mm");

                pickupDate.setText(date.format(start));
                pickupTime.setText(time.format(start));

                dropoffDate.setText(date.format(end));
                dropoffTime.setText(time.format(end));
            }
            cost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        Log.d("tag", "click");
                        imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
                        if (!cost.getText().toString().isEmpty())
                            plan.setCost(new Expense(ExpenseTypes.CarRental, Double.parseDouble(cost.getText().toString())));
                        if (validate())
                            parentAdapter.editPlan(plan, plans.indexOf(plan));
                    }
                    return false;
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validate())
                        parentAdapter.editPlan(plan, plans.indexOf(plan));
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


        private boolean validate() {
            boolean check = true;

            if (TextUtils.isEmpty(plan.getStartLocationAddress())) {
                pickupAddressLayout.setError(fragment.getString(R.string.login_required_field));
                check = false;
            } else {
                pickupAddressLayout.setError(null);
                pickupAddressLayout.setErrorEnabled(false);
            }

            if (TextUtils.isEmpty(plan.getStartTime())) {
                pickupDateLayout.setError(fragment.getString(R.string.login_required_field));
                pickupTimeLayout.setError(fragment.getString(R.string.login_required_field));
                check = false;
            } else {
                pickupDateLayout.setError(null);
                pickupDateLayout.setErrorEnabled(false);
                pickupDateLayout.setError(null);
                pickupDateLayout.setErrorEnabled(false);
            }


            if (TextUtils.isEmpty(plan.getEndLocationAddress())) {
                dropoffAddressLayout.setError(fragment.getString(R.string.login_required_field));
                check = false;
            } else {
                dropoffAddressLayout.setError(null);
                dropoffAddressLayout.setErrorEnabled(false);
            }

            if (TextUtils.isEmpty(plan.getEndTime())) {
                dropoffDateLayout.setError(fragment.getString(R.string.login_required_field));
                dropoffTimeLayout.setError(fragment.getString(R.string.login_required_field));
                check = false;
            } else {
                dropoffDateLayout.setError(null);
                dropoffDateLayout.setErrorEnabled(false);
                dropoffTimeLayout.setError(null);
                dropoffTimeLayout.setErrorEnabled(false);
            }


            return check;


        }
    }

    class FlightViewHolder extends PlanViewHolder {

        FlightViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setDetails(Plan plan) {
        }

    }

    class HotelViewHolder extends PlanViewHolder {

        HotelViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setDetails(Plan plan) {
        }

    }

    class ActivityViewHolder extends PlanViewHolder {

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setDetails(Plan plan) {
        }

    }

}
