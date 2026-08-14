package com.example.myapplication.features.order.model;


import java.util.Date;
import java.util.List;

public class Order {

    private String id;
    private String name;
    private String email;
    private String phone;

    private String address;

    private String payment;
    private double delivery;
    private double subtotal;
    private double total;
    private String status;
    private Date timestamp;

    private List<OrderItem> items;
    //private Map<String, Object> address;

    public Order() {}

    public String getId() { return id; }
    public String getName() { return name; }
    public double getSubtotal() { return subtotal; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    private String userId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTimestamp() { return timestamp; }
    public List<OrderItem> getItems() { return items; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public double getDelivery() {
        return delivery;
    }

    public void setDelivery(double delivery) {
        this.delivery = delivery;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;

    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}