package com.example.myapplication.features;

import com.example.myapplication.features.product.model.Product;

public interface OnCartClickListener {
    void onAddProduct(Product product);
    void onRemoveProduct(Product product);
}
