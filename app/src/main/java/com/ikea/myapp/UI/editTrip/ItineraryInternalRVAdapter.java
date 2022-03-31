package com.ikea.myapp.UI.editTrip;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.models.PlanActivity;
import com.ikea.myapp.models.PlanFlight;
import com.ikea.myapp.models.PlanHeader;
import com.ikea.myapp.models.PlanHotel;
import com.ikea.myapp.models.PlanNote;
import com.ikea.myapp.models.PlanRental;
import com.ikea.myapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItineraryInternalRVAdapter extends RecyclerView.Adapter<ItineraryInternalRVAdapter.SliderViewHolder> {

    private List<Object> objects;
    private final ItineraryRVAdapter.SliderViewHolder parentAdapter;
    private final int type;

    public ItineraryInternalRVAdapter(ItineraryRVAdapter.SliderViewHolder parentAdapter, int type) {
        this.type = type;
        this.parentAdapter = parentAdapter;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.layout_plan_note;
        switch (type) {
            case PlanHeader.NOTE:
                layout = R.layout.layout_plan_note;
                break;
            case PlanHeader.HOTEL:
                layout = R.layout.layout_plan_hotel;
                break;
            case PlanHeader.RENTAL:
                layout = R.layout.layout_plan_rental;
                break;
            case PlanHeader.FLIGHT:
                layout = R.layout.layout_plan_flight;
                break;
            case PlanHeader.ACTIVITY:
                layout = R.layout.layout_plan_activity;
                break;

        }
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
//        switch (type) {
//            case PlanHeader.NOTE:
//                holder.setDetails(new ArrayList<>(((HashMap) notes.get(position)).values()).get(0));
//                break;
//            case PlanHeader.HOTEL:
//                holder.setDetails(new ArrayList<>(((HashMap) hotels.get(position)).values()).get(0));
//                break;
//            case PlanHeader.RENTAL:
//                holder.setDetails(new ArrayList<>(((HashMap) rentals.get(position)).values()).get(0));
//                break;
//            case PlanHeader.FLIGHT:
//                holder.setDetails(new ArrayList<>(((HashMap) flights.get(position)).values()).get(0));
//                break;
//            case PlanHeader.ACTIVITY:
//                holder.setDetails(new ArrayList<>(((HashMap) activities.get(position)).values()).get(0));
//                break;
//        }
    }


    @Override
    public int getItemCount() {
        return objects.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private EditText text;
        private PlanNote myNote;
        private PlanHotel myHotel;
        private PlanFlight myFlight;
        private PlanRental myRental;
        private PlanActivity myActivity;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);

            switch (type) {
                case PlanHeader.NOTE:
                    text = itemView.findViewById(R.id.note_text_edit_text);
                    text.setOnKeyListener((view, i, keyEvent) -> {
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            if (i == KeyEvent.KEYCODE_ENTER) {
                                updateNote();
                            }
                        }
                        return false;
                    });
                    break;
            }
        }

        private void updateNote() {
            if (myNote != null) {
                myNote.setNote(text.getText().toString());
                parentAdapter.editObject((Object) myNote, getAdapterPosition());
            }
        }

        void setDetails(Object object) {
            Class<?> aClass = object.getClass();
            if (PlanNote.class.equals(aClass)) {
                this.myNote = (PlanNote) object;
                text.setText(myNote.getNote());
            } else if (PlanHotel.class.equals(aClass)) {

            }
        }
    }

    public void setList(List<Object> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }


}
