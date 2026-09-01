package com.example.myapplication.features.order;

import androidx.lifecycle.ViewModel;

import com.example.myapplication.features.auth.repository.AuthRepository;
import com.example.myapplication.features.cart.model.CartItem;
import com.example.myapplication.features.cart.usecase.CartUseCase;
import com.example.myapplication.features.order.model.Order;
import com.example.myapplication.features.order.usecase.CreateOrderUseCase;
import com.example.myapplication.features.profiles.model.Profile;
import com.example.myapplication.features.profiles.usecase.UpdateProfileUseCase;
import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CheckoutViewModel extends ViewModel {

    private final CartUseCase cartUseCase;
    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final AuthRepository authRepository;

    @Inject
    public CheckoutViewModel(
            CartUseCase cartUseCase,
            CreateOrderUseCase createOrderUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            AuthRepository authRepository
    ) {
        this.cartUseCase = cartUseCase;
        this.createOrderUseCase = createOrderUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.authRepository = authRepository;
    }

    public List<CartItem> getItems() {
        return cartUseCase.getItems();
    }

    public double getSubtotal() {
        return cartUseCase.getTotal();
    }

    public void clearCart() {
        cartUseCase.clearCart();
    }

    public String getCurrentUserId() {
        return authRepository.getCurrentUserId();
    }

    public Task<Void> createOrder(Order order) {
        return createOrderUseCase.execute(order);
    }

    public Task<Void> updateProfile(Profile profile) {
        return updateProfileUseCase.execute(profile);
    }
}
