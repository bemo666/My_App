package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.CustomCurrency;
import com.ikea.myapp.models.Expense;
import com.ikea.myapp.R;

import java.util.List;

public class ExpensesRVAdapter extends RecyclerView.Adapter<ExpensesRVAdapter.ViewHolder> {

    private Context context;
    private List<Expense> expenses;
    private BudgetFragment fragment;
    private CustomCurrency currency;

    public ExpensesRVAdapter(BudgetFragment fragment, List<Expense> expenses, CustomCurrency currency) {
        this.context = fragment.requireContext();
        this.fragment = fragment;
        this.expenses = expenses;
        this.currency = currency;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_expense, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.icon.setImageResource(expense.getType().getImage());
        holder.title.setText(expense.getType().name());
        holder.description.setText(expense.getDescription());
        holder.cost.setText(currency.getSymbol() + fragment.prettyPrint(expense.getPrice()));
        holder.itemView.setOnClickListener(view -> fragment.editExpense(position));
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView description, title, cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.expense_icon);
            title = itemView.findViewById(R.id.expense_type_text);
            description = itemView.findViewById(R.id.expense_description_text);
            cost = itemView.findViewById(R.id.expense_cost);
        }
    }

    public void setExpenses(List<Expense> expenses){
        this.expenses = expenses;
        notifyDataSetChanged();
    }
}
