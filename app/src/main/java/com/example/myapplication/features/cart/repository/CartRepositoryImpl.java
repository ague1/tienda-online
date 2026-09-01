package com.example.myapplication.features.cart.repository;

import com.example.myapplication.features.cart.model.Cart;
import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.product.domain.model.Product;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CartRepositoryImpl implements CartRepository {

    private final Cart cart;

    @Inject
    public CartRepositoryImpl(Cart cart) {
        this.cart = cart;
    }

    @Override
    public List<CartItem> getItems() {
        return cart.getItems();
    }

    @Override
    public void addProduct(Product product, double price) {
        cart.addProduct(product,price);
    }

    @Override
    public void removeProduct(String productId) {
        cart.removeProduct(productId);
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Override
    public double getTotal() {
        return cart.getTotal();
    }

    @Override
    public void increase(String productId) {
        cart.increase(productId);
    }

    @Override
    public void decrease(String productId) {
        cart.decrease(productId);
    }

    @Override
    public void setQuantity(
            String productId,
            int quantity
    ) {
        cart.setQuantity(
                productId,
                quantity
        );
    }
}
