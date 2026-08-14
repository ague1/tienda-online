package com.example.myapplication.features.product.repository;

import com.example.myapplication.features.product.model.Product;
import com.example.myapplication.features.product.firebase.ProductDataSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ProductRepository {

    private final ProductDataSource dataSource;

    @Inject
    public ProductRepository(ProductDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Task<List<Product>> getProducts() {

        return dataSource.getProducts()
                .continueWith(task -> {

                    QuerySnapshot snapshot = task.getResult();

                    List<Product> products = new ArrayList<>();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {

                        Product product = document.toObject(Product.class);

                        if (product != null) {
                            product.setId(document.getId());
                            products.add(product);
                        }
                    }

                    return products;
                });
    }
}
