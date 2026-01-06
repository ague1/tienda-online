package com.example.myapplication.pedidos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDetailsFragment extends Fragment {

    private static final String ARG_STATUS = "arg_status";
    private String status;
    private RecyclerView recycler;
    private TextView emptyText;
    private pedidoAdapter adapter;

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
        adapter = new pedidoAdapter(getContext(), new ArrayList<>());
        recycler.setAdapter(adapter);
        loadOrders();
        return view;
    }

    private void loadOrders() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("OrderDetails", "User is null, cannot load orders.");

            emptyText.setText("Please log in to view your orders.");
            emptyText.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
            return;
        }

        String uid = user.getUid();

        // orderBy requires an index when combined with whereEqualTo on different fields in some cases;
        // if Firestore asks you to create an index, crea el índice desde la consola (Firestore te da el enlace).
        db.collection("orders")
                .whereEqualTo("userId", uid)
                .whereEqualTo("status", status)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Log.e("FIREBASE", "Error: " + error.getMessage());
                        return;
                    }

                    if (value == null || value.isEmpty()) {
                        adapter.updateOrders(new ArrayList<>());
                        emptyText.setVisibility(View.VISIBLE);
                        recycler.setVisibility(View.GONE);
                        return;
                    }
                    List<DocumentSnapshot> docs = value.getDocuments();
                    adapter.updateOrders(docs);
                    emptyText.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                });
    }
}