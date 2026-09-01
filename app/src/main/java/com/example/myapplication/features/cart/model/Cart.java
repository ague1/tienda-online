package com.example.myapplication.features.cart.model;

import com.example.myapplication.features.product.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class Cart {
    private  final List<CartItem> items;
    @Inject
    public Cart() {
        items = new ArrayList<>();
    }


    public List<CartItem> getItems(){

        return new ArrayList<>(items);
    }


    public void addProduct(
            Product product,
            double price
    ) {

        if (product == null || product.getId() == null) {
            return;
        }

        for (CartItem item : items) {

            if (product.getId().equals(item.getProductId())) {

                item.setQuantity(
                        item.getQuantity() + 1
                );

                return;
            }
        }

        CartItem item = new CartItem(
                product.getId(),
                product.getNombre(),
                product.getImage(),
                price,
                1
        );

        items.add(item);
    }

    public void setQuantity(String productId, int quantity) {

        if (productId == null) {
            return;
        }

        for (CartItem item : items) {

            if (productId.equals(item.getProductId())) {

                if (quantity <= 0) {
                    items.remove(item);
                } else {
                    item.setQuantity(quantity);
                }

                return;
            }
        }
    }


   /* public void updateProduct(Product product) {
        for (Product p : products) {
            if (p.getId().equals(product.getId())) {
                p.setCantidad(product.getCantidad());
                return;
            }
        }
    }*/

    public void increase(String productId) {
        if (productId == null) {return;}

        for (CartItem item : items) {

            if (productId.equals(item.getProductId())) {

                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
    }

    public void decrease(String productId) {

        if (productId == null) {return;}

        for (CartItem item : items) {

            if (productId.equals(item.getProductId())) {

                if (item.getQuantity() <= 1) {items.remove(item);
                } else {
                    item.setQuantity(item.getQuantity() - 1);
                }

                return;
            }
        }
    }
    public void removeProduct(String productId) {
        if (productId == null) {return;}

        items.removeIf(item -> productId.equals(item.getProductId()));
    }

    public void clear() {
        items.clear();
    }

    public double getTotal() {
        double total = 0;

        for (CartItem item : items) {

            total +=
                    item.getPrecio()
                            * item.getQuantity();
        }

        return total;
    }
}
