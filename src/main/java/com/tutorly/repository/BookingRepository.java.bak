package com.tutorly.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Booking;

public class BookingRepository {

    public int createBooking(Booking booking) throws SQLException {

        String sql = """
                INSERT INTO bookings
                (
                    student_id,
                    tutor_id,
                    subject_id,
                    booking_date,
                    status
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                java.sql.Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(1, booking.getStudentId());
            statement.setInt(2, booking.getTutorId());
            statement.setInt(3, booking.getSubjectId());

            statement.setDate(4, java.sql.Date.valueOf(booking.getBookingDate()));

            statement.setString(
                    5,
                    booking.getStatus()
            );

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {

                    int bookingId = keys.getInt(1);

                    booking.setBookingId(bookingId);

                    return bookingId;
                }
            }
        }

        throw new SQLException("Failed to create booking.");
    }

    public Booking findById(int bookingId) throws SQLException {

        String sql = """
                SELECT
                    booking_id,
                    student_id,
                    tutor_id,
                    subject_id,
                    booking_date,
                    status
                FROM bookings
                WHERE booking_id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, bookingId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                return mapBooking(resultSet);
            }
        }
    }

    public List<Booking> findByStudentId(
            int studentId
    ) throws SQLException {

        String sql = """
                SELECT
                    booking_id,
                    student_id,
                    tutor_id,
                    subject_id,
                    booking_date,
                    status
                FROM bookings
                WHERE student_id = ?
                ORDER BY booking_date DESC
                """;

        List<Booking> bookings = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    bookings.add(mapBooking(resultSet));
                }
            }
        }

        return bookings;
    }

    public List<Booking> findByTutorId(
            int tutorId
    ) throws SQLException {

        String sql = """
                SELECT
                    booking_id,
                    student_id,
                    tutor_id,
                    subject_id,
                    booking_date,
                    status
                FROM bookings
                WHERE tutor_id = ?
                ORDER BY booking_date DESC
                """;

        List<Booking> bookings = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, tutorId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    bookings.add(mapBooking(resultSet));
                }
            }
        }

        return bookings;
    }

    public boolean updateStatus(
            int bookingId,
            String status
    ) throws SQLException {

        String sql = """
                UPDATE bookings
                SET status = ?
                WHERE booking_id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, status);
            statement.setInt(2, bookingId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteBooking(
            int bookingId
    ) throws SQLException {

        String sql = """
                DELETE FROM bookings
                WHERE booking_id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, bookingId);

            return statement.executeUpdate() > 0;
        }
    }

    private Booking mapBooking(
            ResultSet resultSet
    ) throws SQLException {

        Booking booking = new Booking();

        booking.setBookingId(
                resultSet.getInt("booking_id")
        );

        booking.setStudentId(
                resultSet.getInt("student_id")
        );

        booking.setTutorId(
                resultSet.getInt("tutor_id")
        );

        booking.setSubjectId(
                resultSet.getInt("subject_id")
        );

        booking.setBookingDate(
                resultSet.getDate("booking_date").toLocalDate()
            );

        booking.setStatus(
                resultSet.getString("status")
        );

        return booking;
    }
}