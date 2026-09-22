package com.example.myapplication.features.order.domain.request;

import java.util.List;

public class CreateOrderRequest {

    private final String name;
    private final String email;
    private final String phone;
    private final String address;
    private final String payment;
    private final String deliveryDate;
    private final String deliveryTime;
    private final List<OrderItemRequest> items;

    public CreateOrderRequest(
            String name,
            String email,
            String phone,
            String address,
            String payment,
            String deliveryDate,
            String deliveryTime,
            List<OrderItemRequest> items
    ) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.payment = payment;
        this.deliveryDate = deliveryDate;
        this.deliveryTime = deliveryTime;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPayment() {
        return payment;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }
}
