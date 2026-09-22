package com.example.myapplication.features.product.domain.model;

public class PromotionProduct {

    private final Product product;
    private final long specialPrice;

    public PromotionProduct(
            Product product,
            long specialPrice
    ) {
        this.product = product;
        this.specialPrice = specialPrice;
    }

    public Product getProduct() {
        return product;
    }

    public long getSpecialPrice() {
        return specialPrice;
    }
}

