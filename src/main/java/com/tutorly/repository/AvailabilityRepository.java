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

    private final TutorSubjectRepository tutorSubjectRepository = new TutorSubjectRepository();

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
                    int id = keys.getInt(1);
                    availability.setAvailabilityId(id);
                    return id;
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
                        WHEN 'Monday' THEN 1 WHEN 'Tuesday' THEN 2 WHEN 'Wednesday' THEN 3
                        WHEN 'Thursday' THEN 4 WHEN 'Friday' THEN 5 WHEN 'Saturday' THEN 6
                        WHEN 'Sunday' THEN 7 ELSE 8 END,
                    a.start_time
                """;

        List<Availability> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tutorId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
        }
        return result;
    }

    public List<SubjectOption> findSubjectsByTutorId(int tutorId) throws SQLException {
        List<SubjectOption> result = new ArrayList<>();
        for (TutorSubjectRepository.SubjectOption option : tutorSubjectRepository.findSubjectsByTutorId(tutorId)) {
            result.add(new SubjectOption(option.getId(), option.getName()));
        }
        return result;
    }

    public boolean tutorTeachesSubject(int tutorId, int subjectId) throws SQLException {
        return tutorSubjectRepository.tutorTeachesSubject(tutorId, subjectId);
    }

    public boolean isAvailable(int tutorId, LocalDate date, LocalTime time, int durationMinutes) throws SQLException {
        if (tutorId <= 0 || date == null || time == null || durationMinutes <= 0) return false;
        String dayOfWeek = convertDay(date.getDayOfWeek());
        LocalTime requestedEnd = time.plusMinutes(durationMinutes);
        if (!time.isBefore(requestedEnd)) return false;

        String sql = """
                SELECT COUNT(*) FROM availability
                WHERE tutor_id = ? AND day_of_week = ? AND status = 'Available'
                  AND start_time <= ? AND end_time >= ?
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

    public boolean isAvailable(int tutorId, int subjectId, LocalDate date,
                               LocalTime time, int durationMinutes) throws SQLException {
        if (tutorId <= 0 || subjectId <= 0 || date == null || time == null || durationMinutes <= 0) return false;
        String dayOfWeek = convertDay(date.getDayOfWeek());
        LocalTime requestedEnd = time.plusMinutes(durationMinutes);
        if (!time.isBefore(requestedEnd)) return false;

        String sql = """
                SELECT COUNT(*) FROM availability
                WHERE tutor_id = ? AND subject_id = ? AND day_of_week = ?
                  AND status = 'Available' AND start_time <= ? AND end_time >= ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tutorId);
            statement.setInt(2, subjectId);
            statement.setString(3, dayOfWeek);
            statement.setTime(4, Time.valueOf(time));
            statement.setTime(5, Time.valueOf(requestedEnd));
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean overlapsExistingAvailability(int tutorId, String dayOfWeek,
                                                LocalTime startTime, LocalTime endTime,
                                                int ignoredAvailabilityId) throws SQLException {
        String sql = """
                SELECT COUNT(*) FROM availability
                WHERE tutor_id = ? AND day_of_week = ? AND status = 'Available'
                  AND availability_id <> ?
                  AND start_time < ? AND end_time > ?
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tutorId);
            statement.setString(2, dayOfWeek);
            statement.setInt(3, ignoredAvailabilityId);
            statement.setTime(4, Time.valueOf(endTime));
            statement.setTime(5, Time.valueOf(startTime));
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

    public List<SubjectOption> findAllSubjects() throws SQLException {
        String sql = "SELECT subject_id, subject_name FROM subjects ORDER BY subject_name ASC";
        List<SubjectOption> result = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                result.add(new SubjectOption(rs.getInt("subject_id"), rs.getString("subject_name")));
            }
        }
        return result;
    }

    private Availability map(ResultSet rs) throws SQLException {
        Availability a = new Availability();
        a.setAvailabilityId(rs.getInt("availability_id"));
        a.setTutorId(rs.getInt("tutor_id"));
        a.setSubjectId(rs.getInt("subject_id"));
        a.setSubjectName(rs.getString("subject_name"));
        a.setDayOfWeek(rs.getString("day_of_week"));
        Time start = rs.getTime("start_time");
        Time end = rs.getTime("end_time");
        if (start != null) a.setStartTime(start.toLocalTime());
        if (end != null) a.setEndTime(end.toLocalTime());
        a.setDescription(rs.getString("description"));
        a.setStatus(rs.getString("status"));
        return a;
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
        public String toString() { return name; }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof SubjectOption option)) return false;
            return id == option.id;
        }

        @Override
        public int hashCode() { return Integer.hashCode(id); }
    }
}
