package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TutorSubjectRepository {

    public List<String> findSubjectsByTutorId(
            int tutorId
    ) throws SQLException {

        String sql = """
                SELECT s.subject_name
                FROM tutor_subjects ts
                JOIN subjects s
                    ON ts.subject_id = s.subject_id
                WHERE ts.tutor_id = ?
                ORDER BY s.subject_name
                """;

        List<String> subjects = new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, tutorId);

            try (ResultSet rs =
                         statement.executeQuery()) {

                while (rs.next()) {
                    subjects.add(
                            rs.getString("subject_name")
                    );
                }
            }
        }

        return subjects;
    }

    public List<String> findAllSubjects()
            throws SQLException {

        String sql = """
                SELECT subject_name
                FROM subjects
                ORDER BY subject_name
                """;

        List<String> subjects = new ArrayList<>();

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql);
                ResultSet rs =
                        statement.executeQuery()
        ) {

            while (rs.next()) {
                subjects.add(
                        rs.getString("subject_name")
                );
            }
        }

        return subjects;
    }

    public void addSubject(
            int tutorId,
            String subjectName
    ) throws SQLException {

        String findSql = """
                SELECT subject_id
                FROM subjects
                WHERE subject_name = ?
                """;

        String insertSql = """
                INSERT INTO tutor_subjects
                (tutor_id, subject_id)
                VALUES (?, ?)
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement find =
                        connection.prepareStatement(findSql)
        ) {

            find.setString(1, subjectName);

            try (ResultSet rs =
                         find.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Subject not found."
                    );
                }

                int subjectId =
                        rs.getInt("subject_id");

                try (PreparedStatement insert =
                             connection.prepareStatement(
                                     insertSql
                             )) {

                    insert.setInt(1, tutorId);
                    insert.setInt(2, subjectId);
                    insert.executeUpdate();
                }
            }
        }
    }

    public void removeSubject(
            int tutorId,
            String subjectName
    ) throws SQLException {

        String sql = """
                DELETE ts
                FROM tutor_subjects ts
                JOIN subjects s
                    ON ts.subject_id = s.subject_id
                WHERE ts.tutor_id = ?
                  AND s.subject_name = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, tutorId);
            statement.setString(2, subjectName);

            statement.executeUpdate();
        }
    }
}
