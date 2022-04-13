package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikea.myapp.R;
import com.ikea.myapp.models.Plan;
import com.ikea.myapp.models.PlanActivity;
import com.ikea.myapp.models.PlanFlight;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanHotel;
import com.ikea.myapp.models.PlanNote;
import com.ikea.myapp.models.PlanRental;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.models.PlanViewHolder;

import java.util.List;

public class ItineraryInternalRVAdapter extends RecyclerView.Adapter<PlanViewHolder> {

    private List<Plan> plans;
    private PlanType type;
    private final ItineraryRVAdapter parentAdapter;
    final static ObjectMapper mapper = new ObjectMapper();
    private final InputMethodManager imm;
    private ItineraryFragment fragment;

    public ItineraryInternalRVAdapter(ItineraryRVAdapter parentAdapter, PlanHeader header, ItineraryFragment fragment) {
        this.plans = header.getPlans();
        this.type = header.getType();
        this.parentAdapter = parentAdapter;
        this.fragment = fragment;
        this.imm = (InputMethodManager) fragment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (type) {
            case Note:
                return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(type.getLayout(), parent, false));
            case Hotel:
                break;
            case Flight:
                break;
            case Rental:
                break;
            case Activity:
                break;
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

        private EditText text;
        private ImageView delete, confirmDelete;
        private boolean deletePressed = false;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.note_text_edit_text);
            delete = itemView.findViewById(R.id.itinerary_delete_ic);
            confirmDelete = itemView.findViewById(R.id.itinerary_confirm_delete);

        }

        public void setDetails(Plan plan) {
            text.setText(plan.getNote());
            text.setOnKeyListener((view, i, keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_ENTER) {
                        imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
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

}
