package com.example.myapplication.features.product.domain.model;

public final class PricedProduct {

    private final Product product;
    private final long price;

    public PricedProduct(
            Product product,
            long price
    ) {
        if (product == null) {
            throw new IllegalArgumentException(
                    "product no puede ser null"
            );
        }

        if (price < 0) {
            throw new IllegalArgumentException(
                    "price no puede ser negativo"
            );
        }

        this.product = product;
        this.price = price;
    }

    public Product getProduct() {
        return product;
    }

    public long getPrice() {
        return price;
    }
}

