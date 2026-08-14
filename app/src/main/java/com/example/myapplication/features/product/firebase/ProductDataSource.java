package com.example.myapplication.features.product.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

public class ProductDataSource {

    private final FirebaseFirestore db;

    @Inject
    public ProductDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<QuerySnapshot> getProducts() {
        return db.collection("productos").get();
    }
}
