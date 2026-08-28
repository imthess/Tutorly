package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Availability;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AvailabilityRepository {

    public int create(Availability availability) throws SQLException {
        String sql = """
                INSERT INTO availability
                (tutor_id, subject_id, day_of_week, start_time, end_time, description, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, availability.getTutorId());
            statement.setInt(2, availability.getSubjectId());
            statement.setString(3, availability.getDayOfWeek());
            statement.setTime(4, Time.valueOf(availability.getStartTime()));
            statement.setTime(5, Time.valueOf(availability.getEndTime()));
            statement.setString(6, availability.getDescription());
            statement.setString(7, availability.getStatus());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int availabilityId = keys.getInt(1);
                    availability.setAvailabilityId(availabilityId);
                    return availabilityId;
                }
            }
        }

        throw new SQLException("Failed to create availability record.");
    }

    public boolean update(int availabilityId, int tutorId, int subjectId,
                          String dayOfWeek, LocalTime startTime, LocalTime endTime,
                          String description) throws SQLException {

        String sql = """
                UPDATE availability
                SET subject_id = ?, day_of_week = ?, start_time = ?, end_time = ?, description = ?
                WHERE availability_id = ? AND tutor_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, subjectId);
            statement.setString(2, dayOfWeek);
            statement.setTime(3, Time.valueOf(startTime));
            statement.setTime(4, Time.valueOf(endTime));
            statement.setString(5, description);
            statement.setInt(6, availabilityId);
            statement.setInt(7, tutorId);

            return statement.executeUpdate() > 0;
        }
    }

    public List<Availability> findByTutorId(int tutorId) throws SQLException {
        String sql = """
                SELECT a.availability_id, a.tutor_id, a.subject_id, s.subject_name,
                       a.day_of_week, a.start_time, a.end_time, a.description, a.status
                FROM availability a
                JOIN subjects s ON a.subject_id = s.subject_id
                WHERE a.tutor_id = ?
                ORDER BY
                    CASE a.day_of_week
                        WHEN 'Monday' THEN 1
                        WHEN 'Tuesday' THEN 2
                        WHEN 'Wednesday' THEN 3
                        WHEN 'Thursday' THEN 4
                        WHEN 'Friday' THEN 5
                        WHEN 'Saturday' THEN 6
                        WHEN 'Sunday' THEN 7
                        ELSE 8
                    END,
                    a.start_time
                """;

        List<Availability> availabilityList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, tutorId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    availabilityList.add(map(rs));
                }
            }
        }

        return availabilityList;
    }

    public List<SubjectOption> findSubjectsByTutorId(int tutorId) throws SQLException {
        String sql = "SELECT subject_id, subject_name FROM subjects ORDER BY subject_name ASC";

        List<SubjectOption> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                subjects.add(new SubjectOption(
                        rs.getInt("subject_id"),
                        rs.getString("subject_name")
                ));
            }
        }

        return subjects;
    }

    public boolean tutorTeachesSubject(int tutorId, int subjectId) throws SQLException {
        return true;
    }

    public boolean isAvailable(int tutorId, LocalDate date, LocalTime time, int durationMinutes) throws SQLException {
        if (tutorId <= 0 || date == null || time == null || durationMinutes <= 0) {
            return false;
        }

        String dayOfWeek = convertDay(date.getDayOfWeek());
        LocalTime requestedEnd = time.plusMinutes(durationMinutes);

        if (requestedEnd.isBefore(time)) {
            return false;
        }

        String sql = """
                SELECT COUNT(*)
                FROM availability
                WHERE tutor_id = ?
                  AND day_of_week = ?
                  AND status = 'Available'
                  AND start_time <= ?
                  AND end_time >= ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, tutorId);
            statement.setString(2, dayOfWeek);
            statement.setTime(3, Time.valueOf(time));
            statement.setTime(4, Time.valueOf(requestedEnd));

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean delete(int availabilityId) throws SQLException {
        String sql = "DELETE FROM availability WHERE availability_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, availabilityId);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(int availabilityId, String status) throws SQLException {
        String sql = "UPDATE availability SET status = ? WHERE availability_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status);
            statement.setInt(2, availabilityId);
            return statement.executeUpdate() > 0;
        }
    }

    private Availability map(ResultSet rs) throws SQLException {
        Availability availability = new Availability();
        availability.setAvailabilityId(rs.getInt("availability_id"));
        availability.setTutorId(rs.getInt("tutor_id"));
        availability.setSubjectId(rs.getInt("subject_id"));
        availability.setSubjectName(rs.getString("subject_name"));
        availability.setDayOfWeek(rs.getString("day_of_week"));

        Time start = rs.getTime("start_time");
        if (start != null) {
            availability.setStartTime(start.toLocalTime());
        }

        Time end = rs.getTime("end_time");
        if (end != null) {
            availability.setEndTime(end.toLocalTime());
        }

        availability.setDescription(rs.getString("description"));
        availability.setStatus(rs.getString("status"));

        return availability;
    }

    private String convertDay(DayOfWeek day) {
        return day.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    public static class SubjectOption {
        private final int id;
        private final String name;

        public SubjectOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }
        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SubjectOption option = (SubjectOption) o;
            return id == option.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    public int findOrCreateSubject(String subjectName, int tutorId) throws SQLException {
        String cleanName = subjectName.trim();
        int subjectId = -1;

        String selectSql = "SELECT subject_id FROM subjects WHERE LOWER(subject_name) = LOWER(?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectSql)) {
            statement.setString(1, cleanName);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    subjectId = rs.getInt("subject_id");
                }
            }
        }

        if (subjectId == -1) {
            String insertSql = "INSERT INTO subjects (subject_name) VALUES (?)";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, cleanName);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        subjectId = keys.getInt(1);
                    }
                }
            }
        }

        return subjectId;
    }

    public List<SubjectOption> findAllSubjects() throws SQLException {
        String sql = "SELECT subject_id, subject_name FROM subjects ORDER BY subject_name ASC";
        List<SubjectOption> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                subjects.add(new SubjectOption(
                    rs.getInt("subject_id"),
                    rs.getString("subject_name")
                ));
            }
        }
        return subjects;
    }
}
