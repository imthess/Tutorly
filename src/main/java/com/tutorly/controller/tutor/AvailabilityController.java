package com.tutorly.controller.tutor;

import com.tutorly.model.Availability;
import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.repository.AvailabilityRepository;
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
import java.util.List;

public class AvailabilityController {

    private final TutorService tutorService = new TutorService();
    private final AvailabilityService availabilityService = new AvailabilityService();

    private Tutor currentTutor;
    private int editingAvailabilityId = -1;

    @FXML private ComboBox<String> dayComboBox;
    @FXML private ComboBox<AvailabilityRepository.SubjectOption> subjectComboBox;
    @FXML private ComboBox<String> startComboBox;
    @FXML private ComboBox<String> endComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TableView<Availability> availabilityTable;
    @FXML private TableColumn<Availability, String> dayColumn;
    @FXML private TableColumn<Availability, String> subjectColumn;
    @FXML private TableColumn<Availability, LocalTime> startColumn;
    @FXML private TableColumn<Availability, LocalTime> endColumn;
    @FXML private TableColumn<Availability, String> descriptionColumn;
    @FXML private TableColumn<Availability, String> statusColumn;
    @FXML private Label messageLabel;
    @FXML private Button saveButton;

    @FXML private TextField newSubjectField;
    @FXML private ListView<AvailabilityRepository.SubjectOption> assignedSubjectsList;
    @FXML private Label subjectMessageLabel;

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();
        if (user == null || !"tutor".equalsIgnoreCase(user.getRole())) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        dayComboBox.setItems(FXCollections.observableArrayList(
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        ));
        startComboBox.setItems(FXCollections.observableArrayList(timeValues()));
        endComboBox.setItems(FXCollections.observableArrayList(timeValues()));

