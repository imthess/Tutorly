package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRepository {

    public void createProfile(
            Connection connection,
            int userId
    ) throws SQLException {

        String sql = """
                INSERT INTO students (user_id)
                VALUES (?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }

    public Student findByUserId(int userId) throws SQLException {

        String sql = """
                SELECT
                    s.student_id,
                    s.user_id,
                    s.education,
                    s.institute,
                    u.full_name,
                    u.email,
                    u.phone,
                    u.role
                FROM students s
                JOIN users u ON s.user_id = u.user_id
                WHERE s.user_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                Student student = new Student();

                student.setStudentId(
                        resultSet.getInt("student_id")
                );

                student.setUserId(
                        resultSet.getInt("user_id")
                );

                student.setFullName(
                        resultSet.getString("full_name")
                );

                student.setEmail(
                        resultSet.getString("email")
                );

                student.setPhone(
                        resultSet.getString("phone")
                );

                student.setRole(
                        resultSet.getString("role")
                );

                student.setEducation(
                        resultSet.getString("education")
                );

                student.setInstitute(
                        resultSet.getString("institute")
                );

                return student;
            }
        }
    }
}
