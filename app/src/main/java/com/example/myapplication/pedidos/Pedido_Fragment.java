package com.example.myapplication.pedidos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Pedido_Fragment extends Fragment {
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

        OrderAdapter adapter = new OrderAdapter(this);
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