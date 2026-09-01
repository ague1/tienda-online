package com.example.myapplication.features.cart.repository;

import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.product.domain.model.Product;
import java.util.List;


public interface CartRepository {

    List<CartItem> getItems();

    void addProduct(Product product, double price);
    void removeProduct(String productId);

    void clearCart();

    double getTotal();

    void increase(String productId);

    void decrease(String productId);

    void setQuantity(
            String productId,
            int quantity
    );
}