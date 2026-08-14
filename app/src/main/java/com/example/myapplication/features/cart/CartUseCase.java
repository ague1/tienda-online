package com.example.myapplication.features.cart;

import com.example.myapplication.features.cart.repository.CartRepository;
import com.example.myapplication.features.product.model.Product;

import java.util.List;

import javax.inject.Inject;

public class CartUseCase {

    private final CartRepository repository;

    @Inject
    public CartUseCase(CartRepository repository) {
        this.repository = repository;
    }

    public void addProduct(Product product) {
        repository.addProduct(product);
    }

    public void removeProduct(Product product) {
        repository.removeProduct(product);
    }

    public void increase(Product product) {
        repository.increase(product);
    }

    public void decrease(Product product) {
        repository.decrease(product);
    }

    public List<Product> getProducts() {
        return repository.getProducts();
    }

    public double getTotal() {
        return repository.getTotal();
    }

    public void clearCart() {
        repository.clearCart();
    }
}
