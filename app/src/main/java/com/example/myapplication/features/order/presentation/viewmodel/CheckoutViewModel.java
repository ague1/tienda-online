package com.example.myapplication.features.order.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.auth.domain.port.AuthRepository;
import com.example.myapplication.features.cart.domain.port.CartRepository;
import com.example.myapplication.features.cart.domain.model.CartItem;
import com.example.myapplication.features.order.domain.request.CreateOrderRequest;
import com.example.myapplication.features.order.application.usecase.CreateOrderUseCase;
import com.example.myapplication.features.profiles.domain.model.Profile;
import com.example.myapplication.features.profiles.application.usecase.UpdateProfileUseCase;
import com.google.android.gms.tasks.Tasks;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;



@HiltViewModel
public class CheckoutViewModel extends ViewModel {

    private final CartRepository cartRepository;
    private final CreateOrderUseCase createOrderUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final AuthRepository authRepository;

    private final MutableLiveData<UiState<Boolean>> orderState =
            new MutableLiveData<>(UiState.idle());

    @Inject
    public CheckoutViewModel(
            CartRepository cartRepository,
            CreateOrderUseCase createOrderUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            AuthRepository authRepository
    ) {
        this.cartRepository = cartRepository;
        this.createOrderUseCase = createOrderUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.authRepository = authRepository;
    }

    public LiveData<List<CartItem>> getItems() {
        return cartRepository.getItems();
    }

    public LiveData<Long> getSubtotal() {
        return cartRepository.getTotal();
    }

    public LiveData<UiState<Boolean>> getOrderState() {
        return orderState;
    }

    public void placeOrder(
            CreateOrderRequest request,
            Profile profile
    ) {
        if (request == null) {
            orderState.setValue(
                    UiState.error("ORDER_REQUEST_REQUIRED")
            );
            return;
        }

        if (profile == null) {
            orderState.setValue(
                    UiState.error("PROFILE_REQUIRED")
            );
            return;
        }

        String userId = authRepository.getCurrentUserId();

        if (userId == null || userId.trim().isEmpty()) {
            orderState.setValue(
                    UiState.error("AUTH_REQUIRED")
            );
            return;
        }

        profile.setUid(userId);

        orderState.setValue(UiState.loading());

        createOrderUseCase.execute(request, userId)
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();

                        return Tasks.forException(
                                exception != null
                                        ? exception
                                        : new IllegalStateException(
                                        "ORDER_CREATE_ERROR"
                                )
                        );
                    }

                    cartRepository.clearCart();

                    return updateProfileUseCase.execute(profile)
                            .continueWith(profileTask -> {

                                if (!profileTask.isSuccessful()) {
                                    return false;
                                }

                                return true;
                            });
                })
                .addOnSuccessListener(profileUpdated -> {

                    orderState.postValue(
                            UiState.success(profileUpdated)
                    );
                })
                .addOnFailureListener(exception -> {

                    orderState.postValue(
                            UiState.error("ORDER_CREATE_ERROR")
                    );
                });
    }
}

