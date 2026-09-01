package com.example.myapplication.features.cart;

import com.example.myapplication.features.product.domain.model.Product;

public interface OnCartClickListener {

    void onAdd(
            Product product,
            double price
    );

    void onIncrease(Product product);

    void onDecrease(Product product);
}
