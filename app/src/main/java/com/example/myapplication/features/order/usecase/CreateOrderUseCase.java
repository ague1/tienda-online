package com.example.myapplication.features.order.usecase;

import com.example.myapplication.features.order.model.Order;
import com.example.myapplication.features.order.repository.OrderRepository;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;


public class CreateOrderUseCase {
    private final OrderRepository orderRepository;
    @Inject
    public CreateOrderUseCase(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public Task<Void> execute(Order order){

        return orderRepository.saveOrder(order);

    }
}
