package com.example.myapplication.features.order.presentation.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.R;

public class ClientOrderHistoryFragment extends Fragment {


    public ClientOrderHistoryFragment() {
        // Required empty public constructor
    }

    public static ClientOrderHistoryFragment newInstance(String param1, String param2) {
        ClientOrderHistoryFragment fragment = new ClientOrderHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_client_order_history, container, false);
    }
}