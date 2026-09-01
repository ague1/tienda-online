package com.example.myapplication.features.product.domain.model;

import com.example.myapplication.features.product.domain.cursor.CategoryProductCursor;

import java.util.List;

public class CategoryProductPage {

    private final List<Product> products;
    private final CategoryProductCursor nextCursor;
    private final boolean hasMore;

    public CategoryProductPage(
            List<Product> products,
            CategoryProductCursor nextCursor,
            boolean hasMore
    ) {
        this.products = products;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    public List<Product> getProducts() {
        return products;
    }

    public CategoryProductCursor getNextCursor() {
        return nextCursor;
    }

    public boolean hasMore() {
        return hasMore;
    }
}
