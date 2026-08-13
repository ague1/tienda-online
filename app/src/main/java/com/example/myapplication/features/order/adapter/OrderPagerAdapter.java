package com.example.myapplication.features.order.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.features.order.fragment.OrderDetailsFragment;

public class OrderPagerAdapter extends FragmentStateAdapter {
    private static final String[] STATUSES = {
            "pending",
            "processing",
            "confirmed",
            "delivered",
            "cancelled"
    };

    public OrderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OrderDetailsFragment.newInstance(STATUSES[position]);
    }

    @Override
    public int getItemCount() {
        return STATUSES.length;
    }

    public String getStatus(int position) {
        return STATUSES[position];
    }
}