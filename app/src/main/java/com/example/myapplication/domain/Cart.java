package com.example.myapplication.domain;

import com.example.myapplication.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private static Cart instance;
    private List<Product> products;

    private Cart() {
        products = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {

        // Si por algún motivo llega null el id, asignamos uno seguro
        if (product.getId() == null) {
            product.setId(product.getNombre()); // fallback temporal
        }

        for (Product p : products) {
            if (p.getId().equals(product.getId())) {
                p.setCantidad(p.getCantidad() + 1);
                return;
            }
        }

        products.add(product);
    }

    public void updateProduct(Product product) {
        for (Product p : products) {
            if (p.getId().equals(product.getId())) {
                p.setCantidad(product.getCantidad());
                return;
            }
        }
    }

    public void removeProduct(Product product) {
        products.remove(product);
    }
}
