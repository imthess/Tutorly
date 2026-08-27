package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    public int createBooking(
            Booking booking
    ) throws SQLException {

        String sql = """
                INSERT INTO bookings
                (
                    student_id,
                    tutor_id,
                    subject_id,
                    booking_date,
                    booking_time,
                    duration,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            statement.setInt(
                    1,
                    booking.getStudentId()
            );

            statement.setInt(
                    2,
                    booking.getTutorId()
            );

            statement.setInt(
                    3,
                    booking.getSubjectId()
            );

            statement.setDate(
                    4,
                    Date.valueOf(
                            booking.getBookingDate()
                    )
            );

            statement.setTime(
                    5,
                    Time.valueOf(
                            booking.getBookingTime()
                    )
            );

            statement.setInt(
                    6,
                    booking.getDuration()
            );

            statement.setString(
                    7,
                    booking.getStatus()
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {

                    int bookingId =
                            keys.getInt(1);

                    booking.setBookingId(
                            bookingId
                    );

                    return bookingId;
                }
            }
        }

        throw new SQLException(
                "Failed to create booking."
        );
    }

    public Booking findById(
            int bookingId
    ) throws SQLException {

        String sql = """
                SELECT booking_id,
                       student_id,
                       tutor_id,
                       subject_id,
                       booking_date,
                       booking_time,
                       duration,
                       status
                FROM bookings
                WHERE booking_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, bookingId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                return mapBooking(rs);
            }
        }
    }

    public List<Booking> findByStudentId(
            int studentId
    ) throws SQLException {

        return findByColumn(
                "student_id",
                studentId
        );
    }

    public List<Booking> findByTutorId(
            int tutorId
    ) throws SQLException {

        return findByColumn(
                "tutor_id",
                tutorId
        );
    }

    private List<Booking> findByColumn(
            String column,
            int id
    ) throws SQLException {

        String sql = """
                SELECT booking_id,
                       student_id,
                       tutor_id,
                       subject_id,
                       booking_date,
                       booking_time,
                       duration,
                       status
                FROM bookings
                WHERE %s = ?
                ORDER BY booking_date DESC,
                         booking_time DESC
                """.formatted(column);

        List<Booking> bookings =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (ResultSet rs =
                         statement.executeQuery()) {

                while (rs.next()) {
                    bookings.add(
                            mapBooking(rs)
                    );
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
                Connection connection =
                        DatabaseConnection.getConnection();
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
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, bookingId);

            return statement.executeUpdate() > 0;
        }
    }

    private Booking mapBooking(
            ResultSet rs
    ) throws SQLException {

        Booking booking = new Booking();

        booking.setBookingId(
                rs.getInt("booking_id")
        );

        booking.setStudentId(
                rs.getInt("student_id")
        );

        booking.setTutorId(
                rs.getInt("tutor_id")
        );

        booking.setSubjectId(
                rs.getInt("subject_id")
        );

        Date date =
                rs.getDate("booking_date");

        if (date != null) {
            booking.setBookingDate(
                    date.toLocalDate()
            );
        }

        Time time =
                rs.getTime("booking_time");

        if (time != null) {
            booking.setBookingTime(
                    time.toLocalTime()
            );
        }

        booking.setDuration(
                rs.getInt("duration")
        );

        booking.setStatus(
                rs.getString("status")
        );

        return booking;
    }
}
