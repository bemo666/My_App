package com.ikea.myapp.UI.editTrip;

import android.content.Context;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ikea.myapp.PlanHeader;
import com.ikea.myapp.PlanNote;
import com.ikea.myapp.R;

import java.util.ArrayList;
import java.util.List;

public class ItineraryRVAdapter extends RecyclerView.Adapter<ItineraryRVAdapter.SliderViewHolder> {

    private List<PlanHeader> planHeaders;
    private Context context;
    private final ItineraryFragment fragment;
    private boolean hasItems = false;


    public ItineraryRVAdapter(ItineraryFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_itinerary_recyclerview_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setDetails(planHeaders.get(position), position);
        holder.arrow.setOnClickListener(view -> expand(holder));
        holder.itemView.setOnClickListener(view -> expand(holder));
        holder.title.setOnFocusChangeListener((view, b) -> {
            if(b){
                holder.titleLayout.setBoxBackgroundColorResource(R.color.lightGrey);
                expand(holder);
            } else {
                holder.titleLayout.setBoxBackgroundColorResource(R.color.white);
            }

        });
    }

    private void expand(SliderViewHolder holder) {
        if (!holder.expanded){
            holder.arrow.setRotation(0);
            holder.hiddenLayout.setVisibility(View.VISIBLE);
        } else {
            holder.arrow.setRotation(-90);
            holder.hiddenLayout.setVisibility(View.GONE);
            if (holder.title.hasFocus()){
                holder.title.clearFocus();
                holder.titleLayout.clearFocus();
                InputMethodManager imm =  (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
            }
        }
        holder.expanded = !holder.expanded;
    }


    @Override
    public int getItemCount() {
        if (planHeaders != null)
            return planHeaders.size();
        else
            return 0;
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private boolean expanded = false;
        private ImageView arrow;
        private TextInputEditText title;
        private TextInputLayout titleLayout;
        private LinearLayout hiddenLayout;
        private RecyclerView internalRV;
        private ItineraryInternalRVAdapter internalRVAdapter;
        private Button addButton;
        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            arrow = itemView.findViewById(R.id.itinerary_selected_ic);
            title = itemView.findViewById(R.id.title_header);
            titleLayout = itemView.findViewById(R.id.layout_title_header);
            hiddenLayout = itemView.findViewById(R.id.itinerary_hidden_layout);
            internalRV = itemView.findViewById(R.id.itinerary_internal_recycler_view);
            addButton = itemView.findViewById(R.id.add_a_note_button);
        }

        void setDetails(PlanHeader item, int position) {
            arrow.setRotation(-90);
            title.setText(item.getTitle());
            addButton.setOnClickListener(view -> {
                planHeaders.get(position).addNotes(new PlanNote(5));
                notifyItemChanged(position);
            });
            internalRVAdapter = new ItineraryInternalRVAdapter(fragment, item.getPlanType());
            internalRVAdapter.setList();
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            internalRV.setAdapter(internalRVAdapter);
            internalRV.setLayoutManager(layoutManager2);
        }
    }

    public void setList(List<PlanHeader> items) {
        this.planHeaders = items;
        hasItems = true;
        notifyDataSetChanged();

    }

    public void setList() {
        List<PlanHeader> lst = new ArrayList<>();
        lst.add(new PlanHeader());
        lst.add(new PlanHeader());
        lst.add(new PlanHeader());
        lst.add(new PlanHeader());
        this.planHeaders = lst;
        hasItems = true;
        notifyDataSetChanged();

    }

    public boolean hasItems() {
        return hasItems;
    }

}
