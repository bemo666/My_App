package com.ikea.myapp.UI.editTrip;

import static com.ikea.myapp.utils.Utils.prettyPrint;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ikea.myapp.R;
import com.ikea.myapp.UI.profile.CurrenciesRVAdapter;
import com.ikea.myapp.models.Budget;
import com.ikea.myapp.models.CustomCurrency;
import com.ikea.myapp.models.Expense;
import com.ikea.myapp.models.ExpenseTypes;
import com.ikea.myapp.models.MyTrip;
import com.ikea.myapp.models.PlanType;
import com.ikea.myapp.utils.MyViewModelFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

public class BudgetFragment extends Fragment {

    private MyTrip trip;
    private String id;
    private TextView currentTotal, budget, expenseTypeSelector, cancelButton, cancelButton2, saveButton, saveButton2, expenseCurrencySymbol, budgetCurrencySymbol, addExpenseTitle, welcomeText;
    private ImageView expenseTypeIcon, budgetCurrencySelector, expenseCurrencySelector;
    private LinearLayout budgetFramentLayout, setBudgetLinearLayout, addExpenseLinearLayout, setBudgetCostLayout, expenseDescriptionLayout, budgetLinearLayout;
    private RelativeLayout expenseCostLayout;
    private LinearProgressIndicator progressIndicator;
    private EditTripViewModel viewModel;
    private MaterialButton addExpenseButton;
    private BottomSheetDialog addExpenseSheet, setBudgetSheet, typesSheet;
    private RecyclerView expensessRV, rv;
    private ExpensesRVAdapter expensesRVAdapter;
    private ExpenseTypes type;
    private EditText budgetET, amount, description;
    private ExpenseTypesAdapter expenseTypesAdapter;
    private int currentPos = -1;
    private RecyclerView currencyRV;
    private CurrenciesRVAdapter currenciesRVAdapter;
    private BottomSheetDialog currencySheet;
    private EditText currencySearchBar;
    private InputMethodManager imm;
    private CardView noExpensesCard;


    public BudgetFragment(String id) {
        this.id = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        setBudgetSheet = new BottomSheetDialog(requireContext());
        setBudgetSheet.setContentView(R.layout.dialog_set_budget);

        addExpenseSheet = new BottomSheetDialog(requireContext());
        addExpenseSheet.setContentView(R.layout.dialog_add_expense);

        typesSheet = new BottomSheetDialog(requireContext());
        typesSheet.setContentView(R.layout.dialog_layout_expense_types);

        currencySheet = new BottomSheetDialog(requireContext());
        currencySheet.setContentView(R.layout.dialog_currency_list);

        budgetFramentLayout = view.findViewById(R.id.budgetFragmentLayout);
        currentTotal = view.findViewById(R.id.budget_current_total);
        budget = view.findViewById(R.id.budget_total);
        progressIndicator = view.findViewById(R.id.budget_progressbar);
        addExpenseButton = view.findViewById(R.id.add_expense_button);
        expensessRV = view.findViewById(R.id.budget_expenses_rv);
        budgetLinearLayout = view.findViewById(R.id.budget_linear_layout);
        noExpensesCard = view.findViewById(R.id.budget_expenses_card);
        welcomeText = view.findViewById(R.id.welcomeText2);
        cancelButton2 = setBudgetSheet.findViewById(R.id.set_budget_cancel_button);
        saveButton2 = setBudgetSheet.findViewById(R.id.set_budget_save_button);
        budgetET = setBudgetSheet.findViewById(R.id.set_budget_edit_text);
        setBudgetLinearLayout = setBudgetSheet.findViewById(R.id.set_budget_layout);
        setBudgetCostLayout = setBudgetSheet.findViewById(R.id.set_budget_cost_layout);
        budgetCurrencySymbol = setBudgetSheet.findViewById(R.id.budget_money_symbol);
        budgetCurrencySelector = setBudgetSheet.findViewById(R.id.budget_money_symbol_arrow);
        expenseCostLayout = addExpenseSheet.findViewById(R.id.expense_cost_layout);
        expenseDescriptionLayout = addExpenseSheet.findViewById(R.id.expense_description_layout);
        expenseTypeIcon = addExpenseSheet.findViewById(R.id.expense_type_icon);
        expenseTypeSelector = addExpenseSheet.findViewById(R.id.expense_type_selector);
        cancelButton = addExpenseSheet.findViewById(R.id.expense_cancel);
        saveButton = addExpenseSheet.findViewById(R.id.expense_save);
        amount = addExpenseSheet.findViewById(R.id.expense_cost_edit_text);
        description = addExpenseSheet.findViewById(R.id.expense_description_edit_text);
        addExpenseLinearLayout = addExpenseSheet.findViewById(R.id.add_expense_layout);
        addExpenseTitle = addExpenseSheet.findViewById(R.id.expense_title);
        expenseCurrencySymbol = addExpenseSheet.findViewById(R.id.expense_money_symbol);
        expenseCurrencySelector = addExpenseSheet.findViewById(R.id.expense_money_symbol_arrow);
        rv = typesSheet.findViewById(R.id.recycler_view);
        currencyRV = currencySheet.findViewById(R.id.currency_recycler_view);
        currencySearchBar = currencySheet.findViewById(R.id.search_edit_text);
        expenseTypesAdapter = new ExpenseTypesAdapter(this);

        viewModel = ViewModelProviders.of(requireActivity(), new MyViewModelFactory(requireActivity().getApplication(), id)).get(EditTripViewModel.class);
        viewModel.getTrip().observe(getViewLifecycleOwner(), myTrip -> {
            if (myTrip != null) {
                trip = myTrip;
                updateData();
                expenseSheet();
                budgetSheet();
                setCurrencySheet();
            }
        });
        viewModel.getName().observe(getViewLifecycleOwner(), name -> {
            if (!name.equals("-1")) {
                welcomeText.setText("Welcome " + name + "!");
            }
        });

        budgetFramentLayout.setOnClickListener(view2 -> {
            view2.clearFocus();
            imm.hideSoftInputFromWindow(view2.getWindowToken(), 0);
        });
        return view;
    }

