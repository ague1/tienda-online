package com.example.myapplication.features.cart.infrastructure.persistence;

import com.example.myapplication.features.cart.domain.model.CartItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
public interface CartDataSource {


    Task<Void> saveCart(
            List<CartItem> items
    );

    List<CartItem> documentToItems(
            DocumentSnapshot document
    );

    ListenerRegistration listenCart(
            EventListener<DocumentSnapshot> listener
    );
}

