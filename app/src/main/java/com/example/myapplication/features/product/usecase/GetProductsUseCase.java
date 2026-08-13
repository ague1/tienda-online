package com.example.myapplication.features.product.usecase;

import com.example.myapplication.features.product.model.Product;
import com.example.myapplication.features.product.repository.ProductRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;

public class GetProductsUseCase {


    private ProductRepository repository;

    @Inject
    public GetProductsUseCase(ProductRepository repository){

        this.repository = repository;

    }

    public Task<List<Product>> execute() {
        return repository.getProducts();
    }

}
