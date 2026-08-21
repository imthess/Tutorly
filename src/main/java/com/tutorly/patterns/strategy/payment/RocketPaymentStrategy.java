package com.tutorly.patterns.strategy.payment;

import java.math.BigDecimal;
import java.util.UUID;

public class RocketPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(
            BigDecimal amount,
            String accountNumber) {

        System.out.println("Processing Rocket payment...");
        System.out.println("Account: " + accountNumber);
        System.out.println("Amount: " + amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentResult(
                    false,
                    null,
                    "Invalid payment amount."
            );
        }

        if (accountNumber == null || accountNumber.isBlank()) {
            return new PaymentResult(
                    false,
                    null,
                    "Rocket account number is required."
            );
        }

        // Simulated Rocket transaction
        String transactionId =
                "ROCKET-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        return new PaymentResult(
                true,
                transactionId,
                "Rocket payment successful."
        );
    }

    @Override
    public PaymentResult refund(
            BigDecimal amount,
            String transactionId) {

        System.out.println("Processing Rocket refund...");

        return new PaymentResult(
                true,
                transactionId,
                "Rocket refund successful."
        );
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.ROCKET;
    }
}