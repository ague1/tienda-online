package com.example.myapplication.features.order.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.features.auth.repository.AuthRepository;
import com.example.myapplication.features.order.adapter.OrderListAdapter;
import com.example.myapplication.features.order.model.Order;
import com.example.myapplication.features.order.repository.OrderRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderDetailsFragment extends Fragment {

    private static final String ARG_STATUS = "arg_status";
    private String status;
    private RecyclerView recycler;
    private TextView emptyText;
    private OrderListAdapter adapter;
    @Inject
    OrderRepository orderRepository;
    @Inject
    AuthRepository authRepository;
    private ListenerRegistration orderListener;

    public OrderDetailsFragment() { /* Required empty constructor */ }

    public static OrderDetailsFragment newInstance(String status) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
        recycler = view.findViewById(R.id.recyclerOrders);
        emptyText = view.findViewById(R.id.emptyText);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderListAdapter(getContext(), new ArrayList<>());
        recycler.setAdapter(adapter);
        loadOrders();
        return view;
    }

    private void loadOrders() {

        String uid = authRepository.getCurrentUserId();
        if(uid == null){

            emptyText.setText("Please log in to view your orders.");
            emptyText.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }


        // orderBy requires an index when combined with whereEqualTo on different fields in some cases;
        // if Firestore asks you to create an index, crea el índice desde la consola (Firestore te da el enlace).
        orderListener = orderRepository.listenOrdersByStatus(uid, status, (value, error) -> {

            if(error != null){
                Log.e("OrderDetails", error.getMessage());
                return;
            }

            if(value == null || value.isEmpty()){
                adapter.updateOrders(new ArrayList<>());
                emptyText.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
                return;
            }

            List<Order> orders = orderRepository.mapList(value.getDocuments());
            adapter.updateOrders(orders);
            emptyText.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(orderListener != null){
            orderListener.remove();
            orderListener = null;
        }
    }
}