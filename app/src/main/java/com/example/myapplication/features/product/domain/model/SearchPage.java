package com.example.myapplication.features.product.domain.model;


import com.example.myapplication.features.product.domain.cursor.SearchCursor;

import java.util.List;

public class SearchPage {

    private final List<Product> products;
    private final SearchCursor nextCursor;
    private final boolean hasMore;

    public SearchPage(
            List<Product> products,
            SearchCursor nextCursor,
            boolean hasMore
    ) {
        this.products = products;
        this.nextCursor = nextCursor;
        this.hasMore = hasMore;
    }

    public List<Product> getProducts() {
        return products;
    }

    public SearchCursor getNextCursor() {
        return nextCursor;
    }

    public boolean hasMore() {
        return hasMore;
    }
}