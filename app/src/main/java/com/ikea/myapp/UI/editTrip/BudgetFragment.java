package com.ikea.myapp.UI.editTrip;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ikea.myapp.MyTrip;
import com.ikea.myapp.R;

public class BudgetFragment extends Fragment {
    private BudgetViewModel viewModel;
    private MyTrip trip;

    public BudgetFragment(MyTrip trip) { this.trip = trip; }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.budget_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BudgetViewModel.class);
    }

}