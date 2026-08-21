package com.tutorly.repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tutorly.patterns.singleton.DatabaseSingleton;
import com.tutorly.patterns.strategy.payment.PaymentMethod;

public class PaymentRepository {

    public boolean paymentExists(int bookingId)
            throws SQLException {

        String sql =
                "SELECT payment_id " +
                "FROM payments " +
                "WHERE booking_id = ?";

        try (Connection connection =
                     DatabaseSingleton.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public int createPayment(
            int bookingId,
            BigDecimal amount,
            PaymentMethod method,
            String status)
            throws SQLException {

        String sql =
                "INSERT INTO payments " +
                "(booking_id, amount, payment_method, payment_status) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection =
                     DatabaseSingleton.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, bookingId);
            statement.setBigDecimal(2, amount);
            statement.setString(
                    3,
                    method.getDatabaseValue()
            );
            statement.setString(4, status);

            statement.executeUpdate();

            try (ResultSet resultSet =
                         statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Failed to create payment."
        );
    }

    public void updatePaymentStatus(
            int bookingId,
            String status)
            throws SQLException {

        String sql =
                "UPDATE payments " +
                "SET payment_status = ? " +
                "WHERE booking_id = ?";

        try (Connection connection =
                     DatabaseSingleton.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, bookingId);

            statement.executeUpdate();
        }
    }

    public void updatePaymentStatusAndMethod(
            int bookingId,
            PaymentMethod method,
            String status)
            throws SQLException {

        String sql =
                "UPDATE payments " +
                "SET payment_method = ?, payment_status = ? " +
                "WHERE booking_id = ?";

        try (Connection connection =
                     DatabaseSingleton.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    method.getDatabaseValue()
            );

            statement.setString(2, status);
            statement.setInt(3, bookingId);

            statement.executeUpdate();
        }
    }

    public void updatePaymentStatusAndMethod(
            int bookingId,
            PaymentMethod method,
            String status,
            BigDecimal amount)
            throws SQLException {

        String sql =
                "UPDATE payments " +
                "SET amount = ?, payment_method = ?, " +
                "payment_status = ? " +
                "WHERE booking_id = ?";

        try (Connection connection =
                     DatabaseSingleton.getInstance().getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, amount);
            statement.setString(
                    2,
                    method.getDatabaseValue()
            );
            statement.setString(3, status);
            statement.setInt(4, bookingId);

            statement.executeUpdate();
        }
    }
}