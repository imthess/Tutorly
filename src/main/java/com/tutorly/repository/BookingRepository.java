package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Booking;
import com.tutorly.model.BookingDetails;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {

    public int createBooking(Booking booking) throws SQLException {
        String sql = """
                INSERT INTO bookings
                (student_id, tutor_id, subject_id, booking_date, booking_time, duration, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindBooking(statement, booking);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    booking.setBookingId(keys.getInt(1));
                    return booking.getBookingId();
                }
            }
        }
        throw new SQLException("Failed to create booking.");
    }

    public int createBookings(List<Booking> bookings) throws SQLException {
        if (bookings == null || bookings.isEmpty()) return 0;

        String sql = """
                INSERT INTO bookings
                (student_id, tutor_id, subject_id, booking_date, booking_time, duration, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                for (Booking booking : bookings) {
                    bindBooking(statement, booking);
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();

                try (ResultSet keys = statement.getGeneratedKeys()) {
                    int index = 0;
                    while (keys.next() && index < bookings.size()) {
                        bookings.get(index++).setBookingId(keys.getInt(1));
                    }
                }

                connection.setAutoCommit(previousAutoCommit);
                return bookings.size();
            } catch (SQLException e) {
                connection.rollback();
                connection.setAutoCommit(previousAutoCommit);
                throw e;
            }
        }
    }

    private void bindBooking(PreparedStatement statement, Booking booking) throws SQLException {
        statement.setInt(1, booking.getStudentId());
        statement.setInt(2, booking.getTutorId());
        statement.setInt(3, booking.getSubjectId());
        statement.setDate(4, Date.valueOf(booking.getBookingDate()));
        statement.setTime(5, Time.valueOf(booking.getBookingTime()));
        statement.setInt(6, booking.getDuration());
        statement.setString(7, booking.getStatus());
    }

    public Booking findById(int bookingId) throws SQLException {
        String sql = """
                SELECT booking_id, student_id, tutor_id, subject_id,
                       booking_date, booking_time, duration, status
                FROM bookings WHERE booking_id = ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? mapBooking(rs) : null;
            }
        }
    }

    public List<Booking> findByStudentId(int studentId) throws SQLException {
        return findByColumn("student_id", studentId);
    }

    public List<Booking> findByTutorId(int tutorId) throws SQLException {
        return findByColumn("tutor_id", tutorId);
    }

    private List<Booking> findByColumn(String column, int id) throws SQLException {
        String sql = """
                SELECT booking_id, student_id, tutor_id, subject_id,
                       booking_date, booking_time, duration, status
                FROM bookings
                WHERE %s = ?
                ORDER BY booking_date DESC, booking_time DESC
                """.formatted(column);

        List<Booking> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) bookings.add(mapBooking(rs));
            }
        }
        return bookings;
    }

    public List<BookingDetails> findTutorBookingDetails(int tutorId) throws SQLException {
        String sql = """
                SELECT b.booking_id, b.student_id, b.tutor_id, b.subject_id,
                       b.booking_date, b.booking_time, b.duration, b.status,
                       su.full_name AS student_name, su.email AS student_email,
                       su.phone AS student_phone, s.education, s.institute,
                       tu.full_name AS tutor_name, sub.subject_name
                FROM bookings b
                JOIN students s ON b.student_id = s.student_id
                JOIN users su ON s.user_id = su.user_id
                JOIN tutors t ON b.tutor_id = t.tutor_id
                JOIN users tu ON t.user_id = tu.user_id
                JOIN subjects sub ON b.subject_id = sub.subject_id
                WHERE b.tutor_id = ?
                ORDER BY b.booking_date DESC, b.booking_time DESC
                """;

        List<BookingDetails> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tutorId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    BookingDetails d = new BookingDetails();
                    mapCommonBookingDetails(d, rs);
                    d.setStudentName(rs.getString("student_name"));
                    d.setEmail(rs.getString("student_email"));
                    d.setPhone(rs.getString("student_phone"));
                    d.setEducation(rs.getString("education"));
                    d.setInstitute(rs.getString("institute"));
                    d.setTutorName(rs.getString("tutor_name"));
                    d.setSubjectName(rs.getString("subject_name"));
                    bookings.add(d);
                }
            }
        }
        return bookings;
    }

    public List<BookingDetails> findStudentBookingDetails(int studentId) throws SQLException {
        String sql = """
                SELECT b.booking_id, b.student_id, b.tutor_id, b.subject_id,
                       b.booking_date, b.booking_time, b.duration, b.status,
                       su.full_name AS student_name, su.email AS student_email,
                       su.phone AS student_phone, s.education, s.institute,
                       tu.full_name AS tutor_name, tu.email AS tutor_email,
                       tu.phone AS tutor_phone, t.qualifications, t.experience,
                       t.hourly_rate, t.bio, sub.subject_name
                FROM bookings b
                JOIN students s ON b.student_id = s.student_id
                JOIN users su ON s.user_id = su.user_id
                JOIN tutors t ON b.tutor_id = t.tutor_id
                JOIN users tu ON t.user_id = tu.user_id
                JOIN subjects sub ON b.subject_id = sub.subject_id
                WHERE b.student_id = ?
                ORDER BY b.booking_date DESC, b.booking_time DESC
                """;

        List<BookingDetails> bookings = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    BookingDetails d = new BookingDetails();
                    mapCommonBookingDetails(d, rs);
                    d.setStudentName(rs.getString("student_name"));
                    d.setEmail(rs.getString("student_email"));
                    d.setPhone(rs.getString("student_phone"));
                    d.setEducation(rs.getString("education"));
                    d.setInstitute(rs.getString("institute"));
                    d.setTutorName(rs.getString("tutor_name"));
                    d.setQualifications(rs.getString("qualifications"));
                    d.setExperience(rs.getInt("experience"));
                    d.setHourlyRate(rs.getDouble("hourly_rate"));
                    d.setBio(rs.getString("bio"));
                    d.setSubjectName(rs.getString("subject_name"));
                    bookings.add(d);
                }
            }
        }
        return bookings;
    }

    public int findTutorUserId(int tutorId) throws SQLException {
        return findUserId("tutors", "tutor_id", tutorId);
    }

    public int findStudentUserId(int studentId) throws SQLException {
        return findUserId("students", "student_id", studentId);
    }

    private int findUserId(String table, String idColumn, int id) throws SQLException {
        String sql = "SELECT user_id FROM " + table + " WHERE " + idColumn + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) throw new SQLException("User profile not found.");
                return rs.getInt("user_id");
            }
        }
    }

    public boolean hasOverlappingBooking(int tutorId, Date bookingDate, Time bookingTime, int duration) throws SQLException {
        return hasOverlappingBookingForColumn("tutor_id", tutorId, bookingDate, bookingTime, duration, 0);
    }

    public boolean hasOverlappingBooking(int tutorId, Date bookingDate, Time bookingTime, int duration, int excludedBookingId) throws SQLException {
        return hasOverlappingBookingForColumn("tutor_id", tutorId, bookingDate, bookingTime, duration, excludedBookingId);
    }

    public boolean hasOverlappingStudentBooking(int studentId, Date bookingDate, Time bookingTime, int duration) throws SQLException {
        return hasOverlappingBookingForColumn("student_id", studentId, bookingDate, bookingTime, duration, 0);
    }

    private boolean hasOverlappingBookingForColumn(
            String column, int id, Date bookingDate, Time bookingTime, int duration, int excludedBookingId
    ) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM bookings
                WHERE %s = ?
                  AND booking_id <> ?
                  AND booking_date = ?
                  AND status IN ('Pending', 'Accepted')
                  AND booking_time < ADDTIME(?, SEC_TO_TIME(? * 60))
                  AND ADDTIME(booking_time, SEC_TO_TIME(duration * 60)) > ?
                """.formatted(column);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setInt(2, excludedBookingId);
            statement.setDate(3, bookingDate);
            statement.setTime(4, bookingTime);
            statement.setInt(5, duration);
            statement.setTime(6, bookingTime);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean updateStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, bookingId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteBooking(int bookingId) throws SQLException {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookingId);
            return statement.executeUpdate() > 0;
        }
    }

    private void mapCommonBookingDetails(BookingDetails details, ResultSet rs) throws SQLException {
        details.setBookingId(rs.getInt("booking_id"));
        details.setStudentId(rs.getInt("student_id"));
        details.setTutorId(rs.getInt("tutor_id"));
        details.setSubjectId(rs.getInt("subject_id"));
        Date date = rs.getDate("booking_date");
        if (date != null) details.setBookingDate(date.toLocalDate().toString());
        Time time = rs.getTime("booking_time");
        if (time != null) details.setBookingTime(time.toLocalTime().toString());
        details.setDuration(rs.getInt("duration"));
        details.setStatus(rs.getString("status"));
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setStudentId(rs.getInt("student_id"));
        booking.setTutorId(rs.getInt("tutor_id"));
        booking.setSubjectId(rs.getInt("subject_id"));
        Date date = rs.getDate("booking_date");
        if (date != null) booking.setBookingDate(date.toLocalDate());
        Time time = rs.getTime("booking_time");
        if (time != null) booking.setBookingTime(time.toLocalTime());
        booking.setDuration(rs.getInt("duration"));
        booking.setStatus(rs.getString("status"));
        return booking;
    }
}
