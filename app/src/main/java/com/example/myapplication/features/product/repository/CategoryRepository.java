package com.example.myapplication.features.product.repository;


import com.example.myapplication.features.product.model.Category;
import com.example.myapplication.features.product.firebase.CategoryDataSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CategoryRepository {

    private final CategoryDataSource dataSource;

    @Inject
    public CategoryRepository(CategoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void getCategories(
            OnSuccessListener<List<Category>> listener
    ) {

        dataSource.getCategories(snapshot -> {

            List<Category> categories = new ArrayList<>();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {

                Category category = new Category();

                category.setId(doc.getId());
                category.setName(doc.getString("name"));
                category.setImage(doc.getString("image"));

                categories.add(category);
            }

            listener.onSuccess(categories);
        });
    }
}
