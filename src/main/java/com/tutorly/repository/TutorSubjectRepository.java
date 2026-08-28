package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TutorSubjectRepository {

    public List<String> findSubjectsByTutorId(int tutorId) throws SQLException {
        String sql = "SELECT subject_name FROM subjects ORDER BY subject_name ASC";

        List<String> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                subjects.add(rs.getString("subject_name"));
            }
        }

        return subjects;
    }

    public List<SubjectOption> findSubjectOptionsByTutorId(int tutorId) throws SQLException {
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

    public List<String> findAllSubjects() throws SQLException {
        String sql = """
                SELECT subject_name
                FROM subjects
                ORDER BY subject_name
                """;

        List<String> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                subjects.add(rs.getString("subject_name"));
            }
        }

        return subjects;
    }

    public void addSubject(int tutorId, String subjectName) throws SQLException {
        // Global system: Subjects already exist globally
    }

    public void removeSubject(int tutorId, String subjectName) throws SQLException {
        // Global system: Subjects are shared and not removable per tutor
    }

    public static class SubjectOption {
        private final int id;
        private final String name;

        public SubjectOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
