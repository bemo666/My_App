package com.ikea.myapp.UI.profile;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ikea.myapp.R;
import com.ikea.myapp.UI.editTrip.BudgetFragment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrenciesRVAdapter extends RecyclerView.Adapter<CurrenciesRVAdapter.ViewHolder> {

    private final BudgetFragment fragment;
    private List<Currency> currencies, sortedList;

    public CurrenciesRVAdapter(BudgetFragment fragment) {
        this.fragment = fragment;
        this.currencies = new ArrayList<>(Currency.getAvailableCurrencies());
        for (int i = this.currencies.size()-1; i>0;i--) {
            if (this.currencies.get(i).getDisplayName().contains("(")) {
                this.currencies.remove(i);
            }
        }
        this.currencies = this.currencies.stream().sorted(Comparator.comparing(Currency::getDisplayName)).collect(Collectors.toList());
        sortedList = this.currencies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(fragment.getContext()).inflate(R.layout.layout_currency, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Currency currency = sortedList.get(position);
        holder.symbol.setText(currency.getSymbol());
        holder.fullName.setText(currency.getDisplayName());
        holder.code.setText(currency.getCurrencyCode());
        holder.itemView.setOnClickListener(view -> fragment.setCurrency(currency));

    }

    @Override
    public int getItemCount() {
        return sortedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView code, fullName, symbol;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            symbol = itemView.findViewById(R.id.currency_symbol);
            fullName = itemView.findViewById(R.id.currency_full_name);
            code = itemView.findViewById(R.id.currency_short_name);
        }
    }

    public void searchUpdate(String str) {
        if (str.isEmpty()) {
            sortedList = currencies;
        } else {
            sortedList = new ArrayList<>();
            for (Currency c : currencies) {
                if (c.getDisplayName().toLowerCase().contains(str.toLowerCase()) ||
                c.getCurrencyCode().toLowerCase().contains(str.toLowerCase())) {
                    sortedList.add(c);
                }
            }
        }
        notifyDataSetChanged();
    }
}

