package com.tutorly.patterns.strategy.payment;

import java.math.BigDecimal;

public class PaymentContext {

    private PaymentStrategy strategy;

    public PaymentContext() {
    }

    public PaymentContext(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PaymentStrategy strategy) {

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Payment strategy cannot be null."
            );
        }

        this.strategy = strategy;
    }

    public PaymentStrategy getStrategy() {
        return strategy;
    }

    public PaymentResult pay(
            BigDecimal amount,
            String accountNumber) {

        if (strategy == null) {
            throw new IllegalStateException(
                    "Payment strategy has not been selected."
            );
        }

        return strategy.pay(amount, accountNumber);
    }

    public PaymentResult refund(
            BigDecimal amount,
            String transactionId) {

        if (strategy == null) {
            throw new IllegalStateException(
                    "Payment strategy has not been selected."
            );
        }

        return strategy.refund(amount, transactionId);
    }
}