package com.example.myapplication.features.cart.presentation.listener;

import com.example.myapplication.features.product.domain.model.PricedProduct;

public interface OnCartClickListener {

    void onAdd(PricedProduct pricedProduct);

    void onIncrease(PricedProduct pricedProduct);

    void onDecrease(PricedProduct pricedProduct);
}

