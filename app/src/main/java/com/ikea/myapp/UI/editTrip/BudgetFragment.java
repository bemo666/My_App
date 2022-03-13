package com.ikea.myapp.UI.editTrip;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ikea.myapp.Budget;
import com.ikea.myapp.Expense;
import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;
import com.ikea.myapp.TripList;
import com.ikea.myapp.ViewModels.ExpenseTypes;

public class BudgetFragment extends Fragment implements View.OnClickListener {
    private MyTrip trip;
    private TextView currentTotal, budget, expenseTypeSelector, cancelButton, cancelButton2, saveButton, saveButton2;
    private ImageView expenseTypeIcon;
    private LinearLayout setBudgetLinearLayout, addExpenseLinearLayout, setBudgetCostLayout, expenseDescriptionLayout;
    private RelativeLayout expenseCostLayout;
    private LinearProgressIndicator progressIndicator;
    private EditTripViewModel viewModel;
    private FloatingActionButton addExpenseButton;
    private BottomSheetDialog addExpenseSheet, setBudgetSheet, typesSheet;
    private RecyclerView expensessRV, rv;
    private ExpensesAdapter expensesAdapter;
    private ExpenseTypes type;
    private EditText budgetET, amount, description;
    private ExpenseTypesAdapter expenseTypesAdapter;

    public BudgetFragment(MyTrip trip) {
        this.trip = trip;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        setBudgetSheet = new BottomSheetDialog(requireContext());
        setBudgetSheet.setContentView(R.layout.dialog_set_budget);
        setBudgetSheet.setCancelable(true);

        addExpenseSheet = new BottomSheetDialog(requireContext());
        addExpenseSheet.setContentView(R.layout.dialog_add_expense);
        addExpenseSheet.setCancelable(true);

        typesSheet = new BottomSheetDialog(requireContext());
        typesSheet.setContentView(R.layout.layout_expense_types_dialog);

        currentTotal =              view.findViewById(R.id.budget_current_total);
        budget =                    view.findViewById(R.id.budget_total);
        progressIndicator =         view.findViewById(R.id.budget_progressbar);
        addExpenseButton =          view.findViewById(R.id.add_expense_button);
        expensessRV =               view.findViewById(R.id.budget_expenses_rv);
        viewModel =                 ViewModelProviders.of(requireActivity()).get(EditTripViewModel.class);
        cancelButton2 =             setBudgetSheet.findViewById(R.id.set_budget_cancel_button);
        saveButton2 =               setBudgetSheet.findViewById(R.id.set_budget_save_button);
        budgetET =                  setBudgetSheet.findViewById(R.id.set_budget_edit_text);
        setBudgetLinearLayout =     setBudgetSheet.findViewById(R.id.set_budget_layout);
        setBudgetCostLayout =       setBudgetSheet.findViewById(R.id.set_budget_cost_layout);
        expenseCostLayout =         addExpenseSheet.findViewById(R.id.expense_cost_layout);
        expenseDescriptionLayout =  addExpenseSheet.findViewById(R.id.expense_description_layout);
        expenseTypeIcon =           addExpenseSheet.findViewById(R.id.expense_type_icon);
        expenseTypeSelector =       addExpenseSheet.findViewById(R.id.expense_type_selector);
        cancelButton =              addExpenseSheet.findViewById(R.id.expense_cancel);
        saveButton =                addExpenseSheet.findViewById(R.id.expense_save);
        amount =                    addExpenseSheet.findViewById(R.id.expense_cost_edit_text);
        description =               addExpenseSheet.findViewById(R.id.expense_description_edit_text);
        addExpenseLinearLayout =    addExpenseSheet.findViewById(R.id.add_expense_layout);
        rv =                        typesSheet.findViewById(R.id.recycler_view);
        expenseTypesAdapter =       new ExpenseTypesAdapter(this);
        viewModel.getTrips().observe(getViewLifecycleOwner(), tripList -> {
            trip = tripList.getTripWithId(trip.getId());
            setData();
        });
        setData();
        expenseSheet();
        budgetSheet();
        populateRV();
        return view;
    }

