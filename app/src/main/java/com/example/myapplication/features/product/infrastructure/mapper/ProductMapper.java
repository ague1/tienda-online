package com.example.myapplication.features.product.infrastructure.mapper;

import com.example.myapplication.features.product.infrastructure.document.ProductDocument;
import com.example.myapplication.features.product.domain.model.Product;

import javax.inject.Inject;

public class ProductMapper {

    @Inject
    public ProductMapper() {
    }

    public Product toDomain(
            String id,
            ProductDocument document
    ) {

        if (document == null) {
            return null;
        }

        if (id == null ||
                id.trim().isEmpty()) {
            return null;
        }

        return new Product(
                id,
                document.getNombre(),
                document.getDescripcion(),
                document.getCategoria(),
                document.getPrecio(),
                document.getStock(),
                document.getTotalSold(),
                document.getImage()
        );
    }
}

