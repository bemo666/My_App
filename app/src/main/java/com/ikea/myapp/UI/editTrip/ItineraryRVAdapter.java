package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.util.Log;
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
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.R;

public class ItineraryRVAdapter extends RecyclerView.Adapter<ItineraryRVAdapter.SliderViewHolder> {

    private final Context context;
    private final ItineraryFragment fragment;
    private boolean hasItems = false;
    private MyTrip trip;
    private final InputMethodManager imm;


    public ItineraryRVAdapter(ItineraryFragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        holder.setDetails(trip.getPlanHeaders().get(position), position);
        holder.arrow.setOnClickListener(view -> expand(holder));
        holder.itemView.setOnClickListener(view -> expand(holder));
        holder.title.setOnFocusChangeListener((view, b) -> {
            if (b) {
                holder.titleLayout.setBoxBackgroundColorResource(R.color.lightGrey);
                expand(holder);
            } else {
                Log.d("tag", "focus off");
                holder.titleLayout.setBoxBackgroundColorResource(R.color.white);
                if(!holder.title.getText().toString().equals(holder.titleString)){
                    updateTitle(holder.title.getText().toString(), position);
                }
            }

        });
    }

    private void expand(SliderViewHolder holder) {
        if (!holder.expanded) {
            holder.arrow.setRotation(0);
            holder.hiddenLayout.setVisibility(View.VISIBLE);
        } else {
            holder.arrow.setRotation(-90);
            holder.hiddenLayout.setVisibility(View.GONE);
            imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
            if (holder.title.hasFocus()) {
                holder.title.clearFocus();
                holder.titleLayout.clearFocus();
            }
        }
        holder.expanded = !holder.expanded;
    }


    @Override
    public int getItemCount() {
        if (trip != null && trip.getPlanHeaders() != null)
            return trip.getPlanHeaders().size();
        else
            return 0;
    }


    class SliderViewHolder extends RecyclerView.ViewHolder {
        private boolean expanded = false;
        private String titleString;
        private final ImageView arrow;
        private final TextInputEditText title;
        private final TextInputLayout titleLayout;
        private final LinearLayout hiddenLayout;
        private final RecyclerView internalRV;
        private ItineraryInternalRVAdapter internalRVAdapter;
        private final Button addButton;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            arrow = itemView.findViewById(R.id.itinerary_selected_ic);
            title = itemView.findViewById(R.id.title_header);
            titleLayout = itemView.findViewById(R.id.layout_title_header);
            hiddenLayout = itemView.findViewById(R.id.itinerary_hidden_layout);
            internalRV = itemView.findViewById(R.id.itinerary_internal_recycler_view);
            addButton = itemView.findViewById(R.id.add_a_note_button);
        }

        void setDetails(PlanHeader header, int position) {
            arrow.setRotation(-90);
            titleString = header.getMyTitle();
            title.setText(header.getMyTitle());
            addButton.setOnClickListener(view -> {
                fragment.checkForAddHeader(header.getObjectType());
                notifyItemChanged(position);
            });
            internalRVAdapter = new ItineraryInternalRVAdapter(this, header.getObjectType());

            internalRVAdapter.setList(header.getObjects());
            internalRV.setAdapter(internalRVAdapter);
            internalRV.setNestedScrollingEnabled(false);
            internalRV.setLayoutManager(new LinearLayoutManager(context));
        }

        public void editObject(Object object, int adapterPosition) {
            trip.getPlanHeaders().get(getAdapterPosition()).editObject(object, adapterPosition);
            fragment.updateTrip(trip);
        }
    }

    public void setList(MyTrip trip) {
        this.trip = trip;
        hasItems = true;
        notifyDataSetChanged();

    }

    public void updateTitle(String title, int position){
        trip.getPlanHeaders().get(position).setMyTitle(title);
        fragment.updateTrip(trip);
    }

//    public void setList() {
//        List<PlanHeader> lst = new ArrayList<>();
//        lst.add(new PlanHeader());
//        lst.add(new PlanHeader());
//        lst.add(new PlanHeader());
//        lst.add(new PlanHeader());
//        this.planHeaders = lst;
//        hasItems = true;
//        notifyDataSetChanged();
//
//    }

    public boolean hasItems() {
        return hasItems;
    }

    public ItineraryFragment getFragment() {
        return fragment;
    }

}
