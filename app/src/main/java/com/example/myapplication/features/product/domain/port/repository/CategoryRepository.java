package com.example.myapplication.features.product.domain.port.repository;


import com.example.myapplication.features.product.domain.model.Category;
import com.example.myapplication.features.product.infrastructure.datasource.CategoryDataSource;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CategoryRepository {

    private final CategoryDataSource dataSource;

    @Inject
    public CategoryRepository(CategoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Task<List<Category>> getCategories() {
        return dataSource.getCategories()
                .continueWith(task -> {

                    QuerySnapshot snapshot = task.getResult();

                    List<Category> categories =
                            new ArrayList<>(snapshot.size());
                    for (DocumentSnapshot doc : snapshot) {

                        categories.add(
                                new Category(
                                        doc.getId(),
                                        doc.getString("name"),
                                        doc.getString("image")
                                )
                        );
                    }

                    return categories;
                });
    }
}
