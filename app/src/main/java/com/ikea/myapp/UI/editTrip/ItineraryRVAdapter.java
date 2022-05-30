package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.ikea.myapp.R;
import com.ikea.myapp.models.Budget;
import com.ikea.myapp.models.Expense;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanType;

import java.util.ArrayList;
import java.util.List;

public class ItineraryRVAdapter extends RecyclerView.Adapter<ItineraryRVAdapter.HeaderViewHolder> {

    private final ItineraryFragment fragment;
    private MyTrip trip;
    private List<PlanHeader> headers;
    private final InputMethodManager imm;
    private ItineraryInternalRVAdapter[] adapters;


    public ItineraryRVAdapter(ItineraryFragment fragment) {
        this.fragment = fragment;
        headers = new ArrayList<>();
        this.imm = (InputMethodManager) fragment.requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_plan_header, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        holder.setDetails(position);
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public void setList(MyTrip trip) {
        this.trip = trip;
        headers = trip.sortList();
        adapters = new ItineraryInternalRVAdapter[headers.size()];
        notifyDataSetChanged();
    }

    public ItineraryFragment getFragment() {
        return fragment;
    }

    public void editBudget(Expense cost) {
        boolean found = false;
        if (trip.hasBudgetEntries()) {
            int i = 0;
            for (Expense e : trip.getBudget().getExpenses()) {
                if (e.hasId()) {
                    if (e.getId().equals(cost.getId())) {
                        found = true;
                        trip.getBudget().editExpense(cost, i);
                    }
                }
                i++;
            }
            if (!found) {
                trip.getBudget().addExpense(cost);
            }
        } else {
            Budget b = new Budget();
            b.addExpense(cost);
            trip.setBudget(b);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (ItineraryInternalRVAdapter a : adapters) {
            if (a != null)
                a.onResult(requestCode, resultCode, data);
        }
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private boolean expanded;
        private final ImageView arrow;
        private final MaterialTextView title;
        private final LinearLayout hiddenLayout;
        protected final RecyclerView internalRV;
        private final Button addButton;
        private final View itemView;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            arrow = itemView.findViewById(R.id.itinerary_selected_ic);
            title = itemView.findViewById(R.id.title_header);
            hiddenLayout = itemView.findViewById(R.id.itinerary_hidden_layout);
            internalRV = itemView.findViewById(R.id.itinerary_internal_recycler_view);
            addButton = itemView.findViewById(R.id.add_a_note_button);
        }

        void setDetails(int position) {
            PlanHeader header = headers.get(position);
            itemView.setOnClickListener(view -> expand());
            expanded = true;
            expand();

            title.setText(header.getType().getName());
            addButton.setText(header.getType().getNote());
            addButton.setOnClickListener(view -> {
                checkForAddHeader(header.getType());
                adapters[position].notifyItemInserted(adapters[position].getItemCount());
            });
            if (adapters[position] == null) {
                adapters[position] = new ItineraryInternalRVAdapter(ItineraryRVAdapter.this, header, fragment, trip.getCurrency());
            }
            internalRV.setAdapter(adapters[position]);
            internalRV.setNestedScrollingEnabled(false);
            internalRV.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        }

        void expand() {
            if (!expanded) {
                arrow.setRotation(0);
                hiddenLayout.setVisibility(View.VISIBLE);
            } else {
                arrow.setRotation(-90);
                hiddenLayout.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(fragment.requireView().getWindowToken(), 0);
            }
            expanded = !this.expanded;
        }

        void forceExpand() {
            arrow.setRotation(0);
            hiddenLayout.setVisibility(View.VISIBLE);
            expanded = true;
        }

        public RecyclerView getInternalRV() {
            return internalRV;
        }
    }

    public void checkForAddHeader(PlanType type) {
        PlanHeader header = null;
        Plan p = new Plan(type.getType());
        trip.addPlan(p);

        for (PlanHeader h : headers) {
            if (h.getType() == type) {
                header = h;
                header.addPlan(p);
                break;
            }
        }

        if (header == null) {
            header = new PlanHeader(type, new ArrayList<>());
            headers.add(header);
            header.addPlan(p);
            adapters = new ItineraryInternalRVAdapter[headers.size()];
            notifyItemInserted(headers.size() - 1);
        }

        if (adapters[headers.indexOf(header)] != null) {
            adapters[headers.indexOf(header)].notifyItemInserted(header.getPlans().size() - 1);
        }
        fragment.showNewCard();
        fragment.updateTrip(trip);


        fragment.expandHeader(headers.indexOf(header));

    }

    public void editPlan(Plan object) {
        int num = trip.getPlans().indexOf(object);
        if(num != -1) {
            trip.editPlan(object, num);
            fragment.updateTrip(trip);
        }
    }

    public void deletePlan(Plan plan) {
        if (plan.getCost() != null) {
            if (trip.hasBudgetEntries()) {
                for (Expense e : trip.getBudget().getExpenses()) {
                    if (e.hasId()) {
                        if (e.getId().equals(plan.getCost().getId())) {
                            trip.getBudget().deleteExpense(e);
                            break;
                        }
                    }
                }
            }
        }
        trip.deletePlan(plan);
        int index = -1;
        for (PlanHeader h : headers)
            if (h.getType().getType() == plan.getObjectType()) {
                index = headers.indexOf(h);
                break;
            }
        headers.get(index).getPlans().remove(plan);
        if (headers.get(index).getPlans().size() == 0) {
            notifyItemRemoved(index);
            headers.remove(index);
        }
        fragment.updateTrip(trip);
    }

    public void expandGroup(Plan plan){
        new Handler().postDelayed(() -> {
            for(PlanHeader h : headers){
                if(h.getType().getType() == plan.getObjectType()){
                    fragment.expandViewHolder(headers.indexOf(h));
                    break;
                }
            }
        }, 10);

    }


}
