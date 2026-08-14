package com.example.myapplication.features.product.firebase;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

public class CategoryDataSource {

    private FirebaseFirestore db;

    @Inject
    public CategoryDataSource() {
        db = FirebaseFirestore.getInstance();
    }

    public void getCategories(
            OnSuccessListener<QuerySnapshot> listener
    ) {
        db.collection("category")
                .get()
                .addOnSuccessListener(listener);
    }
}
