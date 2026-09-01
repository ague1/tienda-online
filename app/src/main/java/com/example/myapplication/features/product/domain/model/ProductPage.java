package com.example.myapplication.features.product.domain.model;

import com.example.myapplication.features.product.domain.cursor.ProductPageCursor;

import java.util.List;

public class ProductPage {

    private final List<Product> products;
    private final ProductPageCursor nextCursor;
    private final boolean hasMore;

    public ProductPage(
            List<Product> products,
            ProductPageCursor nextCursor,
            boolean hasMore
    ) {
        this.products = products;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    public List<Product> getProducts() {
        return products;
    }

    public ProductPageCursor getNextCursor() {
        return nextCursor;
    }

    public boolean hasMore() {
        return hasMore;
    }
}