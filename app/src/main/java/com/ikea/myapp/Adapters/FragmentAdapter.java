package com.ikea.myapp.Adapters;

import android.util.Log;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {

    private List<Fragment> fragments;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Fragment> fragments) {
        super(fragmentManager, lifecycle);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment tmp = fragments.get(position);
        return tmp;
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}
