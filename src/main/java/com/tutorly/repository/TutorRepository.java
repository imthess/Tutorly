package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Tutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TutorRepository {

    public void createProfile(Connection connection, int userId)
            throws SQLException {

        String sql = """
                INSERT INTO tutors (user_id)
                VALUES (?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public Tutor findByUserId(int userId) throws SQLException {

        String sql = """
                SELECT
                    t.tutor_id,
                    t.user_id,
                    t.qualifications,
                    t.experience,
                    t.hourly_rate,
                    t.bio,
                    u.full_name,
                    u.email,
                    u.phone,
                    u.role
                FROM tutors t
                JOIN users u ON t.user_id = u.user_id
                WHERE t.user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                Tutor tutor = new Tutor();

                tutor.setTutorId(
                        resultSet.getInt("tutor_id")
                );

                tutor.setUserId(
                        resultSet.getInt("user_id")
                );

                tutor.setFullName(
                        resultSet.getString("full_name")
                );

                tutor.setEmail(
                        resultSet.getString("email")
                );

                tutor.setPhone(
                        resultSet.getString("phone")
                );

                tutor.setRole(
                        resultSet.getString("role")
                );

                tutor.setQualifications(
                        resultSet.getString("qualifications")
                );

                tutor.setExperience(
                        resultSet.getInt("experience")
                );

                tutor.setHourlyRate(
                        resultSet.getDouble("hourly_rate")
                );

                tutor.setBio(
                        resultSet.getString("bio")
                );

                return tutor;
            }
        }
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
                SET qualifications = ?,
                    experience = ?,
                    hourly_rate = ?,
                    bio = ?
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, qualifications);
            statement.setInt(2, experience);
            statement.setDouble(3, hourlyRate);
            statement.setString(4, bio);
            statement.setInt(5, userId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException(
                        "No tutor profile exists for user ID: "
                                + userId
                );
            }
        }
    }
}
