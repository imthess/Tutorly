package com.tutorly.repository;

import com.tutorly.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for the global subject catalogue and tutor-to-subject assignments.
 *
 * Subjects are global records in the subjects table.  tutor_subjects stores
 * which subjects each tutor teaches.  This keeps one subject (for example,
 * "Java") visible to every user while still allowing each tutor to choose
 * their own teaching subjects.
 */
public class TutorSubjectRepository {

    public List<SubjectOption> findSubjectsByTutorId(int tutorId) throws SQLException {
        String sql = """
                SELECT DISTINCT s.subject_id, s.subject_name
                FROM subjects s
                LEFT JOIN tutor_subjects ts
                       ON ts.subject_id = s.subject_id
                      AND ts.tutor_id = ?
                LEFT JOIN availability a
                       ON a.subject_id = s.subject_id
                      AND a.tutor_id = ?
                WHERE ts.tutor_id IS NOT NULL
                   OR a.availability_id IS NOT NULL
                ORDER BY s.subject_name ASC
                """;

        List<SubjectOption> subjects = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tutorId);
            statement.setInt(2, tutorId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new SubjectOption(
                            rs.getInt("subject_id"),
                            rs.getString("subject_name")
                    ));
                }
            }
        }

        return subjects;
    }

    public List<SubjectOption> findAllSubjectOptions() throws SQLException {
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

    public List<String> findSubjectsByTutorIdAsNames(int tutorId) throws SQLException {
        List<String> result = new ArrayList<>();
        for (SubjectOption option : findSubjectsByTutorId(tutorId)) {
            result.add(option.getName());
        }
        return result;
    }

    public List<String> findAllSubjects() throws SQLException {
        List<String> subjects = new ArrayList<>();
        for (SubjectOption option : findAllSubjectOptions()) {
            subjects.add(option.getName());
        }
        return subjects;
    }

    /**
     * Creates the subject if it does not already exist, then assigns it to the tutor.
     * Subject matching is case-insensitive so Java, java and JAVA are one catalogue item.
     */
    public int addSubject(int tutorId, String subjectName) throws SQLException {
        if (tutorId <= 0) {
            throw new IllegalArgumentException("Invalid tutor ID.");
        }

        String cleanName = normalizeSubjectName(subjectName);
        int subjectId = findSubjectIdByName(cleanName);

        if (subjectId == -1) {
            String insertSql = "INSERT INTO subjects (subject_name) VALUES (?)";
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         insertSql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, cleanName);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Failed to create subject.");
                    }
                    subjectId = keys.getInt(1);
                }
            }
        }

        assignSubject(tutorId, subjectId);
        return subjectId;
    }

    public void removeSubject(int tutorId, int subjectId) throws SQLException {
        if (tutorId <= 0 || subjectId <= 0) {
            throw new IllegalArgumentException("Invalid tutor or subject.");
        }

        String availabilitySql = """
                SELECT COUNT(*)
                FROM availability
                WHERE tutor_id = ? AND subject_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(availabilitySql)) {
            statement.setInt(1, tutorId);
            statement.setInt(2, subjectId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException(
                            "This subject is used by one or more availability slots. " +
                            "Edit or delete those slots before removing the subject."
                    );
                }
            }
        }

        String deleteSql = "DELETE FROM tutor_subjects WHERE tutor_id = ? AND subject_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteSql)) {
            statement.setInt(1, tutorId);
            statement.setInt(2, subjectId);
            statement.executeUpdate();
        }
    }

    public boolean tutorTeachesSubject(int tutorId, int subjectId) throws SQLException {
        String sql = """
                SELECT EXISTS (
                    SELECT 1 FROM tutor_subjects
                    WHERE tutor_id = ? AND subject_id = ?
                    UNION ALL
                    SELECT 1 FROM availability
                    WHERE tutor_id = ? AND subject_id = ?
                    LIMIT 1
                )
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tutorId);
            statement.setInt(2, subjectId);
            statement.setInt(3, tutorId);
            statement.setInt(4, subjectId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }

    public int findSubjectIdByName(String subjectName) throws SQLException {
        String sql = "SELECT subject_id FROM subjects WHERE LOWER(TRIM(subject_name)) = LOWER(TRIM(?)) LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, subjectName);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt("subject_id") : -1;
            }
        }
    }

    private void assignSubject(int tutorId, int subjectId) throws SQLException {
        String sql = "INSERT IGNORE INTO tutor_subjects (tutor_id, subject_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, tutorId);
            statement.setInt(2, subjectId);
            statement.executeUpdate();
        }
    }

    private String normalizeSubjectName(String subjectName) {
        if (subjectName == null || subjectName.isBlank()) {
            throw new IllegalArgumentException("Subject name is required.");
        }

        String cleanName = subjectName.trim().replaceAll("\\s+", " ");
        if (cleanName.length() > 150) {
            throw new IllegalArgumentException("Subject name must be 150 characters or less.");
        }
        return cleanName;
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

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof SubjectOption option)) return false;
            return id == option.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }
}
