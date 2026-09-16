package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Tutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TutorRepository {

    public void createProfile(Connection connection, int userId) throws SQLException {
        String sql = "INSERT INTO tutors (user_id) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public boolean isProfileComplete(int userId) throws SQLException {
        String sql = """
                SELECT qualifications, experience, hourly_rate, bio
                FROM tutors
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return false;

                String qualifications = rs.getString("qualifications");
                int experience = rs.getInt("experience");
                double hourlyRate = rs.getDouble("hourly_rate");
                String bio = rs.getString("bio");

                return qualifications != null && !qualifications.isBlank()
                        && experience >= 0
                        && hourlyRate > 0
                        && bio != null && !bio.isBlank();
            }
        }
    }

    public Tutor findByUserId(int userId) throws SQLException {
        String sql = """
                SELECT t.tutor_id, t.user_id, t.qualifications, t.experience,
                       t.hourly_rate, t.bio, u.full_name, u.email, u.phone, u.role
                FROM tutors t
                JOIN users u ON t.user_id = u.user_id
                WHERE t.user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) return null;
                return mapTutor(rs);
            }
        }
    }

    public List<Tutor> searchTutors(
            String keyword,
            int subjectId,
            Double minRate,
            Double maxRate,
            Integer minExperience,
            String dayOfWeek
    ) throws SQLException {
        String sql = """
                SELECT t.tutor_id, t.user_id, t.qualifications, t.experience,
                       t.hourly_rate, t.bio, u.full_name, u.email, u.phone, u.role
                FROM tutors t
                JOIN users u ON t.user_id = u.user_id
                WHERE t.qualifications IS NOT NULL
                  AND t.experience IS NOT NULL
                  AND t.hourly_rate IS NOT NULL
                  AND EXISTS (
                      SELECT 1
                      FROM availability a
                      WHERE a.tutor_id = t.tutor_id
                        AND a.status = 'Available'
                  )
                  AND (? = '' OR u.full_name LIKE ? OR t.qualifications LIKE ? OR t.bio LIKE ?)
                  AND (? = 0 OR EXISTS (
                      SELECT 1 FROM tutor_subjects ts2
                      WHERE ts2.tutor_id = t.tutor_id
                        AND ts2.subject_id = ?
                  ) OR EXISTS (
                      SELECT 1 FROM availability a2
                      WHERE a2.tutor_id = t.tutor_id
                        AND a2.subject_id = ?
                        AND a2.status = 'Available'
                  ))
                  AND (? IS NULL OR t.hourly_rate >= ?)
                  AND (? IS NULL OR t.hourly_rate <= ?)
                  AND (? IS NULL OR t.experience >= ?)
                  AND (? = '' OR EXISTS (
                      SELECT 1 FROM availability a3
                      WHERE a3.tutor_id = t.tutor_id
                        AND a3.day_of_week = ?
                        AND a3.status = 'Available'
                  ))
                ORDER BY u.full_name ASC
                """;

        List<Tutor> result = new ArrayList<>();
        String cleanKeyword = keyword == null ? "" : keyword.trim();
        String pattern = "%" + cleanKeyword + "%";
        String cleanDay = dayOfWeek == null ? "" : dayOfWeek.trim();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            statement.setString(i++, cleanKeyword);
            statement.setString(i++, pattern);
            statement.setString(i++, pattern);
            statement.setString(i++, pattern);

            statement.setInt(i++, subjectId);
            statement.setInt(i++, subjectId);
            statement.setInt(i++, subjectId);

            statement.setObject(i++, minRate);
            statement.setObject(i++, minRate);
            statement.setObject(i++, maxRate);
            statement.setObject(i++, maxRate);
            statement.setObject(i++, minExperience);
            statement.setObject(i++, minExperience);

            statement.setString(i++, cleanDay);
            statement.setString(i, cleanDay);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) result.add(mapTutor(rs));
            }
        }

        return result;
    }

    public void updateProfile(
            int userId,
            String qualifications,
            int experience,
            double hourlyRate,
            String bio
    ) throws SQLException {
        String sql = """
                UPDATE tutors
                SET qualifications = ?, experience = ?, hourly_rate = ?, bio = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, qualifications);
            statement.setInt(2, experience);
            statement.setDouble(3, hourlyRate);
            statement.setString(4, bio);
            statement.setInt(5, userId);

            if (statement.executeUpdate() == 0) {
                throw new SQLException("No tutor profile exists for user ID: " + userId);
            }
        }
    }

    private Tutor mapTutor(ResultSet rs) throws SQLException {
        Tutor tutor = new Tutor();
        tutor.setTutorId(rs.getInt("tutor_id"));
        tutor.setUserId(rs.getInt("user_id"));
        tutor.setFullName(rs.getString("full_name"));
        tutor.setEmail(rs.getString("email"));
        tutor.setPhone(rs.getString("phone"));
        tutor.setRole(rs.getString("role"));
        tutor.setQualifications(rs.getString("qualifications"));
        tutor.setExperience(rs.getInt("experience"));
        tutor.setHourlyRate(rs.getDouble("hourly_rate"));
        tutor.setBio(rs.getString("bio"));
        return tutor;
    }
}
