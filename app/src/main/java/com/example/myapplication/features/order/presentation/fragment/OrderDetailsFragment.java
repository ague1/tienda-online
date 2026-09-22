package com.example.myapplication.features.order.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.features.order.presentation.viewmodel.OrderDetailsViewModel;
import com.example.myapplication.features.order.presentation.adapter.OrderListAdapter;
import com.example.myapplication.features.order.domain.model.Order;


import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderDetailsFragment extends Fragment {

    private static final String ARG_STATUS = "arg_status";
    private String status;
    private RecyclerView recycler;
    private TextView emptyText;
    private OrderListAdapter adapter;

    private OrderDetailsViewModel viewModel;

    public OrderDetailsFragment() {
        // Required empty constructor
    }

    public static OrderDetailsFragment newInstance(String status) {

        OrderDetailsFragment fragment =
                new OrderDetailsFragment();

        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(
            @Nullable Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
   public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_order_details,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(
                view,
                savedInstanceState
        );

        recycler =
                view.findViewById(
                        R.id.recyclerOrders
                );

        emptyText =
                view.findViewById(
                        R.id.emptyText
                );

        recycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        adapter =
                new OrderListAdapter(
                        requireContext(),
                        new ArrayList<>()
                );

        recycler.setAdapter(adapter);

        viewModel =
                new ViewModelProvider(this)
                        .get(OrderDetailsViewModel.class);

        observeOrders();

        viewModel.loadOrders(status);
    }

    private void observeOrders() {

        viewModel.getState().observe(
                getViewLifecycleOwner(),
                state -> {

                    if (state == null) {
                        return;
                    }

                    switch (state.getStatus()) {

                        case IDLE:
                            break;

                        case LOADING:

                            emptyText.setVisibility(
                                    View.GONE
                            );

                            recycler.setVisibility(
                                    View.GONE
                            );

                            break;

                        case SUCCESS:

                            List<Order> orders =
                                    state.getData();

                            if (orders == null ||
                                    orders.isEmpty()) {

                                adapter.updateOrders(
                                        new ArrayList<>()
                                );

                                emptyText.setText(
                                        "No tienes pedidos en este estado."
                                );

                                emptyText.setVisibility(
                                        View.VISIBLE
                                );

                                recycler.setVisibility(
                                        View.GONE
                                );

                            } else {

                                adapter.updateOrders(
                                        orders
                                );

                                emptyText.setVisibility(
                                        View.GONE
                                );

                                recycler.setVisibility(
                                        View.VISIBLE
                                );
                            }

                            break;

                        case ERROR:

                            adapter.updateOrders(
                                    new ArrayList<>()
                            );

                            emptyText.setText(
                                    getErrorMessage(
                                            state.getError()
                                    )
                            );

                            emptyText.setVisibility(
                                    View.VISIBLE
                            );

                            recycler.setVisibility(
                                    View.GONE
                            );

                            break;
                    }
                }
        );
    }

    private String getErrorMessage(String error) {

        if ("AUTH_REQUIRED".equals(error)) {
            return "Please log in to view your orders.";
        }

        if ("ORDER_STATUS_REQUIRED".equals(error)) {
            return "No se pudo determinar el estado de los pedidos.";
        }

        return "No se pudieron cargar tus pedidos.";
    }
}
