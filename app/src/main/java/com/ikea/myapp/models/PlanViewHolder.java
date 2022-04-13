package com.ikea.myapp.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PlanViewHolder extends RecyclerView.ViewHolder {

    public PlanViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void setDetails(Plan plan);
}
