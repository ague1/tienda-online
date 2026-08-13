package com.example.myapplication.features.cart.repository;

import com.example.myapplication.features.cart.model.Cart;
import com.example.myapplication.features.product.model.Product;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class CartRepository {


    private final Cart cart;

    @Inject
    public CartRepository(Cart cart){
        this.cart = cart;
    }



    public List<Product> getProducts(){
        return cart.getProducts();
    }


    public void addProduct(Product product){
        cart.addProduct(product);
    }


    public void updateProduct(Product product){
        cart.updateProduct(product);
    }


    public void removeProduct(Product product){
        cart.removeProduct(product);
    }


    public void clearCart(){
        cart.clear();
    }


    public double getTotal(){
        return cart.getTotal();
    }


    public void increase(Product product){
        cart.increase(product);
    }


    public void decrease(Product product){
        cart.decrease(product);
    }

}
