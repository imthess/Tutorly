package com.tutorly.controller.student;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FindTutorsController {

    @FXML
    private TextField subjectField;

    @FXML
    private ListView<String> tutorList;

    @FXML
    private Label detailsLabel;

    @FXML
    private Label messageLabel;

    private final List<Tutor> tutors =
            new ArrayList<>();

    @FXML
    private void initialize() {

        User user = Session.getCurrentUser();

        if (user == null ||
                !"student".equalsIgnoreCase(user.getRole())) {

            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        loadTutors();
    }

    @FXML
    private void handleSearch() {

        loadTutors(
                subjectField.getText().trim()
        );
    }

    private void loadTutors() {

        loadTutors("");
    }

    private void loadTutors(String subject) {

        tutors.clear();

        String sql = """
                SELECT DISTINCT
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
                JOIN users u
                    ON t.user_id = u.user_id
                LEFT JOIN tutor_subjects ts
                    ON t.tutor_id = ts.tutor_id
                LEFT JOIN subjects s
                    ON ts.subject_id = s.subject_id
                WHERE
                    (? = '' OR s.subject_name LIKE ?)
                ORDER BY u.full_name
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            String pattern =
                    "%" + subject + "%";

            statement.setString(1, subject);
            statement.setString(2, pattern);

            try (ResultSet rs =
                         statement.executeQuery()) {

                while (rs.next()) {

                    Tutor tutor = new Tutor();

                    tutor.setTutorId(
                            rs.getInt("tutor_id")
                    );

                    tutor.setUserId(
                            rs.getInt("user_id")
                    );

                    tutor.setFullName(
                            rs.getString("full_name")
                    );

                    tutor.setEmail(
                            rs.getString("email")
                    );

                    tutor.setPhone(
                            rs.getString("phone")
                    );

                    tutor.setRole(
                            rs.getString("role")
                    );

                    tutor.setQualifications(
                            rs.getString("qualifications")
                    );

                    tutor.setExperience(
                            rs.getInt("experience")
                    );

                    tutor.setHourlyRate(
                            rs.getDouble("hourly_rate")
                    );

                    tutor.setBio(
                            rs.getString("bio")
                    );

                    tutors.add(tutor);
                }
            }

            tutorList.setItems(
                    FXCollections.observableArrayList(
                            tutors.stream()
                                    .map(Tutor::getFullName)
                                    .toList()
                    )
            );

        } catch (SQLException e) {

            messageLabel.setText(
                    "Unable to load tutors."
            );
        }
    }

    @FXML
    private void handleTutorSelected() {

        int index =
                tutorList.getSelectionModel()
                        .getSelectedIndex();

        if (index < 0 ||
                index >= tutors.size()) {

            return;
        }

        Tutor tutor =
                tutors.get(index);

        detailsLabel.setText(
                buildTutorDetails(tutor)
        );
    }

    private String buildTutorDetails(
            Tutor tutor
    ) {

        StringBuilder details =
                new StringBuilder();

        details.append(
                "Tutor: "
        ).append(
                tutor.getFullName()
        ).append("\n\n");

        details.append(
                "Qualifications: "
        ).append(
                safe(tutor.getQualifications())
        ).append("\n");

        details.append(
                "Experience: "
        ).append(
                tutor.getExperience()
        ).append(" years\n");

        details.append(
                "Hourly Rate: "
        ).append(
                tutor.getHourlyRate()
        ).append("\n\n");

        details.append(
                "Subjects: "
        ).append(
                getSubjects(tutor.getTutorId())
        ).append("\n\n");

        details.append(
                "Availability:\n"
        );

        details.append(
                getAvailability(tutor.getTutorId())
        ).append("\n");

        details.append(
                "\nAbout:\n"
        ).append(
                safe(tutor.getBio())
        );

        return details.toString();
    }

    private String getSubjects(int tutorId) {

        String sql = """
                SELECT s.subject_name
                FROM tutor_subjects ts
                JOIN subjects s
                    ON ts.subject_id = s.subject_id
                WHERE ts.tutor_id = ?
                ORDER BY s.subject_name
                """;

        List<String> subjects =
                new ArrayList<>();

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

        } catch (SQLException ignored) {
        }

        return subjects.isEmpty()
                ? "Not specified"
                : String.join(", ", subjects);
    }

    private String getAvailability(int tutorId) {

        String sql = """
                SELECT day_of_week,
                       start_time,
                       end_time,
                       description,
                       status
                FROM availability
                WHERE tutor_id = ?
                  AND status = 'Available'
                ORDER BY
                    FIELD(day_of_week,
                    'Monday','Tuesday','Wednesday',
                    'Thursday','Friday','Saturday','Sunday'),
                    start_time
                """;

        StringBuilder result =
                new StringBuilder();

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

                    result.append(
                            rs.getString("day_of_week")
                    ).append(" ")
                    .append(
                            rs.getTime("start_time")
                    ).append(" - ")
                    .append(
                            rs.getTime("end_time")
                    );

                    String description =
                            rs.getString("description");

                    if (description != null &&
                            !description.isBlank()) {

                        result.append(" | ")
                              .append(description.trim());
                    }

                    result.append("\n");
                }
            }

        } catch (SQLException ignored) {
        }

        if (result.isEmpty()) {
            return "No available schedule listed.";
        }

        return result.toString().trim();
    }

    private String safe(String value) {

        return value == null ||
                value.isBlank()
                ? "Not provided"
                : value;
    }

    @FXML
    private void handleBookTutor() {

        int index =
                tutorList.getSelectionModel()
                        .getSelectedIndex();

        if (index < 0 ||
                index >= tutors.size()) {

            messageLabel.setText(
                    "Select a tutor first."
            );

            return;
        }

        Navigator.navigate(
                "/fxml/student/create-booking.fxml"
        );
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/student/dashboard.fxml"
        );
    }
}
