package com.example.myapplication.pedidos;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class OrderAdapter extends FragmentStateAdapter {

    public OrderAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return OrderDetailsFragment.newInstance("pending");
            case 1: return OrderDetailsFragment.newInstance("processing");
            case 2: return OrderDetailsFragment.newInstance("confirmed");
            case 3: return OrderDetailsFragment.newInstance("delivered");
            case 4: return OrderDetailsFragment.newInstance("cancelled");
            default: return OrderDetailsFragment.newInstance("pending");
        }
    }

    @Override
    public int getItemCount() { return 5; }
}