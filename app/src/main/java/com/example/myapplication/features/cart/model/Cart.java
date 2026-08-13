package com.example.myapplication.features.cart.model;

import com.example.myapplication.features.product.model.Product;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class Cart {
    private final List<Product> products;
    @Inject
    public Cart() {
        products = new ArrayList<>();
    }


    public List<Product> getProducts(){

        return new ArrayList<>(products);
    }

    public void addProduct(Product product) {

        // Si por algún motivo llega null el id, asignamos uno seguro
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

    public void clear() {
        products.clear();
    }

    public double getTotal() {

        double total = 0;

        for (Product product : products) {
            total += product.getPrecio() * product.getCantidad();
        }

        return total;
    }

    public void increase(Product product) {


        product.setCantidad(
                product.getCantidad() + 1
        );
    }

    public void decrease(Product product) {
        if(product.getCantidad() > 1){

            product.setCantidad(
                    product.getCantidad() - 1
            );

        }else{

            products.remove(product);

        }
    }
}
