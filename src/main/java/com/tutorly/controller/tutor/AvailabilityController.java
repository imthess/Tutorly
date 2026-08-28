package com.tutorly.controller.tutor;

import com.tutorly.model.Availability;
import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.service.AvailabilityService;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalTime;

public class AvailabilityController {

    private final TutorService tutorService = new TutorService();
    private final AvailabilityService availabilityService = new AvailabilityService();

    @FXML
    private ComboBox<String> dayComboBox;

    @FXML
    private ComboBox<String> startComboBox;

    @FXML
    private ComboBox<String> endComboBox;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TableView<Availability> availabilityTable;

    @FXML
    private TableColumn<Availability, String> dayColumn;

    @FXML
    private TableColumn<Availability, LocalTime> startColumn;

    @FXML
    private TableColumn<Availability, LocalTime> endColumn;

    @FXML
    private TableColumn<Availability, String> descriptionColumn;

    @FXML
    private TableColumn<Availability, String> statusColumn;

    @FXML
    private Label messageLabel;

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();

        if (user == null || !"tutor".equalsIgnoreCase(user.getRole())) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        dayComboBox.setItems(
                FXCollections.observableArrayList(
                        "Monday",
                        "Tuesday",
                        "Wednesday",
                        "Thursday",
                        "Friday",
                        "Saturday",
                        "Sunday"
                )
        );

        startComboBox.setItems(
                FXCollections.observableArrayList(
                        timeValues()
                )
        );

        endComboBox.setItems(
                FXCollections.observableArrayList(
                        timeValues()
                )
        );

        dayColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "dayOfWeek"
                )
        );

        startColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "startTime"
                )
        );

        endColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "endTime"
                )
        );

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );

        loadAvailability();
    }

    private String[] timeValues() {
        return new String[]{
                "08:00",
                "09:00",
                "10:00",
                "11:00",
                "12:00",
                "13:00",
                "14:00",
                "15:00",
                "16:00",
                "17:00",
                "18:00",
                "19:00",
                "20:00",
                "21:00",
                "22:00"
        };
    }

    private void loadAvailability() {
        try {
            User user = Session.getCurrentUser();
            if (user == null) {
                Navigator.navigate("/fxml/login.fxml");
                return;
            }

            Tutor tutor = tutorService.getTutorProfile(user.getUserId());

            if (tutor == null) {
                messageLabel.setText("Tutor profile not found.");
                return;
            }

            // FIX: Use tutor.getTutorId() (5) instead of user.getUserId() (9)
            availabilityTable.setItems(
                    FXCollections.observableArrayList(
                            availabilityService.getTutorAvailability(tutor.getTutorId())
                    )
            );

            messageLabel.setText(
                    availabilityTable.getItems().isEmpty()
                            ? "No availability added yet."
                            : "Your availability schedule."
            );

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load availability.");
        }
    }

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/tutor/dashboard.fxml");
    }

    @FXML
    public void handleAddAvailability(ActionEvent actionEvent) {
        String selectedDay = dayComboBox.getValue();
        String startTimeStr = startComboBox.getValue();
        String endTimeStr = endComboBox.getValue();
        String description = descriptionField.getText();

        if (selectedDay == null || startTimeStr == null || endTimeStr == null) {
            messageLabel.setText("Please select day, start time, and end time.");
            return;
        }

        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        if (!startTime.isBefore(endTime)) {
            messageLabel.setText("Start time must be before end time.");
            return;
        }

        User user = Session.getCurrentUser();
        if (user == null) {
            messageLabel.setText("User session expired. Please log in again.");
            return;
        }

        try {
            Tutor tutor = tutorService.getTutorProfile(user.getUserId());
            if (tutor == null) {
                messageLabel.setText("Tutor profile not found.");
                return;
            }

            // FIX: Pass tutor.getTutorId() (5) instead of user.getUserId() (9)
            availabilityService.addAvailability(
                    tutor.getTutorId(),
                    selectedDay,
                    startTime,
                    endTime,
                    description
            );

            messageLabel.setText("Availability added successfully!");

            dayComboBox.getSelectionModel().clearSelection();
            startComboBox.getSelectionModel().clearSelection();
            endComboBox.getSelectionModel().clearSelection();
            descriptionField.clear();

            loadAvailability();

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Database error occurred while saving availability.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to add availability: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteAvailability(ActionEvent actionEvent) {
        Availability selectedAvailability = availabilityTable.getSelectionModel().getSelectedItem();

        if (selectedAvailability == null) {
            messageLabel.setText("Please select an availability slot to delete.");
            return;
        }

        try {
            availabilityService.deleteAvailability(selectedAvailability.getAvailabilityId());

            messageLabel.setText("Availability slot deleted successfully!");
            loadAvailability();

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Database error occurred while deleting availability.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to delete availability: " + e.getMessage());
        }
    }

    @FXML
    public void handleToggleAvailability(ActionEvent actionEvent) {
        Availability selectedAvailability = availabilityTable.getSelectionModel().getSelectedItem();

        if (selectedAvailability == null) {
            messageLabel.setText("Please select an availability slot from the table.");
            return;
        }

        try {
            availabilityService.toggleAvailability(
                    selectedAvailability.getAvailabilityId(),
                    selectedAvailability.getStatus()
            );

            messageLabel.setText("Availability status updated successfully!");
            loadAvailability();

        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Database error occurred while updating status.");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Failed to update status: " + e.getMessage());
        }
    }
}