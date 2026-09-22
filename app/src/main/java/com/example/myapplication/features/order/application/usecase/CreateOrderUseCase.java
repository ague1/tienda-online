package com.example.myapplication.features.order.application.usecase;

import com.example.myapplication.features.order.domain.request.CreateOrderRequest;
import com.example.myapplication.features.order.domain.request.OrderItemRequest;
import com.example.myapplication.features.order.domain.model.Order;
import com.example.myapplication.features.order.domain.model.OrderItem;
import com.example.myapplication.features.order.domain.port.OrderRepository;
import com.example.myapplication.features.product.domain.port.repository.ProductRepository;
import com.example.myapplication.features.product.domain.port.repository.PromotionRepository;
import com.example.myapplication.features.product.domain.model.Product;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;


public class CreateOrderUseCase {

    private static final int MAX_QUANTITY_PER_ITEM = 100;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final FirebaseFirestore db;

    @Inject
    public CreateOrderUseCase(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            PromotionRepository promotionRepository,
            FirebaseFirestore db
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
        this.db = db;
    }

    public Task<Void> execute(
            CreateOrderRequest request,
            String userId
    ) {

        if (request == null) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "Request cannot be null"
                    )
            );
        }

        if (userId == null ||
                userId.trim().isEmpty()) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "User ID cannot be empty"
                    )
            );
        }

        List<OrderItemRequest> requestedItems =
                request.getItems();

        if (requestedItems == null ||
                requestedItems.isEmpty()) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "Order must contain items"
                    )
            );
        }

        List<String> productIds =
                new ArrayList<>();

        Set<String> uniqueProductIds =
                new HashSet<>();

        for (OrderItemRequest item :
                requestedItems) {

            if (item == null ||
                    item.getProductId() == null ||
                    item.getProductId().trim().isEmpty()) {

                return Tasks.forException(
                        new IllegalArgumentException(
                                "Invalid product ID"
                        )
                );
            }

            if (!uniqueProductIds.add(
                    item.getProductId()
            )) {

                return Tasks.forException(
                        new IllegalArgumentException(
                                "Duplicate product in order: "
                                        + item.getProductId()
                        )
                );
            }

            int quantity =
                    item.getQuantity();

            if (quantity <= 0) {

                return Tasks.forException(
                        new IllegalArgumentException(
                                "Invalid product quantity"
                        )
                );
            }

            if (quantity > MAX_QUANTITY_PER_ITEM) {

                return Tasks.forException(
                        new IllegalArgumentException(
                                "Quantity exceeds the maximum allowed"
                        )
                );
            }

            productIds.add(
                    item.getProductId()
            );
        }

        return promotionRepository
                .getActivePricesByProductIds(productIds)
                .continueWithTask(promotionTask -> {

                    if (!promotionTask.isSuccessful()) {

                        Exception exception =
                                promotionTask.getException();

                        if (exception != null) {
                            return Tasks.forException(
                                    exception
                            );
                        }

                        return Tasks.forException(
                                new IllegalStateException(
                                        "Could not load active promotions"
                                )
                        );
                    }

                    Map<String, Long> promotionPrices =
                            promotionTask.getResult();

                    if (promotionPrices == null) {
                        promotionPrices =
                                new HashMap<>();
                    }

                    final Map<String, Long>
                            finalPromotionPrices =
                            promotionPrices;

                    return db.runTransaction(
                            transaction -> {

                                Map<String, Product>
                                        productsById =
                                        new HashMap<>();

                                for (String productId :
                                        productIds) {

                                    Product product =
                                            productRepository
                                                    .getProductInTransaction(
                                                            transaction,
                                                            productId
                                                    );

                                    if (product == null) {

                                        throw new IllegalStateException(
                                                "Product not found: "
                                                        + productId
                                        );
                                    }

                                    productsById.put(
                                            productId,
                                            product
                                    );
                                }

                                List<OrderItem> orderItems =
                                        new ArrayList<>();

                                long subtotal = 0L;

                                Map<String, Integer> newStocks =
                                        new HashMap<>();

                                for (OrderItemRequest requestedItem :
                                        requestedItems) {

                                    String productId =
                                            requestedItem
                                                    .getProductId();

                                    Product product =
                                            productsById.get(
                                                    productId
                                            );

                                    int quantity =
                                            requestedItem
                                                    .getQuantity();

                                    if (quantity >
                                            product.getStock()) {

                                        throw new IllegalStateException(
                                                "Not enough stock for: "
                                                        + product
                                                        .getNombre()
                                        );
                                    }

                                    Long promotionPrice =
                                            finalPromotionPrices.get(productId);

                                    long price;

                                    if (promotionPrice != null) {
                                        price = promotionPrice;
                                    } else {
                                        price = product.getPrecio();
                                    }

                                    long itemSubtotal =
                                            price * quantity;

                                    subtotal += itemSubtotal;

                                    OrderItem orderItem =
                                            new OrderItem();

                                    orderItem.setId(
                                            product.getId()
                                    );

                                    orderItem.setNombre(
                                            product.getNombre()
                                    );

                                    orderItem.setCantidad(
                                            quantity
                                    );

                                    orderItem.setPrecio(
                                            price
                                    );

                                    orderItems.add(
                                            orderItem
                                    );

                                    int newStock =
                                            product.getStock()
                                                    - quantity;

                                    newStocks.put(
                                            productId,
                                            newStock
                                    );
                                }

                                long delivery = 299L;

                                long total =
                                        subtotal + delivery;

                                Order order =
                                        new Order();

                                order.setId(
                                        UUID.randomUUID()
                                                .toString()
                                );

                                order.setName(
                                        request.getName()
                                );

                                order.setEmail(
                                        request.getEmail()
                                );

                                order.setPhone(
                                        request.getPhone()
                                );

                                order.setAddress(
                                        request.getAddress()
                                );

                                order.setPayment(
                                        request.getPayment()
                                );
                                order.setDeliveryDate(
                                        request.getDeliveryDate()
                                );

                                order.setDeliveryTime(
                                        request.getDeliveryTime()
                                );


                                order.setUserId(
                                        userId
                                );

                                order.setItems(
                                        orderItems
                                );

                                order.setSubtotal(
                                        subtotal
                                );

                                order.setDelivery(
                                        delivery
                                );

                                order.setTotal(
                                        total
                                );

                                order.setStatus(
                                        "pending"
                                );

                                Date now = new Date();

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(now);
                                calendar.add(Calendar.HOUR_OF_DAY, 24);

                                order.setTimestamp(now);
                                order.setPaymentDeadline(
                                        calendar.getTime()
                                );


                                for (
                                        Map.Entry<String, Integer> entry :
                                        newStocks.entrySet()
                                ) {

                                    productRepository
                                            .updateStockInTransaction(
                                                    transaction,
                                                    entry.getKey(),
                                                    entry.getValue()
                                            );
                                }

                                orderRepository
                                        .saveOrderInTransaction(
                                                transaction,
                                                order
                                        );

                                return null;
                            }
                    );
                });
    }
}
