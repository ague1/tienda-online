package com.example.myapplication.features.order.repository;

import com.example.myapplication.features.order.firebase.OrderDataSource;
import com.example.myapplication.features.order.model.Order;
import com.example.myapplication.features.order.model.OrderItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class OrderRepository {
    private OrderDataSource dataSource;
    @Inject
    public OrderRepository(OrderDataSource dataSource){

        this.dataSource = dataSource;
    }

    public ListenerRegistration listenOrder(
            String orderId,
            EventListener<DocumentSnapshot> listener
    ){
        return dataSource.listenOrder(orderId,listener);

    }

    public ListenerRegistration listenOrdersByStatus(
            String userId,
            String status,
            EventListener<QuerySnapshot> listener
    ) {

        return dataSource.listenOrdersByStatus(userId,status,listener);
    }


    // Convertir un documento Firebase a objeto Order
    public Order map(DocumentSnapshot doc) {

        Order order = new Order();

        order.setId(doc.getId());

        order.setName(doc.getString("name"));
        Double subtotal = doc.getDouble("subtotal");
        order.setSubtotal(subtotal != null ? subtotal : 0);
        Double total = doc.getDouble("total");
        order.setTotal(total != null ? total : 0);

        order.setStatus(doc.getString("status"));
        order.setTimestamp(doc.getDate("timestamp"));
        Object items = doc.get("items");

        if(items instanceof List){
            List<OrderItem> orderItems = new ArrayList<>();

            for (Object obj : (List<?>) items) {

                if (obj instanceof Map) {

                    Map<String, Object> map = (Map<String, Object>) obj;

                    OrderItem item = new OrderItem();

                    item.setId((String) map.get("id"));
                    item.setNombre((String) map.get("nombre"));

                    Number cantidad = (Number) map.get("cantidad");
                    item.setCantidad(cantidad.intValue());

                    Number precio = (Number) map.get("precio");
                    item.setPrecio(precio.doubleValue());

                    orderItems.add(item);
                }
            }

            order.setItems(orderItems);
        }

        order.setAddress(doc.getString("address"));


        return order;
    }


    // Convertir una lista de documentos Firebase a lista de Order
    public List<Order> mapList(List<DocumentSnapshot> documents) {

        List<Order> orders = new ArrayList<>();

        for (DocumentSnapshot doc : documents) {
            orders.add(map(doc));
        }

        return orders;
    }
    public Task<Void> saveOrder(Order order) {

        Map<String, Object> data = new HashMap<>();

        data.put("name", order.getName());
        data.put("email", order.getEmail());
        data.put("phone", order.getPhone());
        data.put("address", order.getAddress());
        data.put("payment", order.getPayment());

        data.put("subtotal", order.getSubtotal());
        data.put("delivery", order.getDelivery());
        data.put("total", order.getTotal());

        data.put("status", order.getStatus());
        data.put("timestamp", order.getTimestamp());

        data.put("userId", order.getUserId());

        List<Map<String,Object>> firebaseItems = new ArrayList<>();

        for(OrderItem item : order.getItems()){

            Map<String,Object> map = new HashMap<>();

            map.put("id", item.getId());
            map.put("nombre", item.getNombre());
            map.put("cantidad", item.getCantidad());
            map.put("precio", item.getPrecio());

            firebaseItems.add(map);
        }

        data.put("items", firebaseItems);

        Map<String,Object> timeline = new HashMap<>();

        timeline.put("placed", new Date());
        timeline.put("pending", new Date());
        timeline.put("confirmed", null);
        timeline.put("processing", null);
        timeline.put("delivered", null);

        data.put("timeline", timeline);


        return dataSource.saveOrder(order.getId(),data);
    }
}