    private void populateRV() {
        expensesAdapter = new ExpensesAdapter(getContext(), trip.getBudget().getExpenses());
        expensessRV.setAdapter(expensesAdapter);
        expensessRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        expensessRV.setNestedScrollingEnabled(false);
    }

    private void budgetSheet() {
        setBudgetLinearLayout.setOnClickListener(view -> {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
        setBudgetCostLayout.setOnClickListener(view -> budgetET.requestFocus());
        budget.setOnClickListener(view1 -> setBudgetSheet.show());
        cancelButton2.setOnClickListener(view -> setBudgetSheet.dismiss());
        saveButton2.setOnClickListener(view -> {
            if (!budgetET.getText().toString().isEmpty()) {
                double num = Double.parseDouble(budgetET.getText().toString());
                if(num>0) {
                    if (trip.getBudget() == null) {
                        Budget budget1 = new Budget(Double.parseDouble(budgetET.getText().toString()));
                        trip.setBudget(budget1);
                    } else
                        trip.getBudget().setBudget(Double.parseDouble(budgetET.getText().toString()));
                    viewModel.updateTrip(trip);
                    setData();
                    setBudgetSheet.dismiss();
                } else {
                    Toast.makeText(getContext(), getString(R.string.budget_value_negative), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.budget_add_budget_text), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void expenseSheet() {
        RelativeLayout selectItemTypeLayout = addExpenseSheet.findViewById(R.id.expense_type_layout);
        addExpenseLinearLayout.setOnClickListener(view -> {
            view.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
        expenseCostLayout.setOnClickListener(view -> amount.requestFocus());
        expenseDescriptionLayout.setOnClickListener(view -> description.requestFocus());
        addExpenseButton.setOnClickListener(view1 -> addExpenseSheet.show());
        cancelButton.setOnClickListener(view -> addExpenseSheet.dismiss());
        saveButton.setOnClickListener(view -> {
            if (!amount.getText().toString().isEmpty() && type != null) {
                Expense expense = new Expense(description.getText().toString(), type, Double.parseDouble(amount.getText().toString()));

                if (trip.getBudget() == null) {
                    Budget budget1 = new Budget();
                    budget1.addExpense(expense);
                    trip.setBudget(budget1);
                } else
                    trip.getBudget().addExpense(expense);

                viewModel.updateTrip(trip);
                expensesAdapter.setExpenses(trip.getBudget().getExpenses());
                setData();
                amount.setText(null);
                description.setText(null);
                addExpenseSheet.dismiss();

                expenseTypeSelector.setText("Select Item Type");
                expenseTypeIcon.setImageResource(R.drawable.ic_receipt);
                type = null;

            } else
                Toast.makeText(getContext(), getString(R.string.budget_enter_expenses_details), Toast.LENGTH_SHORT).show();
        });

        rv.setAdapter(expenseTypesAdapter);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        selectItemTypeLayout.setOnClickListener(v -> typesSheet.show());

    }

    public void selectExpenseType(ExpenseTypes type){
        expenseTypeSelector.setText(type.name());
        expenseTypeIcon.setImageResource(type.getImage());
        this.type = type;
        typesSheet.dismiss();
    }

    private void setData() {

        String finalBudget = "Budget: $";
        if (trip.getBudget() == null || trip.getBudget().getBudget() == null)
            finalBudget += "0.00";
        else
            finalBudget += trip.getBudget().getBudget();

        budget.setText(finalBudget);
        String finalCurrent = "$";
        if (trip.getBudget() == null || trip.getBudget().getBudget() == null)
            finalCurrent += "0.00";
        else
            finalCurrent += trip.getBudget().getCurrentTally();
        currentTotal.setText(finalCurrent);
        if(trip.getBudget() != null){
            if(trip.getBudget().getBudget() != null && trip.getBudget().getBudget() != 0){
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setMax(100);
                int progress = (int)((trip.getBudget().getCurrentTally()/trip.getBudget().getBudget())*100);
                Log.d("tag", "progress: "+ progress);
                if(progress > 100) { progress = 100; }
                progressIndicator.setProgress(progress);

            } else {
                progressIndicator.setVisibility(View.GONE);
            }
        }

    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.budget_total) {

        }
    }
}