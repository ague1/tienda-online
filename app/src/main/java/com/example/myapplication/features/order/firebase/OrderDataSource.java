package com.example.myapplication.features.order.firebase;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import javax.inject.Inject;

public class OrderDataSource {

    private final FirebaseFirestore db;
    @Inject
    public OrderDataSource(){
        db = FirebaseFirestore.getInstance();

    }

    public ListenerRegistration listenOrdersByStatus(
            String userId,
            String status,
            EventListener<QuerySnapshot> listener
    ) {

        return db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", status)
                .orderBy(
                        "timestamp",
                        Query.Direction.DESCENDING
                )
                .addSnapshotListener((snapshots, error) -> {

                    if (error != null) {
                        Log.e(
                                "OrderDataSource",
                                "ERROR cargando pedidos. userId=" + userId
                                        + " status=" + status,
                                error
                        );
                        return;
                    }

                    if (snapshots == null) {
                        Log.e("OrderDataSource", "snapshots == null");
                        return;
                    }

                    Log.d(
                            "OrderDataSource",
                            "Pedidos encontrados: " + snapshots.size()
                                    + " | userId=" + userId
                                    + " | status=" + status
                    );

                    listener.onEvent(snapshots, null);
                });
    }

    public ListenerRegistration listenOrder(
            String orderId,
            EventListener<DocumentSnapshot> listener
    ){

        return db.collection("orders")
                .document(orderId)
                .addSnapshotListener(listener);

    }

    public Task<Void> saveOrder(String orderId, Map<String, Object> order
    ) {
        return db.collection("orders")
                .document(orderId)
                .set(order);
    }

}
