package com.example.myapplication.features.product.data.firebase.datasource;

import com.example.myapplication.features.product.domain.cursor.CategoryProductCursor;
import com.example.myapplication.features.product.domain.cursor.ProductPageCursor;
import com.example.myapplication.features.product.domain.cursor.SearchCursor;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class ProductDataSource {

    private static final String COLLECTION_PRODUCTS = "productos";

    private final FirebaseFirestore db;

    @Inject
    public ProductDataSource(
            FirebaseFirestore db
    ) {
        this.db = db;
    }

    public Task<QuerySnapshot> getFirstPage(int pageSize) {

        return db.collection(COLLECTION_PRODUCTS)
                .orderBy("nombre")
                .orderBy(FieldPath.documentId())
                .limit(pageSize + 1)
                .get();
    }

    public Task<QuerySnapshot> getNextPage(
            ProductPageCursor cursor,
            int pageSize
    ) {

        return db.collection(COLLECTION_PRODUCTS)
                .orderBy("nombre")
                .orderBy(FieldPath.documentId())
                .startAfter(
                        cursor.getNombre(),
                        cursor.getDocumentId()
                )
                .limit(pageSize + 1)
                .get();
    }

    public Task<QuerySnapshot> getProductsByIds(
            List<String> productIds
    ) {

        if (productIds == null || productIds.isEmpty()) {
            return Tasks.forResult(null);
        }

        return db.collection(COLLECTION_PRODUCTS)
                .whereIn(
                        FieldPath.documentId(),
                        productIds
                )
                .get();
    }

    public Task<QuerySnapshot> searchProducts(
            String query,
            int pageSize
    ) {

        if (query == null || query.trim().isEmpty()) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "La búsqueda no puede estar vacía"
                    )
            );
        }

        String search = query
                .trim()
                .toLowerCase(Locale.ROOT);

        return db.collection(COLLECTION_PRODUCTS)
                .whereGreaterThanOrEqualTo(
                        "keySearch",
                        search
                )
                .whereLessThanOrEqualTo(
                        "keySearch",
                        search + "\uf8ff"
                )
                .orderBy("keySearch")
                .orderBy(FieldPath.documentId())
                .limit(pageSize + 1)
                .get();
    }

    public Task<QuerySnapshot> searchProductsNextPage(
            String query,
            SearchCursor cursor,
            int pageSize
    ) {

        String search = query
                .trim()
                .toLowerCase(Locale.ROOT);

        return db.collection(COLLECTION_PRODUCTS)
                .whereGreaterThanOrEqualTo(
                        "keySearch",
                        search
                )
                .whereLessThanOrEqualTo(
                        "keySearch",
                        search + "\uf8ff"
                )
                .orderBy("keySearch")
                .orderBy(FieldPath.documentId())
                .startAfter(
                        cursor.getKeySearch(),
                        cursor.getDocumentId()
                )
                .limit(pageSize + 1)
                .get();
    }

    public Task<QuerySnapshot> getProductsByCategory(
            String category,
            int pageSize
    ) {

        return db.collection(COLLECTION_PRODUCTS)
                .whereEqualTo(
                        "categoria",
                        category
                )
                .orderBy("keySearch")
                .orderBy(FieldPath.documentId())
                .limit(pageSize + 1)
                .get();
    }

    public Task<QuerySnapshot> getProductsByCategoryNextPage(
            String category,
            CategoryProductCursor cursor,
            int pageSize
    ) {

        return db.collection(COLLECTION_PRODUCTS)
                .whereEqualTo(
                        "categoria",
                        category
                )
                .orderBy("keySearch")
                .orderBy(FieldPath.documentId())
                .startAfter(
                        cursor.getKeySearch(),
                        cursor.getDocumentId()
                )
                .limit(pageSize + 1)
                .get();
    }
}
