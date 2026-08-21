package com.tutorly.patterns.strategy.payment;

import java.math.BigDecimal;
import java.util.UUID;

public class BkashPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(
            BigDecimal amount,
            String accountNumber) {

        System.out.println("Processing bKash payment...");
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
                    "bKash account number is required."
            );
        }

        // Simulated bKash transaction
        String transactionId =
                "BKASH-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        return new PaymentResult(
                true,
                transactionId,
                "bKash payment successful."
        );
    }

    @Override
    public PaymentResult refund(
            BigDecimal amount,
            String transactionId) {

        System.out.println("Processing bKash refund...");

        return new PaymentResult(
                true,
                transactionId,
                "bKash refund successful."
        );
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.BKASH;
    }
}