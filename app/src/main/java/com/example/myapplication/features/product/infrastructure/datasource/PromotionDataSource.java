package com.example.myapplication.features.product.infrastructure.datasource;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import javax.inject.Inject;

public class PromotionDataSource {

    private static final String COLLECTION_PROMOTIONS = "promotion";

    private final FirebaseFirestore db;

    @Inject
    public PromotionDataSource(
            FirebaseFirestore db
    ) {
        this.db = db;
    }

    public Task<QuerySnapshot> getFirstPage(
            int pageSize
    ) {

        return db.collection(COLLECTION_PROMOTIONS)
                .whereEqualTo("active", true)
                .orderBy("specialPrice")
                .orderBy(FieldPath.documentId())
                .limit(pageSize + 1)
                .get();
    }

    public Task<QuerySnapshot> getNextPage(
            DocumentSnapshot lastDocument,
            int pageSize
    ) {

        return db.collection(COLLECTION_PROMOTIONS)
                .whereEqualTo("active", true)
                .orderBy("specialPrice")
                .orderBy(FieldPath.documentId())
                .startAfter(
                        lastDocument.getLong("specialPrice"),
                        lastDocument.getId()
                )
                .limit(pageSize + 1)
                .get();
    }

    public Task<QuerySnapshot> getActivePromotionsByProductIds(
            List<String> productIds
    ) {

        if (productIds == null || productIds.isEmpty()) {
            return Tasks.forResult(null);
        }

        return db.collection(COLLECTION_PROMOTIONS)
                .whereEqualTo("active", true)
                .whereIn("productId", productIds)
                .get();
    }


}
