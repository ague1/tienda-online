package com.example.myapplication.features.order.domain.port;

import com.example.myapplication.features.order.infrastructure.datasource.OrderListener;
import com.example.myapplication.features.order.infrastructure.datasource.OrderSubscription;
import com.example.myapplication.features.order.domain.model.OrderTimeline;
import com.example.myapplication.features.order.infrastructure.datasource.OrderDataSource;
import com.example.myapplication.features.order.domain.model.Order;
import com.example.myapplication.features.order.domain.model.OrderItem;
import com.example.myapplication.features.product.infrastructure.datasource.ProductDataSource;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class OrderRepository {
    private OrderDataSource dataSource;
    private final ProductDataSource productDataSource;
    private final FirebaseFirestore db;

    @Inject
    public OrderRepository(OrderDataSource dataSource, ProductDataSource productDataSource, FirebaseFirestore db){

        this.dataSource = dataSource;
        this.productDataSource = productDataSource;
        this.db = db;
    }

    public OrderSubscription listenOrder(
            String orderId,
            OrderListener<Order> listener
    ) {

        ListenerRegistration registration =
                dataSource.listenOrder(
                        orderId,
                        (snapshot, error) -> {

                            if (error != null) {
                                listener.onError(error);
                                return;
                            }

                            if (snapshot == null) {
                                listener.onError(
                                        new IllegalStateException(
                                                "ORDER_SNAPSHOT_NULL"
                                        )
                                );
                                return;
                            }

                            if (!snapshot.exists()) {
                                listener.onError(
                                        new IllegalStateException(
                                                "ORDER_NOT_FOUND"
                                        )
                                );
                                return;
                            }

                            listener.onSuccess(
                                    map(snapshot)
                            );
                        }
                );

        return registration::remove;
    }


    public OrderSubscription listenOrdersByStatus(
            String userId,
            String status,
            OrderListener<List<Order>> listener
    ) {

        ListenerRegistration registration =
                dataSource.listenOrdersByStatus(
                        userId,
                        status,
                        (snapshot, error) -> {

                            if (error != null) {
                                listener.onError(error);
                                return;
                            }

                            if (snapshot == null) {
                                listener.onError(
                                        new IllegalStateException(
                                                "ORDERS_SNAPSHOT_NULL"
                                        )
                                );
                                return;
                            }

                            listener.onSuccess(
                                    mapList(
                                            snapshot.getDocuments()
                                    )
                            );
                        }
                );

        return registration::remove;
    }



    // Convertir un documento Firebase a objeto Order
    public Order map(DocumentSnapshot doc) {

        if (doc == null || !doc.exists()) {
            return null;
        }

        Order order = new Order();

        // ID
        order.setId(doc.getId());

        // Datos del cliente
        order.setName(
                doc.getString("name")
        );

        order.setEmail(
                doc.getString("email")
        );

        order.setPhone(
                doc.getString("phone")
        );

        order.setAddress(
                doc.getString("address")
        );

        // Pago
        order.setPayment(
                doc.getString("payment")
        );

        // Entrega
        order.setDeliveryDate(
                doc.getString("deliveryDate")
        );

        order.setDeliveryTime(
                doc.getString("deliveryTime")
        );

        Long delivery =
                doc.getLong("delivery");

        order.setDelivery(
                delivery != null
                        ? delivery
                        : 0L
        );

        // Totales
        Long subtotal =
                doc.getLong("subtotal");

        order.setSubtotal(
                subtotal != null
                        ? subtotal
                        : 0L
        );

        Long total =
                doc.getLong("total");

        order.setTotal(
                total != null
                        ? total
                        : 0L
        );

        // Estado
        order.setStatus(
                doc.getString("status")
        );

        // Usuario
        order.setUserId(
                doc.getString("userId")
        );

        // Timestamp
        order.setTimestamp(
                doc.getDate("timestamp")
        );

        // Items
        Object items =
                doc.get("items");

        List<OrderItem> orderItems =
                new ArrayList<>();

        if (items instanceof List) {

            for (Object obj :
                    (List<?>) items) {

                if (!(obj instanceof Map)) {
                    continue;
                }

                Map<String, Object> map =
                        (Map<String, Object>) obj;

                OrderItem item =
                        new OrderItem();

                item.setId(
                        (String) map.get("id")
                );

                item.setNombre(
                        (String) map.get("nombre")
                );

                Number cantidad =
                        (Number) map.get("cantidad");

                item.setCantidad(
                        cantidad != null
                                ? cantidad.intValue()
                                : 0
                );

                Number precio =
                        (Number) map.get("precio");

                item.setPrecio(
                        precio != null
                                ? precio.longValue()
                                : 0L
                );

                orderItems.add(item);
            }
        }

        order.setItems(
                orderItems
        );

        Object timelineObject =
                doc.get("timeline");

        if (timelineObject instanceof Map) {

            Map<?, ?> timelineMap =
                    (Map<?, ?>) timelineObject;

            OrderTimeline timeline = new OrderTimeline();

            timeline.setPlaced(toDate(
                    timelineMap.get("placed"))
            );

            timeline.setPending(toDate(
                    timelineMap.get("pending"))
            );

            timeline.setConfirmed(toDate(
                    timelineMap.get("confirmed"))
            );

            timeline.setProcessing(
                    toDate(
                            timelineMap.get("processing")
                    )
            );

            timeline.setDelivered(
                    toDate(
                            timelineMap.get("delivered")
                    )
            );

            order.setTimeline(timeline);
        }


        return order;
    }



    // Convertir una lista de documentos Firebase a lista de Order
    public List<Order> mapList(
            List<DocumentSnapshot> documents
    ) {
        List<Order> orders = new ArrayList<>();
        if (documents == null) {return orders;}

        for (DocumentSnapshot doc : documents) {

            Order order = map(doc);

            if (order != null) {
                orders.add(order);
            }
        }

        return orders;
    }
    public void saveOrderInTransaction(
            Transaction transaction,
            Order order
    ) {

        Map<String, Object> data = new HashMap<>();

        data.put("name", order.getName());
        data.put("email", order.getEmail());
        data.put("phone", order.getPhone());
        data.put("address", order.getAddress());
        data.put("payment", order.getPayment());
        data.put("deliveryDate", order.getDeliveryDate());
        data.put("deliveryTime", order.getDeliveryTime());

        data.put("subtotal", order.getSubtotal());
        data.put("delivery", order.getDelivery());
        data.put("total", order.getTotal());

        data.put("status", order.getStatus());
        data.put("timestamp", order.getTimestamp());

        data.put("userId", order.getUserId());

        List<Map<String, Object>> firebaseItems =
                new ArrayList<>();

        for (OrderItem item : order.getItems()) {

            Map<String, Object> map =
                    new HashMap<>();

            map.put("id", item.getId());
            map.put("nombre", item.getNombre());
            map.put("cantidad", item.getCantidad());
            map.put("precio", item.getPrecio());

            firebaseItems.add(map);
        }

        data.put("items", firebaseItems);

        Map<String, Object> timeline =
                new HashMap<>();

        timeline.put("placed", new Date());
        timeline.put("pending", new Date());
        timeline.put("confirmed", null);
        timeline.put("processing", null);
        timeline.put("delivered", null);

        data.put("timeline", timeline);

        dataSource.saveOrderInTransaction(
                transaction,
                order.getId(),
                data
        );
    }

    public Task<Void> expireOrder(
            String orderId
    ) {

        if (orderId == null ||
                orderId.trim().isEmpty()) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "Order ID cannot be empty"
                    )
            );
        }

        return db.runTransaction(transaction -> {
            DocumentSnapshot orderDocument =
                    dataSource.getOrderInTransaction(
                            transaction,
                            orderId
                    );

            if (orderDocument == null ||
                    !orderDocument.exists()) {

                throw new IllegalStateException(
                        "Order not found: " + orderId);
            }

            String status = orderDocument.getString("status");

                    /*
                     * Importantísimo:
                     *
                     * Si el pedido ya fue pagado,
                     * confirmado o expirado, no hacemos nada.
                     */
            if (!"pending".equals(status)) {
                return null;
            }

            Date paymentDeadline = orderDocument.getDate(
                    "paymentDeadline"
            );

            if (paymentDeadline == null) {

                throw new IllegalStateException(
                        "Order has no payment deadline: "
                                + orderId
                );
            }

            Date now = new Date();

            if (paymentDeadline.after(now)) {

                throw new IllegalStateException(
                        "Payment deadline has not expired"
                );
            }


            Object itemsObject = orderDocument.get("items");
            if (!(itemsObject instanceof List)) {

                throw new IllegalStateException(
                        "Order has no items: " + orderId);
            }

            List<?> items = (List<?>) itemsObject;

            for (Object itemObject : items) {

                if (!(itemObject instanceof Map)) {
                    continue;
                }

                Map<?, ?> itemMap = (Map<?, ?>) itemObject;

                Object productIdObject = itemMap.get("id");

                Object quantityObject = itemMap.get("cantidad");

                if (!(productIdObject instanceof String)) {
                    continue;
                }

                if (!(quantityObject instanceof Number)) {
                    continue;
                }

                String productId = (String) productIdObject;

                int quantity = ((Number) quantityObject)
                        .intValue();

                if (productId.trim().isEmpty() || quantity <= 0) {
                    continue;
                }

                DocumentSnapshot productDocument = productDataSource
                        .getProductInTransaction(
                                transaction,
                                productId
                        );

                if (productDocument == null ||
                        !productDocument.exists()) {

                    throw new IllegalStateException(
                            "Product not found: "
                                    + productId
                    );
                }

                Long currentStock = productDocument.getLong(
                        "stock"
                );

                int stock = currentStock != null
                        ? currentStock.intValue()
                        : 0;

                int newStock = stock + quantity;

                productDataSource.updateStockInTransaction(
                        transaction,
                        productId,
                        newStock
                );
            }

            dataSource.updateOrderStatusInTransaction(
                    transaction,
                    orderId,
                    "expired"
            );

            return null;
        });
    }
    public Task<Void> confirmOrderPayment(
            String orderId
    ) {

        if (orderId == null ||
                orderId.trim().isEmpty()) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "Order ID cannot be empty"
                    )
            );
        }

        return db.runTransaction(transaction -> {

            DocumentSnapshot orderDocument =
                    dataSource.getOrderInTransaction(
                            transaction,
                            orderId
                    );

            if (orderDocument == null ||
                    !orderDocument.exists()) {

                throw new IllegalStateException(
                        "Order not found: " + orderId
                );
            }

            String status =
                    orderDocument.getString("status");

            /*
             * El pago solamente puede confirmar
             * un pedido que todavía está pendiente.
             */
            if (!"pending".equals(status)) {

                throw new IllegalStateException(
                        "Order cannot be confirmed. Current status: "
                                + status
                );
            }

            Date paymentDeadline =
                    orderDocument.getDate(
                            "paymentDeadline"
                    );

            if (paymentDeadline == null) {

                throw new IllegalStateException(
                        "Order has no payment deadline: "
                                + orderId
                );
            }

            Date now = new Date();

            /*
             * Si las 24 horas ya pasaron,
             * el pago ya no puede confirmar este pedido.
             */
            if (!paymentDeadline.after(now)) {

                throw new IllegalStateException(
                        "Payment deadline has expired"
                );
            }

            /*
             * pending → confirmed
             */
            dataSource.confirmOrderPaymentInTransaction(
                    transaction,
                    orderId
            );

            return null;
        });
    }

    private Date toDate(Object value) {

        if (value instanceof Date) {
            return (Date) value;
        }

        if (value instanceof Timestamp) {
            return ((Timestamp) value).toDate();
        }

        return null;
    }

}

