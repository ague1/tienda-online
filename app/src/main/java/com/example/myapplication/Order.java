package com.example.myapplication;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Order {

    private String id;
    private String name;
    private double subtotal;
    private double total;
    private String status;
    private Date timestamp;
    private List<Map<String, Object>> items;
    private Map<String, Object> address;

    public Order() {}

    public static Order fromSnapshot(DocumentSnapshot doc) {
        Order order = new Order();

        order.id = doc.getId();
        order.name = doc.getString("name");

        Double sb = doc.getDouble("subtotal");
        order.subtotal = sb != null ? sb : 0;

        Double tt = doc.getDouble("total");
        order.total = tt != null ? tt : 0;

        order.status = doc.getString("status");
        order.timestamp = doc.getDate("timestamp");

        order.items = (List<Map<String, Object>>) doc.get("items");
        order.address = (Map<String, Object>) doc.get("address");

        return order;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getSubtotal() { return subtotal; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
    public Date getTimestamp() { return timestamp; }
    public List<Map<String, Object>> getItems() { return items; }
    public Map<String, Object> getAddress() { return address; }
}