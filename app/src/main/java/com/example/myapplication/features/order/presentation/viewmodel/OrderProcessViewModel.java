package com.example.myapplication.features.order.presentation.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.core.ui.UiState;
import com.example.myapplication.features.order.infrastructure.datasource.OrderListener;
import com.example.myapplication.features.order.infrastructure.datasource.OrderSubscription;
import com.example.myapplication.features.order.domain.model.Order;
import com.example.myapplication.features.order.application.usecase.ListenOrderUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class OrderProcessViewModel extends ViewModel {

    private final ListenOrderUseCase listenOrderUseCase;

    private final MutableLiveData<UiState<Order>> state =
            new MutableLiveData<>(UiState.idle());

    private OrderSubscription orderSubscription;
    private String currentOrderId;

    @Inject
    public OrderProcessViewModel(
            ListenOrderUseCase listenOrderUseCase
    ) {
        this.listenOrderUseCase = listenOrderUseCase;
    }

    public LiveData<UiState<Order>> getState() {
        return state;
    }

    public void listenOrder(String orderId) {

        if (orderId == null ||
                orderId.trim().isEmpty()) {

            state.setValue(
                    UiState.error("ORDER_ID_REQUIRED")
            );

            return;
        }

        if (orderId.equals(currentOrderId) &&
                orderSubscription != null) {
            return;
        }

        removeOrderListener();

        currentOrderId = orderId;

        state.setValue(
                UiState.loading()
        );

        orderSubscription =
                listenOrderUseCase.execute(
                        orderId,
                        new OrderListener<Order>() {

                            @Override
                            public void onSuccess(Order order) {

                                state.postValue(
                                        UiState.success(order)
                                );
                            }

                            @Override
                            public void onError(
                                    Exception exception
                            ) {

                                state.postValue(
                                        UiState.error(
                                                "ORDER_LOAD_ERROR"
                                        )
                                );
                            }
                        }
                );
    }

    private void removeOrderListener() {

        if (orderSubscription != null) {
            orderSubscription.close();
            orderSubscription = null;
        }
    }

    @Override
    protected void onCleared() {

        removeOrderListener();

        super.onCleared();
    }
}


