package com.example.myapplication.features.order.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.example.myapplication.features.order.adapter.OrderPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ClientOrderStatusFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String initialStatus;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(R.layout.fragment_products,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.tabLayoutOrders);
        viewPager = view.findViewById(R.id.viewPagerOrders);
        OrderPagerAdapter adapter = new OrderPagerAdapter(this);
        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch(position){
                case 0: tab.setText("Pendiente");
                break;
                case 1: tab.setText("Procesando");
                break;
                case 2: tab.setText("En agenda");
                break;
                case 3: tab.setText("Entregados");
                break;
                case 4: tab.setText("Cancelados");
                break;
            }

        }
        ).attach();

        if(getArguments()!=null){

            initialStatus =
                    getArguments()
                            .getString("status");

            if(initialStatus != null){

                viewPager.post(() ->
                        setTabByStatus(initialStatus)
                );

            }
        }
    }
    public void setTabByStatus(String status){

        int position = 0;
        switch(status){
            case "pending":
                position = 0;
                break;

            case "processing":
                position = 1;
                break;

            case "confirmed":
                position = 2;
                break;

            case "delivered":
                position = 3;
                break;

            case "cancelled":
                position = 4;
                break;
        }
        viewPager.setCurrentItem(position,true);
    }
}