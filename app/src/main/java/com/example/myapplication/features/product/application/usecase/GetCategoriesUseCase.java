package com.example.myapplication.features.product.application.usecase;

import com.example.myapplication.features.product.domain.model.Category;
import com.example.myapplication.features.product.data.repository.CategoryRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;


public class GetCategoriesUseCase {

    private final CategoryRepository repository;

    @Inject
    public GetCategoriesUseCase(
            CategoryRepository repository
    ) {
        this.repository = repository;
    }

    public Task<List<Category>> execute() {
        return repository.getCategories();
    }
}


