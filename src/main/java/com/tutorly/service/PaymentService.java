package com.tutorly.service;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.tutorly.patterns.strategy.payment.BkashPaymentStrategy;
import com.tutorly.patterns.strategy.payment.CardPaymentStrategy;
import com.tutorly.patterns.strategy.payment.NagadPaymentStrategy;
import com.tutorly.patterns.strategy.payment.PaymentContext;
import com.tutorly.patterns.strategy.payment.PaymentMethod;
import com.tutorly.patterns.strategy.payment.PaymentResult;
import com.tutorly.patterns.strategy.payment.PaymentStrategy;
import com.tutorly.patterns.strategy.payment.RocketPaymentStrategy;
import com.tutorly.repository.PaymentRepository;

public class PaymentService {

    private final PaymentContext paymentContext;
    private final PaymentRepository paymentRepository;

    public PaymentService() {

        this.paymentContext = new PaymentContext();
        this.paymentRepository = new PaymentRepository();
    }

    public PaymentResult processPayment(
            int bookingId,
            BigDecimal amount,
            PaymentMethod method,
            String accountNumber)
            throws SQLException {

        if (bookingId <= 0) {
            return new PaymentResult(
                    false,
                    null,
                    "Invalid booking ID."
            );
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            return new PaymentResult(
                    false,
                    null,
                    "Invalid payment amount."
            );
        }

        if (method == null) {
            return new PaymentResult(
                    false,
                    null,
                    "Payment method must be selected."
            );
        }

        /*
         * STRATEGY SELECTION
         */
        PaymentStrategy strategy =
                createStrategy(method);

        /*
         * Set selected strategy into Context.
         */
        paymentContext.setStrategy(strategy);

        /*
         * Execute payment.
         */
        PaymentResult result =
                paymentContext.pay(
                        amount,
                        accountNumber
                );

        /*
         * Save payment result to database.
         */
        if (result.isSuccessful()) {

            if (paymentRepository.paymentExists(bookingId)) {

                paymentRepository.updatePaymentStatusAndMethod(
                        bookingId,
                        method,
                        "Paid",
                        amount
                );

            } else {

                paymentRepository.createPayment(
                        bookingId,
                        amount,
                        method,
                        "Paid"
                );
            }

        } else {

            if (paymentRepository.paymentExists(bookingId)) {

                paymentRepository.updatePaymentStatusAndMethod(
                        bookingId,
                        method,
                        "Failed",
                        amount
                );

            } else {

                paymentRepository.createPayment(
                        bookingId,
                        amount,
                        method,
                        "Failed"
                );
            }
        }

        return result;
    }

    public PaymentResult refundPayment(
            int bookingId,
            BigDecimal amount,
            PaymentMethod method,
            String transactionId)
            throws SQLException {

        PaymentStrategy strategy =
                createStrategy(method);

        paymentContext.setStrategy(strategy);

        PaymentResult result =
                paymentContext.refund(
                        amount,
                        transactionId
                );

        if (result.isSuccessful()) {

            paymentRepository.updatePaymentStatus(
                    bookingId,
                    "Refunded"
            );
        }

        return result;
    }

    private PaymentStrategy createStrategy(
            PaymentMethod method) {

        return switch (method) {

            case BKASH ->
                    new BkashPaymentStrategy();

            case NAGAD ->
                    new NagadPaymentStrategy();

            case ROCKET ->
                    new RocketPaymentStrategy();

            case CARD ->
                    new CardPaymentStrategy();
        };
    }
}