    private void setCurrencySheet() {
        currenciesRVAdapter = new CurrenciesRVAdapter(this);
        currencyRV.setAdapter(currenciesRVAdapter);
        currencyRV.setLayoutManager(new LinearLayoutManager(getContext()));
        currencySearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currenciesRVAdapter.searchUpdate(editable.toString());
            }
        });
        currentTotal.setOnClickListener(view -> currencySheet.show());

    }

    private void populateRV() {
        CustomCurrency c = trip.getCurrency();
        expensesRVAdapter = new ExpensesRVAdapter(this, trip.getBudget().getExpenses(), c);
        expensessRV.setAdapter(expensesRVAdapter);
        expensessRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        expensessRV.setNestedScrollingEnabled(false);
    }

    public void editExpense(int position) {
        Expense ex = trip.getBudget().getExpenses().get(position);
        amount.setText(String.valueOf(ex.getPrice()));
        currentPos = position;
        addExpenseTitle.setText(getString(R.string.ui_edit_expense));
        cancelButton.setText(getString(R.string.ui_delete));
        cancelButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
        cancelButton.setOnClickListener(view -> {
            trip.getBudget().deleteExpense(position);
            updateData();
            viewModel.updateTrip(trip);
            expensesRVAdapter.setExpenses(trip.getBudget().getExpenses());
            dismissExpenseSheet();
        });
        if (ex.getDescription() != (null) && !ex.getDescription().isEmpty())
            description.setText(ex.getDescription());
        expenseTypeSelector.setText(ex.getType().name());
        expenseTypeIcon.setImageResource(ex.getType().getImage());
        expenseCurrencySymbol.setText(trip.getCurrency().getSymbol());
        expenseCurrencySymbol.setOnClickListener(view -> currencySheet.show());
        expenseCurrencySelector.setOnClickListener(view -> currencySheet.show());
        type = ex.getType();
        addExpenseSheet.show();

    }

    private void dismissExpenseSheet() {
        updateData();
        amount.setText(null);
        description.setText(null);
        expenseTypeSelector.setText(getString(R.string.edittext_select_item_type));
        expenseTypeIcon.setImageResource(R.drawable.ic_receipt);
        type = null;

        addExpenseSheet.dismiss();
    }

    private void budgetSheet() {
        budgetCurrencySymbol.setText(trip.getCurrency().getSymbol());
        budgetET.setText(prettyPrint(trip.getBudget().getBudget()));
        budgetCurrencySymbol.setOnClickListener(view -> currencySheet.show());
        budgetCurrencySelector.setOnClickListener(view -> currencySheet.show());
        setBudgetLinearLayout.setOnClickListener(view -> {
            view.clearFocus();
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
        setBudgetCostLayout.setOnClickListener(view -> budgetET.requestFocus());
        budgetLinearLayout.setOnClickListener(view1 -> setBudgetSheet.show());
        cancelButton2.setOnClickListener(view -> setBudgetSheet.dismiss());
        saveButton2.setOnClickListener(view -> {
            if (!budgetET.getText().toString().isEmpty()) {
                double num = Double.parseDouble(budgetET.getText().toString());
                if (num >= 0) {
                    if (trip.getBudget() == null) {
                        Budget budget1 = new Budget(Double.parseDouble(budgetET.getText().toString()));
                        trip.setBudget(budget1);
                    } else
                        trip.getBudget().setBudget(Double.parseDouble(budgetET.getText().toString()));
                    viewModel.updateTrip(trip);
                    updateData();
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
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
        expenseCostLayout.setOnClickListener(view -> amount.requestFocus());
        expenseDescriptionLayout.setOnClickListener(view -> description.requestFocus());
        amount.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    assert selectItemTypeLayout != null;
                    selectItemTypeLayout.callOnClick();
                }
            }
            return false;
        });
        addExpenseButton.setOnClickListener(view1 -> {
            addExpenseSheet.show();
            addExpenseTitle.setText(getString(R.string.ui_add_expense));
            cancelButton.setText(getString(R.string.places_cancel));
            cancelButton.setOnClickListener(view -> addExpenseSheet.dismiss());
        });
        cancelButton.setOnClickListener(view -> addExpenseSheet.dismiss());
        addExpenseSheet.setOnDismissListener(dialogInterface -> {
            if (currentPos != -1) {
                amount.setText(null);
                description.setText(null);
                expenseTypeSelector.setText(getString(R.string.edittext_select_item_type));
                expenseTypeIcon.setImageResource(R.drawable.ic_receipt);
                type = null;
                currentPos = -1;
                cancelButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey));

            }
        });
        saveButton.setOnClickListener(view -> {
            if (!amount.getText().toString().isEmpty() && type != null) {
                BigDecimal bd = new BigDecimal(amount.getText().toString()).setScale(2, RoundingMode.HALF_UP);
                Expense expense = new Expense(description.getText().toString(), type, bd.doubleValue());
                if (currentPos != -1){
                    if (trip.getBudget().getExpenses().get(currentPos).hasId()){
                        expense.setId(trip.getBudget().getExpenses().get(currentPos).getId());
                        trip.getPlans().forEach(plan -> {
                            if (plan.getCost() != null){
                                if(plan.getCost().hasId()) {
                                    if (plan.getCost().getId().equals(expense.getId())) {
                                        plan.setCost(expense);
                                    }
                                }
                            }
                        });
                    }
                }

                if (trip.getBudget() == null) {
                    Budget budget1 = new Budget();
                    budget1.addExpense(expense);
                    trip.setBudget(budget1);
                } else if (currentPos != -1) {
                    trip.getBudget().editExpense(expense, currentPos);
                    currentPos = -1;
                } else {
                    trip.getBudget().addExpense(expense);
                }

                viewModel.updateTrip(trip);
                expensesRVAdapter.setExpenses(trip.getBudget().getExpenses());
                dismissExpenseSheet();

            } else
                Toast.makeText(getContext(), getString(R.string.budget_enter_expenses_details), Toast.LENGTH_SHORT).show();
        });

        rv.setAdapter(expenseTypesAdapter);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 3));

        Objects.requireNonNull(selectItemTypeLayout).setOnClickListener(v -> typesSheet.show());

    }

    public void selectExpenseType(ExpenseTypes type) {
        expenseTypeSelector.setText(type.name());
        expenseTypeIcon.setImageResource(type.getImage());
        this.type = type;
        typesSheet.dismiss();
    }

    private void updateData() {
        String finalBudget;
        if (trip.getBudget() == null || trip.getBudget().getBudget() == null || trip.getBudget().getBudget() == 0)
            finalBudget = "Set a Budget";
        else {
            BigDecimal bd = BigDecimal.valueOf(trip.getBudget().getBudget()).setScale(2, RoundingMode.HALF_UP);
            finalBudget = "Budget: " + trip.getCurrency().getSymbol() + prettyPrint(bd.doubleValue());
        }

        budget.setText(finalBudget);
        String finalCurrent = trip.getCurrency().getSymbol();
//                trip.getCurrency().getSymbol();
        if (trip.getBudget() == null || trip.getBudget().getBudget() == null)
            finalCurrent += "0.00";
        else {
            BigDecimal bd = BigDecimal.valueOf(trip.getBudget().getCurrentTally()).setScale(2, RoundingMode.HALF_UP);
            finalCurrent += prettyPrint(bd.doubleValue());
        }
        currentTotal.setText(finalCurrent);
        if (trip.getBudget() != null) {
            if (trip.getBudget().getBudget() != null && trip.getBudget().getBudget() != 0) {
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setMax(100);
                int progress = (int) ((trip.getBudget().getCurrentTally() / trip.getBudget().getBudget()) * 100);
                if (progress > 100) {
                    progress = 100;
                }
                if (progress == 100) {
                    currentTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                } else if (progress >= 70){
                    currentTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.actualOrange));
                }
                progressIndicator.setProgress(progress);

            } else {
                progressIndicator.setVisibility(View.GONE);
            }
        } else
            currentTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        populateRV();
        if (trip.hasBudgetEntries()){
            expensessRV.setVisibility(View.VISIBLE);
            noExpensesCard.setVisibility(View.GONE);
        }
        else{
            expensessRV.setVisibility(View.GONE);
            noExpensesCard.setVisibility(View.VISIBLE);
        }
    }


    public void setCurrency(Currency c) {
        trip.setCurrency(new CustomCurrency(c));
        viewModel.updateTrip(trip);
        updateData();
        expenseCurrencySymbol.setText(trip.getCurrency().getSymbol());
        budgetCurrencySymbol.setText(trip.getCurrency().getSymbol());

        currencySheet.dismiss();
    }
}