package com.example.myapplication.app.client.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.app.client.adapter.OrderPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ClientOrderStatusFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_products, container, false);

        tabLayout = view.findViewById(R.id.tabLayoutOrders);
        viewPager = view.findViewById(R.id.viewPagerOrders);

        OrderPagerAdapter adapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Pendiente"); break;
                case 1: tab.setText("Procesando"); break;
                case 2: tab.setText("En Agenda"); break;
                case 3: tab.setText("Entregados"); break;
                case 4: tab.setText("Cancelados"); break;
            }
        }).attach();

        return view;
    }

    public void setTabByStatus(String status) {

        int index = 0;

        switch (status) {
            case "pending":
                index = 0;
                break;

            case "processing":
                index = 1;
                break;

            case "confirmed":
                index = 2;
                break;

            case "delivered":
                index = 3;
                break;

            case "cancelled":
                index = 4;
                break;
        }

        if (viewPager != null) {
            viewPager.setCurrentItem(index, true);
        }
    }
}