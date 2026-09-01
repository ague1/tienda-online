package com.example.myapplication.features.product.application.usecase;

import com.example.myapplication.features.product.domain.model.SearchPage;
import com.example.myapplication.features.product.domain.cursor.CategoryProductCursor;
import com.example.myapplication.features.product.domain.model.CategoryProductPage;
import com.example.myapplication.features.product.domain.model.ProductPage;
import com.example.myapplication.features.product.domain.cursor.ProductPageCursor;
import com.example.myapplication.features.product.domain.cursor.SearchCursor;
import com.example.myapplication.features.product.data.repository.ProductRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

public class GetProductsUseCase {

    private static final int PAGE_SIZE = 4;

    private final ProductRepository repository;

    @Inject
    public GetProductsUseCase(
            ProductRepository repository
    ) {
        this.repository = repository;
    }

    // Productos normales
    public Task<ProductPage> getFirstPage() {
        return repository.getFirstPage(PAGE_SIZE);
    }

    public Task<ProductPage> getNextPage(
            ProductPageCursor cursor
    ) {

        return repository.getNextPage(
                cursor,
                PAGE_SIZE
        );
    }

    // Búsqueda
    public Task<SearchPage> searchProducts(
            String query
    ) {

        return repository.searchProducts(
                query,
                PAGE_SIZE
        );
    }

    public Task<SearchPage> searchProductsNextPage(
            String query,
            SearchCursor cursor
    ) {

        return repository.searchProductsNextPage(
                query,
                cursor,
                PAGE_SIZE
        );
    }

    // Categorías
    public Task<CategoryProductPage> getProductsByCategory(
            String category
    ) {
        return repository.getProductsByCategory(
                category,
                PAGE_SIZE
        );
    }

    public Task<CategoryProductPage> getProductsByCategoryNextPage(
            String category,
            CategoryProductCursor cursor
    ) {
        return repository.getProductsByCategoryNextPage(
                category,
                cursor,
                PAGE_SIZE
        );
    }
}