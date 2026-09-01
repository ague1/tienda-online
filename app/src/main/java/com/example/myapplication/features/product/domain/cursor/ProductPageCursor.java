package com.example.myapplication.features.product.domain.cursor;

public class ProductPageCursor {

    private final String nombre;
    private final String documentId;

    public ProductPageCursor(
            String nombre,
            String documentId
    ) {
        this.nombre = nombre;
        this.documentId = documentId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumentId() {
        return documentId;
    }
}
