package com.tutorly.patterns.strategy.payment;

public enum PaymentMethod {

    BKASH("Bkash"),
    NAGAD("Nagad"),
    ROCKET("Rocket"),
    CARD("Card");

    private final String databaseValue;

    PaymentMethod(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    public String getDatabaseValue() {
        return databaseValue;
    }

    @Override
    public String toString() {
        return databaseValue;
    }
}