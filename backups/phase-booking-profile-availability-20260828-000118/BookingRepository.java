package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Booking;
import com.tutorly.model.BookingDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    public int createBooking(Booking booking)
            throws SQLException {

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
                                java.sql.Statement.RETURN_GENERATED_KEYS
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
                    java.sql.Date.valueOf(
                            booking.getBookingDate()
                    )
            );

            statement.setTime(
                    5,
                    java.sql.Time.valueOf(
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
                SELECT
                    booking_id,
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

            try (ResultSet resultSet =
                         statement.executeQuery()) {

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

        return findBookings(
                "student_id",
                studentId
        );
    }

    public List<Booking> findByTutorId(
            int tutorId
    ) throws SQLException {

        return findBookings(
                "tutor_id",
                tutorId
        );
    }

    private List<Booking> findBookings(
            String column,
            int id
    ) throws SQLException {

        String sql = """
                SELECT
                    booking_id,
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

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    bookings.add(
                            mapBooking(resultSet)
                    );
                }
            }
        }

        return bookings;
    }

    public List<BookingDetails> findStudentDetails(
            int studentId
    ) throws SQLException {

        String sql = """
                SELECT
                    b.booking_id,
                    b.student_id,
                    b.tutor_id,
                    b.subject_id,
                    su.full_name AS student_name,
                    tu.full_name AS tutor_name,
                    s.subject_name,
                    b.booking_date,
                    b.booking_time,
                    b.duration,
                    b.status
                FROM bookings b
                JOIN students st
                    ON b.student_id = st.student_id
                JOIN users su
                    ON st.user_id = su.user_id
                JOIN tutors t
                    ON b.tutor_id = t.tutor_id
                JOIN users tu
                    ON t.user_id = tu.user_id
                JOIN subjects s
                    ON b.subject_id = s.subject_id
                WHERE b.student_id = ?
                ORDER BY b.booking_date DESC,
                         b.booking_time DESC
                """;

        return executeDetailsQuery(
                sql,
                studentId
        );
    }

    public List<BookingDetails> findTutorDetails(
            int tutorId
    ) throws SQLException {

        String sql = """
                SELECT
                    b.booking_id,
                    b.student_id,
                    b.tutor_id,
                    b.subject_id,
                    su.full_name AS student_name,
                    tu.full_name AS tutor_name,
                    s.subject_name,
                    b.booking_date,
                    b.booking_time,
                    b.duration,
                    b.status
                FROM bookings b
                JOIN students st
                    ON b.student_id = st.student_id
                JOIN users su
                    ON st.user_id = su.user_id
                JOIN tutors t
                    ON b.tutor_id = t.tutor_id
                JOIN users tu
                    ON t.user_id = tu.user_id
                JOIN subjects s
                    ON b.subject_id = s.subject_id
                WHERE b.tutor_id = ?
                ORDER BY b.booking_date DESC,
                         b.booking_time DESC
                """;

        return executeDetailsQuery(
                sql,
                tutorId
        );
    }

    private List<BookingDetails> executeDetailsQuery(
            String sql,
            int id
    ) throws SQLException {

        List<BookingDetails> bookings =
                new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    BookingDetails details =
                            new BookingDetails();

                    details.setBookingId(
                            resultSet.getInt("booking_id")
                    );

                    details.setStudentId(
                            resultSet.getInt("student_id")
                    );

                    details.setTutorId(
                            resultSet.getInt("tutor_id")
                    );

                    details.setSubjectId(
                            resultSet.getInt("subject_id")
                    );

                    details.setStudentName(
                            resultSet.getString("student_name")
                    );

                    details.setTutorName(
                            resultSet.getString("tutor_name")
                    );

                    details.setSubjectName(
                            resultSet.getString("subject_name")
                    );

                    details.setBookingDate(
                            resultSet
                                    .getDate("booking_date")
                                    .toLocalDate()
                    );

                    java.sql.Time time =
                            resultSet.getTime(
                                    "booking_time"
                            );

                    if (time != null) {
                        details.setBookingTime(
                                time.toLocalTime()
                        );
                    }

                    details.setDuration(
                            resultSet.getInt("duration")
                    );

                    details.setStatus(
                            resultSet.getString("status")
                    );

                    bookings.add(details);
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
            ResultSet resultSet
    ) throws SQLException {

        Booking booking =
                new Booking();

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
                resultSet
                        .getDate("booking_date")
                        .toLocalDate()
        );

        java.sql.Time time =
                resultSet.getTime("booking_time");

        if (time != null) {
            booking.setBookingTime(
                    time.toLocalTime()
            );
        }

        booking.setDuration(
                resultSet.getInt("duration")
        );

        booking.setStatus(
                resultSet.getString("status")
        );

        return booking;
    }
}
