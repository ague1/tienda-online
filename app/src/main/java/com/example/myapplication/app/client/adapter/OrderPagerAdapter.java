package com.example.myapplication.app.client.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.app.client.fragment.OrderDetailsFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {

    public OrderPagerAdapter(@NonNull Fragment fragment) {
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