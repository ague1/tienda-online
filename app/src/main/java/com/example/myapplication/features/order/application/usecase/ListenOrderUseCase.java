package com.example.myapplication.features.order.application.usecase;

import com.example.myapplication.features.order.infrastructure.datasource.OrderListener;
import com.example.myapplication.features.order.infrastructure.datasource.OrderSubscription;
import com.example.myapplication.features.order.domain.model.Order;
import com.example.myapplication.features.order.domain.port.OrderRepository;

import javax.inject.Inject;

public class ListenOrderUseCase {

    private final OrderRepository orderRepository;

    @Inject
    public ListenOrderUseCase(
            OrderRepository orderRepository
    ) {
        this.orderRepository = orderRepository;
    }

    public OrderSubscription execute(
            String orderId,
            OrderListener<Order> listener
    ) {
        return orderRepository.listenOrder(
                orderId,
                listener
        );
    }
}
