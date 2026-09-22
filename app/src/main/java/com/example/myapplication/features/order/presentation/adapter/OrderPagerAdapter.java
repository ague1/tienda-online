package com.example.myapplication.features.order.presentation.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.features.order.presentation.fragment.OrderDetailsFragment;

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


    public static OrderDetailsFragment newInstance(String status) {

        OrderDetailsFragment fragment =
                new OrderDetailsFragment();

        Bundle args = new Bundle();
        args.putString("status", status);

        fragment.setArguments(args);

        return fragment;
    }

    public int getPositionByStatus(String status) {
        for (int i = 0; i < STATUSES.length; i++) {
            if (STATUSES[i].equals(status)) {
                return i;
            }
        }

        return 0;
    }

}