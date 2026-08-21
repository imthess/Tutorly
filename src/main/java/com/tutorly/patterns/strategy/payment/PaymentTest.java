package com.tutorly.patterns.strategy.payment;

import java.math.BigDecimal;

public class PaymentTest {

    public static void main(String[] args) {

        PaymentContext context =
                new PaymentContext();

        /*
         * bKash
         */
        context.setStrategy(
                new BkashPaymentStrategy()
        );

        PaymentResult bkashResult =
                context.pay(
                        new BigDecimal("1000.00"),
                        "01712345678"
                );

        System.out.println(
                bkashResult.getMessage()
        );

        System.out.println(
                "Transaction ID: "
                        + bkashResult.getTransactionId()
        );


        /*
         * Nagad
         */
        context.setStrategy(
                new NagadPaymentStrategy()
        );

        PaymentResult nagadResult =
                context.pay(
                        new BigDecimal("1500.00"),
                        "01812345678"
                );

        System.out.println(
                nagadResult.getMessage()
        );

        System.out.println(
                "Transaction ID: "
                        + nagadResult.getTransactionId()
        );


        /*
         * Rocket
         */
        context.setStrategy(
                new RocketPaymentStrategy()
        );

        PaymentResult rocketResult =
                context.pay(
                        new BigDecimal("800.00"),
                        "01912345678"
                );

        System.out.println(
                rocketResult.getMessage()
        );

        System.out.println(
                "Transaction ID: "
                        + rocketResult.getTransactionId()
        );


        /*
         * Card
         */
        context.setStrategy(
                new CardPaymentStrategy()
        );

        PaymentResult cardResult =
                context.pay(
                        new BigDecimal("2000.00"),
                        "1234567812345678"
                );

        System.out.println(
                cardResult.getMessage()
        );

        System.out.println(
                "Transaction ID: "
                        + cardResult.getTransactionId()
        );
    }
}