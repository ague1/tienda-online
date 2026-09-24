package com.example.myapplication.features.order.application.usecase;

import com.example.myapplication.features.order.domain.port.PaymentRepository;
import com.example.myapplication.features.order.domain.model.PaymentResult;
import com.example.myapplication.features.order.domain.port.OrderRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import javax.inject.Inject;

public class ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Inject
    public ProcessPaymentUseCase(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public Task<Void> execute(
            String orderId,
            long amount
    ) {

        if (orderId == null ||
                orderId.trim().isEmpty()) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "Order ID cannot be empty"
                    )
            );
        }

        if (amount <= 0) {

            return Tasks.forException(
                    new IllegalArgumentException(
                            "Invalid payment amount"
                    )
            );
        }

        return paymentRepository
                .processPayment(
                        orderId,
                        amount
                )
                .continueWithTask(task -> {

                    if (!task.isSuccessful()) {

                        Exception exception =
                                task.getException();

                        return Tasks.forException(
                                exception != null
                                        ? exception
                                        : new IllegalStateException(
                                        "Payment failed"
                                )
                        );
                    }

                    PaymentResult result =
                            task.getResult();

                    if (result == null ||
                            !result.isSuccessful()) {

                        return Tasks.forException(
                                new IllegalStateException(
                                        "Payment was not successful"
                                )
                        );
                    }

                    return orderRepository
                            .confirmOrderPayment(
                                    orderId
                            );
                });
    }
}

