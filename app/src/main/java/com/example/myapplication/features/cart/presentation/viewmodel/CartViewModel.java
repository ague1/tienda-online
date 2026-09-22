package com.example.myapplication.features.cart.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.features.cart.domain.port.CartRepository;
import com.example.myapplication.features.cart.domain.model.CartItem;
import com.example.myapplication.features.product.domain.model.PricedProduct;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CartViewModel extends ViewModel {

    private final CartRepository repository;

    @Inject
    public CartViewModel(
            CartRepository repository
    ) {
        this.repository = repository;
    }

    public LiveData<List<CartItem>> getItems() {
        return repository.getItems();
    }

    public LiveData<Long> getTotal() {
        return repository.getTotal();
    }

    public void addProduct(
            PricedProduct pricedProduct
    ) {
        repository.addProduct(
                pricedProduct
        );
    }


    public void increase(String productId) {
        repository.increase(productId);
    }

    public void decrease(String productId) {
        repository.decrease(productId);
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

    public void updateProductPrice(
            String productId,
            long newPrice
    ) {
        repository.updateProductPrice(
                productId,
                newPrice
        );
    }


}
