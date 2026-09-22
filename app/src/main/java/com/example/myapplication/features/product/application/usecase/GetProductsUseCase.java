package com.example.myapplication.features.product.application.usecase;

import com.example.myapplication.features.product.domain.port.repository.ProductRepository;
import com.example.myapplication.features.product.domain.cursor.CategoryProductCursor;
import com.example.myapplication.features.product.domain.cursor.ProductPageCursor;
import com.example.myapplication.features.product.domain.cursor.SearchCursor;
import com.example.myapplication.features.product.domain.model.CategoryProductPage;
import com.example.myapplication.features.product.domain.model.ProductPage;
import com.example.myapplication.features.product.domain.model.SearchPage;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.Locale;

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

    public Task<ProductPage> getFirstPage() {
        return repository.getFirstPage(PAGE_SIZE);
    }

    public Task<ProductPage> getNextPage(
            ProductPageCursor cursor
    ) {

        if (cursor == null) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "El cursor no puede ser null"
                    )
            );
        }

        return repository.getNextPage(
                cursor,
                PAGE_SIZE
        );
    }

    public Task<SearchPage> searchProducts(
            String query
    ) {

        if (query == null) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "La búsqueda no puede ser null"
                    )
            );
        }

        String normalizedQuery =
                query.trim()
                        .toLowerCase(Locale.ROOT);

        if (normalizedQuery.isEmpty()) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "La búsqueda no puede estar vacía"
                    )
            );
        }

        return repository.searchProducts(
                normalizedQuery,
                PAGE_SIZE
        );
    }

    public Task<SearchPage> searchProductsNextPage(
            String query,
            SearchCursor cursor
    ) {

        if (cursor == null) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "El cursor no puede ser null"
                    )
            );
        }

        String normalizedQuery =
                query == null
                        ? ""
                        : query.trim()
                        .toLowerCase(Locale.ROOT);

        if (normalizedQuery.isEmpty()) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "La búsqueda no puede estar vacía"
                    )
            );
        }

        return repository.searchProductsNextPage(
                normalizedQuery,
                cursor,
                PAGE_SIZE
        );
    }

    public Task<CategoryProductPage> getProductsByCategory(
            String category
    ) {

        if (category == null) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "La categoría no puede ser null"
                    )
            );
        }

        String normalizedCategory =
                category.trim();

        if (normalizedCategory.isEmpty()) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "La categoría no puede estar vacía"
                    )
            );
        }

        return repository.getProductsByCategory(
                normalizedCategory,
                PAGE_SIZE
        );
    }

    public Task<CategoryProductPage> getProductsByCategoryNextPage(
            String category,
            CategoryProductCursor cursor
    ) {

        if (cursor == null) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "El cursor no puede ser null"
                    )
            );
        }

        String normalizedCategory =
                category == null
                        ? ""
                        : category.trim();

        if (normalizedCategory.isEmpty()) {
            return Tasks.forException(
                    new IllegalArgumentException(
                            "La categoría no puede estar vacía"
                    )
            );
        }

        return repository.getProductsByCategoryNextPage(
                normalizedCategory,
                cursor,
                PAGE_SIZE
        );
    }
}

