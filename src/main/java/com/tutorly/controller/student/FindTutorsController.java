package com.tutorly.controller.student;

import com.tutorly.model.Availability;
import com.tutorly.model.Student;
import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.repository.AvailabilityRepository;
import com.tutorly.service.AvailabilityService;
import com.tutorly.service.BookingService;
import com.tutorly.service.StudentService;
import com.tutorly.service.TutorService;
import com.tutorly.service.TutorSubjectService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindTutorsController {

    @FXML private TextField keywordField;
    @FXML private ComboBox<AvailabilityRepository.SubjectOption> subjectComboBox;
    @FXML private TextField minRateField;
    @FXML private TextField maxRateField;
    @FXML private TextField minExperienceField;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private VBox tutorCardsBox;
    @FXML private Label resultCountLabel;
    @FXML private Label messageLabel;

    @FXML private Label selectedTutorNameLabel;
    @FXML private Label selectedTutorMetaLabel;
    @FXML private Label selectedTutorSubjectsLabel;
    @FXML private Label selectedTutorBioLabel;
    @FXML private VBox availabilityBox;
    @FXML private DatePicker startDatePicker;
    @FXML private Spinner<Integer> classesSpinner;
    @FXML private Label bookingSummaryLabel;
    @FXML private Button requestButton;

    private final TutorService tutorService = new TutorService();
    private final StudentService studentService = new StudentService();
    private final AvailabilityService availabilityService = new AvailabilityService();
    private final BookingService bookingService = new BookingService();
    private final AvailabilityRepository availabilityRepository = new AvailabilityRepository();
    private final TutorSubjectService tutorSubjectService = new TutorSubjectService();

    private final List<Tutor> tutors = new ArrayList<>();
    private final Map<CheckBox, Availability> slotSelections = new HashMap<>();
    private Tutor selectedTutor;

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a");

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();

        if (user == null || !"student".equalsIgnoreCase(user.getRole())) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        setupFilters();
        setupBookingControls();
        clearTutorDetails();
        loadTutors();
    }

    private void setupFilters() {
        try {
            List<AvailabilityRepository.SubjectOption> subjects =
                    new ArrayList<>();
            subjects.add(new AvailabilityRepository.SubjectOption(0, "All subjects"));
            subjects.addAll(availabilityRepository.findAllSubjects());
            subjectComboBox.setItems(FXCollections.observableArrayList(subjects));
            subjectComboBox.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            subjectComboBox.setItems(FXCollections.observableArrayList(
                    new AvailabilityRepository.SubjectOption(0, "All subjects")
            ));
            subjectComboBox.getSelectionModel().selectFirst();
        }

        dayComboBox.setItems(FXCollections.observableArrayList(
                "Any day", "Monday", "Tuesday", "Wednesday",
                "Thursday", "Friday", "Saturday", "Sunday"
        ));
        dayComboBox.getSelectionModel().selectFirst();
        subjectComboBox.setOnShowing(event -> refreshSubjectFilter());
    }

    private void setupBookingControls() {
        startDatePicker.setValue(LocalDate.now().plusDays(1));
        classesSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 52, 10)
        );
        requestButton.setDisable(true);
        bookingSummaryLabel.setText("Select a tutor and one or more weekly slots.");
    }

    private void refreshSubjectFilter() {
        try {
            int selectedId = subjectComboBox.getValue() == null ? 0 : subjectComboBox.getValue().getId();
            List<AvailabilityRepository.SubjectOption> subjects = new ArrayList<>();
            subjects.add(new AvailabilityRepository.SubjectOption(0, "All subjects"));
            subjects.addAll(availabilityRepository.findAllSubjects());
            subjectComboBox.setItems(FXCollections.observableArrayList(subjects));
            subjects.stream().filter(option -> option.getId() == selectedId).findFirst()
                    .ifPresentOrElse(subjectComboBox::setValue, () -> subjectComboBox.getSelectionModel().selectFirst());
        } catch (SQLException e) {
            messageLabel.setText("Unable to refresh the subject catalogue.");
        }
    }

    @FXML
    private void handleSearch() {
        refreshSubjectFilter();
        loadTutors();
    }

    @FXML
    private void handleClearFilters() {
        keywordField.clear();
        minRateField.clear();
        maxRateField.clear();
        minExperienceField.clear();
        subjectComboBox.getSelectionModel().selectFirst();
        dayComboBox.getSelectionModel().selectFirst();
        loadTutors();
    }

    private void loadTutors() {
        try {
            Double minRate = parseOptionalDouble(minRateField.getText(), "Minimum hourly rate");
            Double maxRate = parseOptionalDouble(maxRateField.getText(), "Maximum hourly rate");
            Integer minExperience = parseOptionalInteger(minExperienceField.getText(), "Minimum experience");

            if (minRate != null && maxRate != null && minRate > maxRate) {
                messageLabel.setText("Minimum rate cannot be greater than maximum rate.");
                return;
            }

            int subjectId = subjectComboBox.getValue() == null
                    ? 0
                    : subjectComboBox.getValue().getId();

            String day = dayComboBox.getValue();
            if ("Any day".equals(day)) {
                day = "";
            }

            tutors.clear();
            tutors.addAll(tutorService.searchTutors(
                    keywordField.getText().trim(),
                    subjectId,
                    minRate,
                    maxRate,
                    minExperience,
                    day
            ));

            renderTutorCards();
            resultCountLabel.setText(tutors.size() + (tutors.size() == 1 ? " tutor found" : " tutors found"));

            if (tutors.isEmpty()) {
                messageLabel.setText("No tutors match these filters.");
                clearTutorDetails();
            } else {
                messageLabel.setText("Choose a tutor to view details and request classes.");
                if (selectedTutor == null || tutors.stream().noneMatch(t -> t.getTutorId() == selectedTutor.getTutorId())) {
                    showTutor(tutors.get(0));
                } else {
                    showTutor(selectedTutor);
                }
            }
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to search tutors.");
        }
    }

    private void renderTutorCards() {
        tutorCardsBox.getChildren().clear();

        for (Tutor tutor : tutors) {
            VBox card = new VBox(8);
            card.getStyleClass().add("tutor-result-card");
            card.setPadding(new Insets(16));
            card.setMaxWidth(Double.MAX_VALUE);

            Label name = new Label(safe(tutor.getFullName(), "Unnamed tutor"));
            name.getStyleClass().add("tutor-card-name");

            Label qualification = new Label(safe(tutor.getQualifications(), "Qualifications not provided"));
            qualification.getStyleClass().add("tutor-card-subtitle");
            qualification.setWrapText(true);

            Label stats = new Label(
                    tutor.getExperience() + " years experience   •   " +
                    formatRate(tutor.getHourlyRate())
            );
            stats.getStyleClass().add("tutor-card-stats");

            Label subjects = new Label("Subjects: " + getTutorSubjects(tutor.getTutorId()));
            subjects.getStyleClass().add("tutor-card-detail");
            subjects.setWrapText(true);

            Label bio = new Label(truncate(safe(tutor.getBio(), "No bio provided"), 150));
            bio.getStyleClass().add("tutor-card-detail");
            bio.setWrapText(true);

            HBox actions = new HBox(10);
            actions.setAlignment(Pos.CENTER_RIGHT);

            Button detailsButton = new Button("View Details");
            detailsButton.getStyleClass().add("secondary-button");
            detailsButton.setOnAction(e -> showTutor(tutor));

            Button selectButton = new Button("Select Tutor");
            selectButton.getStyleClass().add("primary-button");
            selectButton.setOnAction(e -> showTutor(tutor));

            actions.getChildren().addAll(detailsButton, selectButton);
            card.getChildren().addAll(name, qualification, stats, subjects, bio, actions);
            card.setOnMouseClicked(e -> showTutor(tutor));

            tutorCardsBox.getChildren().add(card);
        }
    }

    private void showTutor(Tutor tutor) {
        selectedTutor = tutor;
        selectedTutorNameLabel.setText(safe(tutor.getFullName(), "Tutor"));
        selectedTutorMetaLabel.setText(
                safe(tutor.getQualifications(), "Qualifications not provided") +
                "\n" + tutor.getExperience() + " years experience  •  " +
                formatRate(tutor.getHourlyRate())
        );
        selectedTutorSubjectsLabel.setText("Subjects: " + getTutorSubjects(tutor.getTutorId()));
        selectedTutorBioLabel.setText(safe(tutor.getBio(), "No biography provided."));

        loadAvailability(tutor.getTutorId());
    }

    private void loadAvailability(int tutorId) {
        availabilityBox.getChildren().clear();
        slotSelections.clear();
        requestButton.setDisable(true);

        try {
            List<Availability> availability = availabilityService.getTutorAvailability(tutorId).stream()
                    .filter(a -> "Available".equalsIgnoreCase(a.getStatus()))
                    .sorted(Comparator.comparingInt(a -> dayNumber(a.getDayOfWeek())))
                    .toList();

            if (availability.isEmpty()) {
                availabilityBox.getChildren().add(new Label("No available weekly slots listed."));
                bookingSummaryLabel.setText("This tutor has no selectable slots right now.");
                return;
            }

            for (Availability slot : availability) {
                CheckBox checkBox = new CheckBox(formatSlot(slot));
                checkBox.setWrapText(true);
                checkBox.getStyleClass().add("availability-check");
                checkBox.setMaxWidth(Double.MAX_VALUE);
                checkBox.setOnAction(e -> updateBookingSummary());
                slotSelections.put(checkBox, slot);
                availabilityBox.getChildren().add(checkBox);
            }

            bookingSummaryLabel.setText("Select one or more weekly slots, then choose how many classes to request per slot.");
        } catch (SQLException e) {
            e.printStackTrace();
            availabilityBox.getChildren().add(new Label("Unable to load tutor availability."));
        }
    }

    private void updateBookingSummary() {
        List<Availability> selected = selectedSlots();
        requestButton.setDisable(selected.isEmpty());

        if (selected.isEmpty()) {
            bookingSummaryLabel.setText("Select one or more weekly slots.");
            return;
        }

        int classes = classesSpinner.getValue();
        bookingSummaryLabel.setText(
                selected.size() + " weekly slot(s) selected • " +
                classes + " class(es) per selected slot • " +
                (selected.size() * classes) + " total booking request(s)"
        );
    }

    @FXML
    private void handleRequestClasses() {
        if (selectedTutor == null) {
            messageLabel.setText("Select a tutor first.");
            return;
        }

        List<Availability> selected = selectedSlots();
        if (selected.isEmpty()) {
            messageLabel.setText("Select at least one available weekly slot.");
            return;
        }

        LocalDate startDate = startDatePicker.getValue();
        if (startDate == null) {
            messageLabel.setText("Choose a start date.");
            return;
        }

        if (startDate.isBefore(LocalDate.now())) {
            messageLabel.setText("Start date cannot be in the past.");
            return;
        }

        try {
            Student student = studentService.getStudentProfile(
                    Session.getCurrentUser().getUserId()
            );

            if (student == null) {
                messageLabel.setText("Student profile not found.");
                return;
            }

            int classesPerSlot = classesSpinner.getValue();
            List<String> clashes = validateWeeklySelections(selected);
            if (!clashes.isEmpty()) {
                messageLabel.setText("Weekly slot clash: " + String.join("; ", clashes));
                return;
            }

            int total = bookingService.createRecurringBookings(
                    student.getStudentId(),
                    selectedTutor.getTutorId(),
                    selected,
                    startDate,
                    classesPerSlot
            );

            messageLabel.setText(
                    "Request submitted successfully: " + total +
                    " class requests sent to " + selectedTutor.getFullName() + "."
            );
            clearSelectionsAfterBooking();
        } catch (IllegalArgumentException e) {
            messageLabel.setText(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to submit the class request.");
        }
    }

    private List<String> validateWeeklySelections(List<Availability> selected) {
        List<String> clashes = new ArrayList<>();

        for (int i = 0; i < selected.size(); i++) {
            for (int j = i + 1; j < selected.size(); j++) {
                Availability a = selected.get(i);
                Availability b = selected.get(j);

                if (!a.getDayOfWeek().equalsIgnoreCase(b.getDayOfWeek())) {
                    continue;
                }

                if (overlaps(a.getStartTime(), a.getEndTime(), b.getStartTime(), b.getEndTime())) {
                    clashes.add(
                            a.getDayOfWeek() + " " +
                            formatTime(a.getStartTime()) + "–" + formatTime(a.getEndTime()) +
                            " overlaps " + formatTime(b.getStartTime()) + "–" + formatTime(b.getEndTime())
                    );
                }
            }
        }

        return clashes;
    }

    private boolean overlaps(LocalTime startA, LocalTime endA, LocalTime startB, LocalTime endB) {
        return startA.isBefore(endB) && startB.isBefore(endA);
    }

    private void clearSelectionsAfterBooking() {
        slotSelections.keySet().forEach(checkBox -> checkBox.setSelected(false));
        updateBookingSummary();
    }

    private List<Availability> selectedSlots() {
        List<Availability> selected = new ArrayList<>();
        for (Map.Entry<CheckBox, Availability> entry : slotSelections.entrySet()) {
            if (entry.getKey().isSelected()) {
                selected.add(entry.getValue());
            }
        }
        selected.sort(Comparator.comparingInt(a -> dayNumber(a.getDayOfWeek())));
        return selected;
    }

    private String getTutorSubjects(int tutorId) {
        try {
            return tutorSubjectService.getTutorSubjects(tutorId).stream()
                    .filter(name -> name != null && !name.isBlank())
                    .distinct()
                    .sorted()
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Not specified");
        } catch (SQLException e) {
            return "Not specified";
        }
    }

    private int dayNumber(String day) {
        try {
            return DayOfWeek.valueOf(day.toUpperCase()).getValue();
        } catch (Exception e) {
            return 8;
        }
    }

    private String formatSlot(Availability slot) {
        StringBuilder text = new StringBuilder();
        text.append(slot.getDayOfWeek())
                .append("  •  ")
                .append(formatTime(slot.getStartTime()))
                .append(" – ")
                .append(formatTime(slot.getEndTime()))
                .append("  •  ")
                .append(safe(slot.getSubjectName(), "Subject not specified"));

        if (slot.getDescription() != null && !slot.getDescription().isBlank()) {
            text.append("\n").append(slot.getDescription().trim());
        }

        return text.toString();
    }

    private String formatTime(LocalTime time) {
        return time == null ? "N/A" : time.format(TIME_FORMAT);
    }

    private String formatRate(double rate) {
        return String.format("%.2f BDT/hour", rate);
    }

    private String truncate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength - 3) + "...";
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private Double parseOptionalDouble(String text, String fieldName) {
        if (text == null || text.isBlank()) return null;
        try {
            double value = Double.parseDouble(text.trim());
            if (value < 0) throw new IllegalArgumentException(fieldName + " cannot be negative.");
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private Integer parseOptionalInteger(String text, String fieldName) {
        if (text == null || text.isBlank()) return null;
        try {
            int value = Integer.parseInt(text.trim());
            if (value < 0) throw new IllegalArgumentException(fieldName + " cannot be negative.");
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a whole number.");
        }
    }

    private void clearTutorDetails() {
        selectedTutor = null;
        selectedTutorNameLabel.setText("Select a tutor");
        selectedTutorMetaLabel.setText("Tutor details will appear here.");
        selectedTutorSubjectsLabel.setText("Subjects: —");
        selectedTutorBioLabel.setText("—");
        availabilityBox.getChildren().clear();
        slotSelections.clear();
        requestButton.setDisable(true);
        bookingSummaryLabel.setText("Select a tutor and one or more weekly slots.");
    }

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/student/dashboard.fxml");
    }
}
