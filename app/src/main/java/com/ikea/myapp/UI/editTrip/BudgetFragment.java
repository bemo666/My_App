package com.ikea.myapp.UI.editTrip;

import androidx.lifecycle.ViewModel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.ViewModels.Currency;

public class BudgetFragment extends Fragment {
    private MyTrip trip;
    private TextView currentTotal, budget;
    private LinearProgressIndicator progressIndicator;
    private ViewModel viewModel;
    private FloatingActionButton addExpenseButton;
    private BottomSheetDialog dialog;

    public BudgetFragment(MyTrip trip) {
        this.trip = trip;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        currentTotal = view.findViewById(R.id.budget_current_total);
        budget = view.findViewById(R.id.budget_total);
        progressIndicator = view.findViewById(R.id.budget_progressbar);
        addExpenseButton = view.findViewById(R.id.add_expense_button);
        viewModel = ViewModelProviders.of(this).get(EditTripViewModel.class);

        String finalBudget = "Budget: $";
        if (trip.getBudget() == null)
            finalBudget += "0.00";
        else
            finalBudget += trip.getBudget().getBudget();

        budget.setText(finalBudget);
        String finalCurrent = "$";
        if (trip.getBudget() == null)
            finalCurrent += "0.00";
        else
            finalCurrent += trip.getBudget().getBudget();
        currentTotal.setText(finalCurrent);

        progressIndicator.setProgress(40, true);

        dialog = new BottomSheetDialog(requireContext());
        dialog.setContentView(R.layout.dialog_budget);
        dialog.setCancelable(true);
        addExpenseButton.setOnClickListener(view1 -> dialog.show());
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}