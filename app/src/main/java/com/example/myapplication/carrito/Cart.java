package com.example.myapplication.carrito;

import com.example.myapplication.home.ProductsList;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private static Cart instance;
    private List<ProductsList> products;

    private Cart() {
        products = new ArrayList<>();
    }

    public static Cart getInstance() {
        if (instance == null) {
            instance = new Cart();
        }
        return instance;
    }

    public List<ProductsList> getProducts() {
        return products;
    }

    public void addProduct(ProductsList product) {

        // Si por algún motivo llega null el id, asignamos uno seguro
        if (product.getId() == null) {
            product.setId(product.getNombre()); // fallback temporal
        }

        for (ProductsList p : products) {
            if (p.getId().equals(product.getId())) {
                p.setCantidad(p.getCantidad() + 1);
                return;
            }
        }

        products.add(product);
    }

    public void updateProduct(ProductsList product) {
        for (ProductsList p : products) {
            if (p.getId().equals(product.getId())) {
                p.setCantidad(product.getCantidad());
                return;
            }
        }
    }

    public void removeProduct(ProductsList product) {
        products.remove(product);
    }
}
