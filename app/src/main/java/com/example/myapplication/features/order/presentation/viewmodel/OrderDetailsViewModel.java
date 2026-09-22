package com.example.myapplication.features.order.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.auth.domain.port.AuthRepository;
import com.example.myapplication.features.order.infrastructure.datasource.OrderListener;
import com.example.myapplication.features.order.infrastructure.datasource.OrderSubscription;
import com.example.myapplication.features.order.domain.model.Order;
import com.example.myapplication.features.order.domain.port.OrderRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderDetailsViewModel extends ViewModel {

    private final OrderRepository orderRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<UiState<List<Order>>> state =
            new MutableLiveData<>(UiState.idle());

    private OrderSubscription orderSubscription;

    @Inject
    public OrderDetailsViewModel(
            OrderRepository orderRepository,
            AuthRepository authRepository
    ) {
        this.orderRepository = orderRepository;
        this.authRepository = authRepository;
    }

    public LiveData<UiState<List<Order>>> getState() {
        return state;
    }

    public void loadOrders(String status) {

        if (orderSubscription != null) {
            orderSubscription.close();
            orderSubscription = null;
        }

        String userId =
                authRepository.getCurrentUserId();

        if (userId == null || userId.trim().isEmpty()) {
            state.setValue(
                    UiState.error("AUTH_REQUIRED")
            );
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            state.setValue(
                    UiState.error("ORDER_STATUS_REQUIRED")
            );
            return;
        }

        state.setValue(
                UiState.loading()
        );

        orderSubscription =
                orderRepository.listenOrdersByStatus(
                        userId,
                        status,
                        new OrderListener<List<Order>>() {

                            @Override
                            public void onSuccess(
                                    List<Order> orders
                            ) {
                                state.postValue(
                                        UiState.success(orders)
                                );
                            }

                            @Override
                            public void onError(
                                    Exception exception
                            ) {
                                state.postValue(
                                        UiState.error(
                                                "ORDERS_LOAD_ERROR"
                                        )
                                );
                            }
                        }
                );
    }

    @Override
    protected void onCleared() {

        if (orderSubscription != null) {
            orderSubscription.close();
            orderSubscription = null;
        }

        super.onCleared();
    }
}