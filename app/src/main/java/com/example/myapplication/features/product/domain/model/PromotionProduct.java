package com.example.myapplication.features.product.domain.model;

public class PromotionProduct {

    private final Product product;
    private final double specialPrice;

    public PromotionProduct(
            Product product,
            double specialPrice
    ) {
        this.product = product;
        this.specialPrice = specialPrice;
    }

    public Product getProduct() {
        return product;
    }

    public double getSpecialPrice() {
        return specialPrice;
    }
}

