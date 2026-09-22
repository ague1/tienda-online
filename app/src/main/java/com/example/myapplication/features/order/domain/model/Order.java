package com.example.myapplication.features.order.domain.model;


import java.util.Date;
import java.util.List;

public class Order {

    private String id;
    private String name;
    private String email;
    private String phone;

    private String address;

    private String payment;
    private long delivery;
    private long subtotal;
    private long total;
    private String status;
    private Date timestamp;
    private String deliveryDate;
    private String deliveryTime;

    private Date paymentDeadline;
    private OrderTimeline timeline;
    private List<OrderItem> items;
    //private Map<String, Object> address;

    public Order() {}

    public String getId() { return id; }
    public String getName() { return name; }
    public long getSubtotal() { return subtotal; }
    public long getTotal() { return total; }
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

    public long getDelivery() {
        return delivery;
    }

    public void setDelivery(long delivery) {
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

    public void setSubtotal(long subtotal) {
        this.subtotal = subtotal;

    }

    public void setTotal(long total) {
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

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(Date paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }
    public OrderTimeline getTimeline() {
        return timeline;
    }

    public void setTimeline(OrderTimeline timeline) {
        this.timeline = timeline;
    }

}