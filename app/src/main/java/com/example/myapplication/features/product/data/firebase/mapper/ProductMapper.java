package com.example.myapplication.features.product.data.firebase.mapper;

import com.example.myapplication.features.product.data.firebase.document.ProductDocument;
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
