package com.example.myapplication.features.order.domain.model;

public class PaymentResult {

    private final boolean successful;
    private final String transactionId;

    public PaymentResult(
            boolean successful,
            String transactionId
    ) {
        this.successful = successful;
        this.transactionId = transactionId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getTransactionId() {
        return transactionId;
    }
}

