package com.example.myapplication.features.cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.cart.usecase.CartUseCase;
import com.example.myapplication.features.product.domain.model.Product;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CartViewModel extends ViewModel {

    private final CartUseCase cartUseCase;
    private final MutableLiveData<List<CartItem>> items =
            new MutableLiveData<>();

    private final MutableLiveData<Double> total =
            new MutableLiveData<>();

    @Inject
    public CartViewModel(
            CartUseCase cartUseCase
    ) {
        this.cartUseCase = cartUseCase;
        refreshCart();
    }

    public LiveData<List<CartItem>> getItems() {
        return items;
    }
    public LiveData<Double> getTotal() {
        return total;
    }

    public void addProduct(
            Product product,
            double price
    ) {

        cartUseCase.addProduct(
                product,
                price
        );

        refreshCart();
    }

    public void increase(String productId) {
        cartUseCase.increase(productId);
        refreshCart();
    }

    public void decrease(String productId) {
        cartUseCase.decrease(productId);
        refreshCart();
    }


    public void removeProduct(String productId) {

        cartUseCase.removeProduct(productId);

        refreshCart();
    }
    public void clearCart() {

        cartUseCase.clearCart();

        refreshCart();
    }

    public void setQuantity(
            String productId,
            int quantity
    ) {

        cartUseCase.setQuantity(
                productId,
                quantity
        );

        refreshCart();
    }



    private void refreshCart() {
        items.setValue(new ArrayList<>(cartUseCase.getItems())
        );

        total.setValue(cartUseCase.getTotal());
    }
}
