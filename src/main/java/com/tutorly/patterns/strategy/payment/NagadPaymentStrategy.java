package com.tutorly.patterns.strategy.payment;

import java.math.BigDecimal;
import java.util.UUID;

public class NagadPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(
            BigDecimal amount,
            String accountNumber) {

        System.out.println("Processing Nagad payment...");
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
                    "Nagad account number is required."
            );
        }

        // Simulated Nagad transaction
        String transactionId =
                "NAGAD-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        return new PaymentResult(
                true,
                transactionId,
                "Nagad payment successful."
        );
    }

    @Override
    public PaymentResult refund(
            BigDecimal amount,
            String transactionId) {

        System.out.println("Processing Nagad refund...");

        return new PaymentResult(
                true,
                transactionId,
                "Nagad refund successful."
        );
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.NAGAD;
    }
}