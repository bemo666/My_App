package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    final static ObjectMapper mapper = new ObjectMapper();
    private final InputMethodManager imm;
    private ItineraryFragment fragment;

    public ItineraryInternalRVAdapter(ItineraryRVAdapter.SliderViewHolder parentAdapter, int type, ItineraryFragment fragment) {
        this.type = type;
        this.parentAdapter = parentAdapter;
        this.fragment = fragment;
        this.imm = (InputMethodManager) fragment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        switch (type) {
            case PlanHeader.NOTE:
                holder.setDetails(mapper.convertValue(objects.get(position), PlanNote.class));
                break;
            case PlanHeader.HOTEL:
                break;
            case PlanHeader.RENTAL:
                break;
            case PlanHeader.FLIGHT:
                break;
            case PlanHeader.ACTIVITY:
                break;
        }
    }


    @Override
    public int getItemCount() {
        if(objects != null)
            return objects.size();
        return 0;
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
                                imm.hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
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
            if (object instanceof PlanNote) {
                this.myNote = (PlanNote) object;
                text.setText(myNote.getNote());
            } else if (object instanceof PlanHotel) {

            }
        }
    }

    public void setList(List<Object> objects) {
        this.objects = objects;
        notifyDataSetChanged();
    }


}