        dayColumn.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        startColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadTutor();
    }

    private String[] timeValues() {
        return new String[]{
                "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00",
                "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00"
        };
    }

    private void loadTutor() {
        try {
            User user = Session.getCurrentUser();
            if (user == null) {
                Navigator.navigate("/fxml/login.fxml");
                return;
            }

            currentTutor = tutorService.getTutorProfile(user.getUserId());
            if (currentTutor == null) {
                messageLabel.setText("Tutor profile not found.");
                return;
            }

            loadSubjects();
            loadAvailability();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load tutor information.");
        }
    }

    private void loadSubjects() throws SQLException {
        List<AvailabilityRepository.SubjectOption> subjects =
                availabilityService.getTutorSubjects(currentTutor.getTutorId());

        subjectComboBox.setItems(FXCollections.observableArrayList(subjects));
        assignedSubjectsList.setItems(FXCollections.observableArrayList(subjects));

        if (!subjects.isEmpty()) {
            subjectMessageLabel.setText(subjects.size() + " subject(s) assigned to your profile.");
        } else {
            subjectMessageLabel.setText("No subjects assigned yet. Add your first subject below.");
        }
    }

    private void loadAvailability() {
        try {
            availabilityTable.setItems(FXCollections.observableArrayList(
                    availabilityService.getTutorAvailability(currentTutor.getTutorId())
            ));

            if (editingAvailabilityId == -1) {
                messageLabel.setText(
                        availabilityTable.getItems().isEmpty()
                                ? "No availability added yet."
                                : "Your availability schedule."
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load availability.");
        }
    }

    @FXML
    private void handleAddSubject() {
        if (currentTutor == null) return;

        try {
            String name = newSubjectField.getText();
            int subjectId = availabilityService.addTutorSubject(currentTutor.getTutorId(), name);
            newSubjectField.clear();
            loadSubjects();

            for (AvailabilityRepository.SubjectOption option : subjectComboBox.getItems()) {
                if (option.getId() == subjectId) {
                    subjectComboBox.setValue(option);
                    break;
                }
            }

            subjectMessageLabel.setText("Subject added successfully and is now available system-wide.");
        } catch (IllegalArgumentException e) {
            subjectMessageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            subjectMessageLabel.setText("Unable to save the subject. Please try again.");
        }
    }

    @FXML
    private void handleRemoveSubject() {
        if (currentTutor == null) return;

        AvailabilityRepository.SubjectOption selected =
                assignedSubjectsList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            subjectMessageLabel.setText("Select a subject to remove.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Subject");
        confirmation.setHeaderText("Remove " + selected.getName() + " from your subjects?");
        confirmation.setContentText("You will no longer be able to create new availability slots for this subject.");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        try {
            availabilityService.removeTutorSubject(currentTutor.getTutorId(), selected.getId());
            loadSubjects();
            subjectMessageLabel.setText("Subject removed from your tutor profile.");
        } catch (IllegalArgumentException e) {
            subjectMessageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            subjectMessageLabel.setText("Unable to remove the subject.");
        }
    }

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/tutor/dashboard.fxml");
    }

    @FXML
    public void handleSaveAvailability(ActionEvent event) {
        String selectedDay = dayComboBox.getValue();
        AvailabilityRepository.SubjectOption selectedSubject = subjectComboBox.getValue();
        String startTimeStr = startComboBox.getValue();
        String endTimeStr = endComboBox.getValue();
        String description = descriptionField.getText();

        if (selectedDay == null || selectedSubject == null || startTimeStr == null || endTimeStr == null) {
            messageLabel.setText("Please select day, subject, start time, and end time.");
            return;
        }

        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalTime endTime = LocalTime.parse(endTimeStr);

        try {
            if (editingAvailabilityId == -1) {
                availabilityService.addAvailability(
                        currentTutor.getTutorId(), selectedSubject.getId(), selectedDay,
                        startTime, endTime, description
                );
                messageLabel.setText("Availability added successfully.");
            } else {
                availabilityService.updateAvailability(
                        editingAvailabilityId, currentTutor.getTutorId(), selectedSubject.getId(),
                        selectedDay, startTime, endTime, description
                );
                messageLabel.setText("Availability updated successfully.");
            }

            clearForm();
            loadAvailability();
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Database error occurred while saving availability.");
        }
    }

    @FXML
    public void handleEditAvailability(ActionEvent event) {
        Availability selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Please select an availability slot to edit.");
            return;
        }

        editingAvailabilityId = selected.getAvailabilityId();
        dayComboBox.setValue(selected.getDayOfWeek());
        startComboBox.setValue(selected.getStartTime().toString());
        endComboBox.setValue(selected.getEndTime().toString());
        descriptionField.setText(selected.getDescription() == null ? "" : selected.getDescription());

        for (AvailabilityRepository.SubjectOption option : subjectComboBox.getItems()) {
            if (option.getId() == selected.getSubjectId()) {
                subjectComboBox.setValue(option);
                break;
            }
        }

        saveButton.setText("Update");
        messageLabel.setText("Editing selected availability slot.");
    }

    @FXML
    public void handleDeleteAvailability(ActionEvent event) {
        Availability selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Please select an availability slot to delete.");
            return;
        }

        try {
            availabilityService.deleteAvailability(selected.getAvailabilityId());
            if (editingAvailabilityId == selected.getAvailabilityId()) clearForm();
            loadAvailability();
            loadSubjects();
            messageLabel.setText("Availability slot deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Database error occurred while deleting availability.");
        }
    }

    @FXML
    public void handleToggleAvailability(ActionEvent event) {
        Availability selected = availabilityTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Please select an availability slot.");
            return;
        }

        try {
            availabilityService.toggleAvailability(selected.getAvailabilityId(), selected.getStatus());
            loadAvailability();
            messageLabel.setText("Availability status updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Database error occurred while updating status.");
        }
    }

    private void clearForm() {
        editingAvailabilityId = -1;
        dayComboBox.getSelectionModel().clearSelection();
        subjectComboBox.getSelectionModel().clearSelection();
        startComboBox.getSelectionModel().clearSelection();
        endComboBox.getSelectionModel().clearSelection();
        descriptionField.clear();
        saveButton.setText("Add Availability");
    }
}
