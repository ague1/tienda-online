package com.example.myapplication.features.order.domain.port;

import com.example.myapplication.features.order.domain.model.PaymentResult;
import com.google.android.gms.tasks.Task;

public interface PaymentRepository {

    Task<PaymentResult> processPayment(
            String orderId,
            long amount
    );
}

