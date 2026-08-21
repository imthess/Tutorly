package com.tutorly.patterns.strategy.payment;

import java.math.BigDecimal;

public interface PaymentStrategy {

    PaymentResult pay(
            BigDecimal amount,
            String accountNumber
    );

    PaymentResult refund(
            BigDecimal amount,
            String transactionId
    );

    PaymentMethod getPaymentMethod();
}