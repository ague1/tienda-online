package com.example.myapplication.features.product.domain.cursor;

public class CategoryProductCursor {

    private final String keySearch;
    private final String documentId;

    public CategoryProductCursor(
            String keySearch,
            String documentId
    ) {
        this.keySearch = keySearch;
        this.documentId = documentId;
    }

    public String getKeySearch() {
        return keySearch;
    }

    public String getDocumentId() {
        return documentId;
    }
}
