package com.example.myapplication.features.order.infrastructure.datasource;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;
public class OrderDataSource {

    private final FirebaseFirestore db;

    @Inject
    public OrderDataSource(
            FirebaseFirestore db
    ) {
        this.db = db;
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
                .addSnapshotListener(
                        (snapshots, error) -> {

                            if (error != null) {
                                listener.onEvent(
                                        null,
                                        error
                                );
                                return;
                            }

                            if (snapshots == null) {
                                return;
                            }

                            listener.onEvent(
                                    snapshots,
                                    null
                            );
                        }
                );
    }


    public ListenerRegistration listenOrder(
            String orderId,
            EventListener<DocumentSnapshot> listener
    ) {

        return db.collection("orders")
                .document(orderId)
                .addSnapshotListener(
                        listener
                );
    }

    public Task<Void> saveOrder(
            String orderId,
            Map<String, Object> order
    ) {

        return db.collection("orders")
                .document(orderId)
                .set(order);
    }

    public void saveOrderInTransaction(
            Transaction transaction,
            String orderId,
            Map<String, Object> order
    ) {

        DocumentReference orderRef =
                db.collection("orders")
                        .document(orderId);

        transaction.set(
                orderRef,
                order
        );
    }

    public Task<QuerySnapshot> getExpiredPendingOrders(
            Date now
    ) {

        return db.collection("orders")
                .whereEqualTo("status", "pending")
                .whereLessThanOrEqualTo(
                        "paymentDeadline",
                        now
                )
                .get();
    }


    public DocumentSnapshot getOrderInTransaction(
            Transaction transaction,
            String orderId
    ) throws FirebaseFirestoreException {

        DocumentReference orderRef =
                db.collection("orders")
                        .document(orderId);

        return transaction.get(orderRef);
    }


    public void updateOrderStatusInTransaction(
            Transaction transaction,
            String orderId,
            String status
    ) {

        DocumentReference orderRef =
                db.collection("orders")
                        .document(orderId);

        transaction.update(
                orderRef,
                "status",
                status
        );
    }

    public void confirmOrderPaymentInTransaction(
            Transaction transaction,
            String orderId
    ) {

        DocumentReference orderRef =
                db.collection("orders")
                        .document(orderId);

        transaction.update(
                orderRef,
                "status",
                "confirmed",
                "timeline.confirmed",
                new Date()
        );
    }

}

