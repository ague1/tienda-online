package com.example.myapplication.features.product.infrastructure.datasource;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import javax.inject.Inject;

public class CategoryDataSource {

    private static final String COLLECTION_CATEGORIES = "category";

    private final FirebaseFirestore db;

    @Inject
    public CategoryDataSource(
            FirebaseFirestore db
    ) {
        this.db = db;
    }

    public Task<QuerySnapshot> getCategories() {
        return db.collection(COLLECTION_CATEGORIES)
                .get();
    }
}
