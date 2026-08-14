package com.example.myapplication.features.product.usecase;

import com.example.myapplication.features.product.model.Category;
import com.example.myapplication.features.product.repository.CategoryRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import javax.inject.Inject;


public class GetCategoriesUseCase {

    private CategoryRepository repository;

    @Inject
    public GetCategoriesUseCase(CategoryRepository repository){
        this.repository = repository;

    }

    public void execute(
            OnSuccessListener<List<Category>> listener
    ) {
        repository.getCategories(listener);
    }
}
