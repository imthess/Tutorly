package com.tutorly.patterns.strategy.payment;

import java.math.BigDecimal;
import java.util.UUID;

public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentResult pay(
            BigDecimal amount,
            String cardNumber) {

        System.out.println("Processing Card payment...");
        System.out.println("Card: **** **** **** "
                + getLastFourDigits(cardNumber));
        System.out.println("Amount: " + amount);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new PaymentResult(
                    false,
                    null,
                    "Invalid payment amount."
            );
        }

        if (!isValidCardNumber(cardNumber)) {
            return new PaymentResult(
                    false,
                    null,
                    "Invalid card number."
            );
        }

        // Simulated card transaction
        String transactionId =
                "CARD-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        return new PaymentResult(
                true,
                transactionId,
                "Card payment successful."
        );
    }

    @Override
    public PaymentResult refund(
            BigDecimal amount,
            String transactionId) {

        System.out.println("Processing Card refund...");

        return new PaymentResult(
                true,
                transactionId,
                "Card refund successful."
        );
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CARD;
    }

    private boolean isValidCardNumber(String cardNumber) {

        if (cardNumber == null) {
            return false;
        }

        String cleaned = cardNumber.replaceAll("\\s+", "");

        return cleaned.matches("\\d{16}");
    }

    private String getLastFourDigits(String cardNumber) {

        if (cardNumber == null) {
            return "****";
        }

        String cleaned = cardNumber.replaceAll("\\s+", "");

        if (cleaned.length() < 4) {
            return "****";
        }

        return cleaned.substring(cleaned.length() - 4);
    }
}