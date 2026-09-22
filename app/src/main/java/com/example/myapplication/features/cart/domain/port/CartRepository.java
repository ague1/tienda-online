package com.example.myapplication.features.cart.domain.port;

import androidx.lifecycle.LiveData;

import com.example.myapplication.features.cart.domain.model.CartItem;
import com.example.myapplication.features.product.domain.model.PricedProduct;

import java.util.List;


public interface CartRepository {

    LiveData<List<CartItem>> getItems();

    LiveData<Long> getTotal();

    void addProduct(PricedProduct pricedProduct);

    void removeProduct(String productId);

    void clearCart();

    void increase(String productId);

    void decrease(String productId);

    void setQuantity(String productId, int quantity);

    void start();

    void stop();

    void startSync();

    void stopSync();

    void updateProductPrice(
            String productId,
            long newPrice
    );
}

