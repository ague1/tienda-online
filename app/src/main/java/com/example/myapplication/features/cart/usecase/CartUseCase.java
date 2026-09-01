package com.example.myapplication.features.cart.usecase;

import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.cart.repository.CartRepository;
import com.example.myapplication.features.product.domain.model.Product;

import java.util.List;

import javax.inject.Inject;

public class CartUseCase {

    private final CartRepository repository;

    @Inject
    public CartUseCase(CartRepository repository) {
        this.repository = repository;
    }

    public List<CartItem> getItems() {
        return repository.getItems();
    }

    public void increase(String productId) {
        repository.increase(productId);
    }

    public void decrease(String productId) {
        repository.decrease(productId);
    }

    public double getTotal() {
        return repository.getTotal();
    }

    public void addProduct(
            Product product,
            double price
    ) {

        repository.addProduct(
                product,
                price
        );
    }

    public void removeProduct(String productId) {
        repository.removeProduct(productId);
    }

    public void clearCart() {
        repository.clearCart();
    }
    public void setQuantity(
            String productId,
            int quantity
    ) {
        repository.setQuantity(
                productId,
                quantity
        );
    }
}
