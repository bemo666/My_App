package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.Expense;
import com.ikea.myapp.R;
import com.ikea.myapp.ViewModels.ExpenseTypes;

import java.util.Arrays;
import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private Context context;
    private ExpenseTypes[] expenses;

    public ExpensesAdapter(Context context) {
        this.context = context;
        expenses = ExpenseTypes.values();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_expense_type, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpenseTypes type = expenses[position];
        holder.img.setImageResource(type.getImage());
        holder.txt.setText(type.name());
    }

    @Override
    public int getItemCount() {
        return expenses.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView txt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            txt = itemView.findViewById(R.id.txt);
        }
    }
}
