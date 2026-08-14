package com.example.myapplication.features.order.usecase;

import com.example.myapplication.features.order.repository.OrderRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;

import javax.inject.Inject;

public class ListenOrderUseCase {

    private final OrderRepository orderRepository;
    @Inject
    public ListenOrderUseCase(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public ListenerRegistration execute(String orderId,
                                        EventListener<DocumentSnapshot> listener){
        return orderRepository.listenOrder(orderId, listener);

    }
}
