package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.ikea.myapp.PlanHeader;
import com.ikea.myapp.PlanNote;
import com.ikea.myapp.R;

import java.util.ArrayList;
import java.util.List;

public class ItineraryInternalRVAdapter extends RecyclerView.Adapter<ItineraryInternalRVAdapter.SliderViewHolder> {

    private List<PlanNote> notes;
    private Context context;
    private final ItineraryFragment fragment;
    private boolean hasItems = false;
    private int type;

    public ItineraryInternalRVAdapter(ItineraryFragment fragment, int type) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.type = type;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        switch (type) {
            case PlanHeader.NOTE:
                layout = R.layout.layout_plan_note;
                break;
            default:
                layout = R.layout.layout_plan_note;
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
                holder.setDetails(notes.get(position));
//                holder.itemView.setOnClickListener(view -> expand(holder));
                break;
        }
    }


    @Override
    public int getItemCount() {
        switch (type) {
            case PlanHeader.NOTE:
                if (notes != null)
                    return notes.size();
                else
                    return 0;
            default:
                return 0;
        }
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {
        private EditText text;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            switch (type) {
                case PlanHeader.NOTE:
                    text = itemView.findViewById(R.id.note_text_edit_text);
                    break;
            }
        }

        void setDetails(PlanNote note) {
            text.setText(note.getNote());
        }
    }

    public void setList(List<PlanNote> notes) {
        this.notes = notes;
        hasItems = true;
        notifyDataSetChanged();

    }

    public void setList() {
        List<PlanNote> lst = new ArrayList<>();
        lst.add(new PlanNote("yass"));
        lst.add(new PlanNote("yassss"));
        lst.add(new PlanNote("yasssssss"));
        lst.add(new PlanNote("yasssssssss"));
        this.notes = lst;
        hasItems = true;
        notifyDataSetChanged();

    }

    public boolean hasItems() {
        return hasItems;
    }

}
