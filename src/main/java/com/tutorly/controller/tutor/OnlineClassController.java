package com.tutorly.controller.tutor;

import com.tutorly.live.LiveClassRoom;
import com.tutorly.live.LiveClassServer;
import com.tutorly.live.LiveClassSession;
import com.tutorly.model.Booking;
import com.tutorly.model.OnlineClass;
import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.patterns.facade.LiveClassFacade;
import com.tutorly.repository.TutorSubjectRepository;
import com.tutorly.service.BookingService;
import com.tutorly.service.NotificationService;
import com.tutorly.service.OnlineClassService;
import com.tutorly.service.TutorService;
import com.tutorly.service.TutorSubjectService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/** Tutor view for choosing one of the tutor's remaining accepted class occurrences. */
public class OnlineClassController {
    @FXML private ListView<ClassOccurrence> classListView;
    @FXML private Label classInfoLabel;
    @FXML private Label statusLabel;
    @FXML private Label cameraStatusLabel;
    @FXML private Label microphoneStatusLabel;
    @FXML private Label recordingStatusLabel;
    @FXML private Label whiteboardStatusLabel;
    @FXML private Button startButton;
    @FXML private Button openButton;
    @FXML private Button endButton;

    private final TutorService tutorService = new TutorService();
    private final TutorSubjectService tutorSubjectService = new TutorSubjectService();
    private final BookingService bookingService = new BookingService();
    private final OnlineClassService onlineClassService = new OnlineClassService();
    private final NotificationService notificationService = new NotificationService();
    private Tutor tutor;
    private LiveClassFacade liveClassFacade;
    private LiveClassServer liveServer;
    private final List<Booking> activeBookings = new ArrayList<>();
    private ClassOccurrence activeOccurrence;
    private final Map<Integer, String> subjectNames = new HashMap<>();

    @FXML private void initialize() {
        try {
            User user = Session.getCurrentUser();
            if (user == null || !"tutor".equalsIgnoreCase(user.getRole())) { Navigator.navigate("/fxml/login.fxml"); return; }
            tutor = tutorService.getTutorProfile(user.getUserId());
            if (tutor == null) { setStatus("Tutor profile not found."); return; }

            if (LiveClassSession.isActive()) {
                liveServer = LiveClassSession.getServer();
                liveClassFacade = LiveClassSession.getFacade();
            }

            for (TutorSubjectRepository.SubjectOption option : tutorSubjectService.getTutorSubjectOptions(tutor.getTutorId()))
                subjectNames.put(option.getId(), option.getName());

            classListView.setPlaceholder(new Label("No remaining accepted classes."));
            classListView.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> updateSelection(selected));
            loadRemainingClasses();
            updateStatus();
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Unable to load remaining classes.");
        }
    }

    /** Remaining means accepted bookings whose occurrence has not been completed. */
    private void loadRemainingClasses() {
        try {
            List<Booking> all = bookingService.getTutorBookings(tutor.getTutorId());
            Map<String, List<Booking>> grouped = new TreeMap<>();
            for (Booking b : all) {
                if (!"Accepted".equalsIgnoreCase(b.getStatus()) || b.getBookingDate() == null || b.getBookingTime() == null) continue;
                String key = b.getSubjectId() + "|" + b.getBookingDate() + "|" + b.getBookingTime();
                grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(b);
            }

            List<ClassOccurrence> occurrences = new ArrayList<>();
            for (List<Booking> group : grouped.values()) {
                Booking first = group.get(0);
                OnlineClass onlineClass = onlineClassService.findByBookingId(first.getBookingId());
                if (onlineClass != null && "Completed".equalsIgnoreCase(onlineClass.getStatus())) continue;
                occurrences.add(new ClassOccurrence(
                        first.getSubjectId(), subjectNames.getOrDefault(first.getSubjectId(), "Subject " + first.getSubjectId()),
                        first.getBookingDate(), first.getBookingTime(), first.getDuration(), group.size(), onlineClass));
            }

            occurrences.sort(Comparator.comparing(ClassOccurrence::date).thenComparing(ClassOccurrence::time).thenComparing(ClassOccurrence::subjectName));
            classListView.setItems(FXCollections.observableArrayList(occurrences));
            if (occurrences.isEmpty()) {
                activeOccurrence = null; activeBookings.clear();
                classInfoLabel.setText("No remaining accepted classes are available to take.");
                setButtons(false, false, false);
            } else {
                classListView.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Unable to load remaining classes.");
        }
    }

    private void updateSelection(ClassOccurrence occurrence) {
        try {
            activeOccurrence = occurrence;
            activeBookings.clear();
            if (occurrence == null) { setButtons(false, false, false); return; }
            for (Booking b : bookingService.getTutorBookings(tutor.getTutorId())) {
                if ("Accepted".equalsIgnoreCase(b.getStatus()) && b.getSubjectId() == occurrence.subjectId
                        && occurrence.date.equals(b.getBookingDate()) && occurrence.time.equals(b.getBookingTime())) activeBookings.add(b);
            }
            if (activeBookings.isEmpty()) { setStatus("No accepted students remain for this class occurrence."); setButtons(false,false,false); return; }
            OnlineClass c = onlineClassService.findByBookingId(activeBookings.get(0).getBookingId());
            activeOccurrence.onlineClass = c;
            boolean running = onlineClassService.isRunning(c);
            boolean completed = c != null && "Completed".equalsIgnoreCase(c.getStatus());
            classInfoLabel.setText(occurrence.display() + "  |  " + activeBookings.size() + " accepted student(s)" + (running ? "  |  LIVE" : completed ? "  |  COMPLETED" : "  |  READY"));
            setButtons(!completed && !running, running, running);
        } catch (Exception e) {
            e.printStackTrace();
            setStatus("Unable to select the class.");
            setButtons(false,false,false);
        }
    }

    @FXML private void handleStartClass() {
        if (activeOccurrence == null || activeBookings.isEmpty()) { setStatus("Select a remaining class first."); return; }
        try {
            OnlineClass existing = activeOccurrence.onlineClass;
            if (existing != null && "Completed".equalsIgnoreCase(existing.getStatus())) { setStatus("This class has already been completed."); return; }
            if (existing != null && onlineClassService.isRunning(existing)) { setStatus("This class is already running."); return; }

            Set<Integer> allowedStudents = new HashSet<>();
            for (Booking b : activeBookings) allowedStudents.add(bookingService.getStudentUserIdForBooking(b.getBookingId()));

            String token = UUID.randomUUID().toString().replace("-", "");
            liveServer = LiveClassServer.start(token, Session.getCurrentUser().getUserId(), allowedStudents);
            String meetingLink = "tutorly://" + liveServer.getConnectHost() + ":" + liveServer.getPort() + "/" + token;
            for (Booking b : activeBookings) onlineClassService.startClassWithMeetingLink(b.getBookingId(), meetingLink, java.time.LocalDateTime.now());
            for (Booking b : activeBookings) {
                notificationService.sendNotification(
                        bookingService.getStudentUserIdForBooking(b.getBookingId()),
                        "Your Tutorly live class has started for " + activeOccurrence.display() + ". Open My Bookings and choose Join Online Class.",
                        "Class");
            }

            User currentUser = Session.getCurrentUser();
            liveClassFacade = new LiveClassFacade(currentUser, liveServer, meetingLink);
            LiveClassSession.start(liveServer, meetingLink, liveClassFacade, this::completeCurrentClass);
            liveClassFacade.startClass();

            String mediaWarning = "";
            if (!liveClassFacade.isCameraOn()) mediaWarning += " Camera could not be opened.";
            if (!liveClassFacade.isMicrophoneOn()) mediaWarning += " Microphone could not be opened.";
            setStatus("Live class started inside Tutorly." + mediaWarning);
            updateStatus();
            setButtons(false, true, true);
        } catch (Exception e) {
            e.printStackTrace();
            if (liveServer != null) { liveServer.stop(); liveServer = null; }
            LiveClassSession.clear();
            setStatus("Unable to start the live class: " + e.getMessage());
        }
    }

    @FXML private void handleOpenClassroom() {
        try {
            if (liveServer == null || !liveServer.isRunning() || activeOccurrence == null) { setStatus("No live class is currently running."); return; }
            String link = activeOccurrence.onlineClass == null ? LiveClassSession.getMeetingUrl() : activeOccurrence.onlineClass.getMeetingLink();
            if (link == null || link.isBlank()) { setStatus("The classroom address is unavailable."); return; }
            liveClassFacade = LiveClassSession.getFacade();
            LiveClassRoom.openTutor(Session.getCurrentUser(), liveServer, link);
        } catch (Exception e) { e.printStackTrace(); setStatus("Unable to open the classroom."); }
    }

    @FXML private void handleEndClass() { completeCurrentClass(); }

    public void finishLiveClass() { completeCurrentClass(); }

    private void completeCurrentClass() {
        if (activeBookings.isEmpty()) { setStatus("No active class is selected."); return; }
        try {
            if (liveClassFacade != null) liveClassFacade.endClass();
            if (liveServer != null) { liveServer.stop(); liveServer = null; }
            for (Booking b : new ArrayList<>(activeBookings)) {
                OnlineClass c = onlineClassService.findByBookingId(b.getBookingId());
                if (onlineClassService.isRunning(c)) onlineClassService.endClass(c.getClassId());
                if ("Accepted".equalsIgnoreCase(b.getStatus())) bookingService.completeBooking(b.getBookingId());
            }
            LiveClassSession.clear();
            activeBookings.clear(); activeOccurrence = null; liveClassFacade = null;
            setStatus("Live class ended and completed.");
            loadRemainingClasses(); updateStatus();
        } catch (Exception e) { e.printStackTrace(); setStatus("Unable to complete the live class: " + e.getMessage()); }
    }

    @FXML private void handleCamera() {
        try { if (liveClassFacade==null || !liveClassFacade.isClassRunning()) { setStatus("Start a live class first."); return; } if(liveClassFacade.isCameraOn()) liveClassFacade.stopCamera(); else liveClassFacade.startCamera(); updateStatus(); }
        catch(Exception e){ setStatus("Unable to change camera: " + e.getMessage()); }
    }
    @FXML private void handleMicrophone() {
        try { if (liveClassFacade==null || !liveClassFacade.isClassRunning()) { setStatus("Start a live class first."); return; } if(liveClassFacade.isMicrophoneOn()) liveClassFacade.stopAudio(); else liveClassFacade.startAudio(); updateStatus(); }
        catch(Exception e){ setStatus("Unable to change microphone: " + e.getMessage()); }
    }
    @FXML private void handleRecording() { try { if(liveClassFacade==null || !liveClassFacade.isClassRunning()){setStatus("Start a live class first.");return;} if(liveClassFacade.isRecording())liveClassFacade.stopRecording();else liveClassFacade.startRecording();updateStatus(); }catch(Exception e){setStatus("Unable to change recording state.");} }
    @FXML private void handleWhiteboard() { try { if(liveClassFacade==null || !liveClassFacade.isClassRunning()){setStatus("Start a live class first.");return;} if(liveClassFacade.isWhiteboardOpen())liveClassFacade.closeWhiteboard();else liveClassFacade.openWhiteboard();updateStatus(); }catch(Exception e){setStatus("Unable to change whiteboard state.");} }
    @FXML private void handleBack() { Navigator.navigate("/fxml/tutor/dashboard.fxml"); }

    private void setButtons(boolean start, boolean open, boolean end) { startButton.setDisable(!start); openButton.setDisable(!open); endButton.setDisable(!end); }
    private void updateStatus() {
        boolean r=liveClassFacade!=null&&liveClassFacade.isClassRunning();
        statusLabel.setText(r?"Tutorly live class is running.":"No class is currently running.");
        cameraStatusLabel.setText("Camera: "+(liveClassFacade!=null&&liveClassFacade.isCameraOn()?"ON":"OFF"));
        microphoneStatusLabel.setText("Microphone: "+(liveClassFacade!=null&&liveClassFacade.isMicrophoneOn()?"ON":"OFF"));
        recordingStatusLabel.setText("Recording: "+(liveClassFacade!=null&&liveClassFacade.isRecording()?"ON":"OFF"));
        whiteboardStatusLabel.setText("Whiteboard: "+(liveClassFacade!=null&&liveClassFacade.isWhiteboardOpen()?"OPEN":"CLOSED"));
    }
    private void setStatus(String s) { if(statusLabel!=null) statusLabel.setText(s); }

    public static final class ClassOccurrence {
        private final int subjectId; private final String subjectName; private final LocalDate date; private final LocalTime time; private final int duration; private final int students; private OnlineClass onlineClass;
        ClassOccurrence(int subjectId, String subjectName, LocalDate date, LocalTime time, int duration, int students, OnlineClass onlineClass) { this.subjectId=subjectId;this.subjectName=subjectName;this.date=date;this.time=time;this.duration=duration;this.students=students;this.onlineClass=onlineClass; }
        LocalDate date(){return date;} LocalTime time(){return time;} String subjectName(){return subjectName;}
        String display() { return String.format("%s — %s at %s (%d min)", subjectName, date, time, duration); }
        @Override public String toString() {
            String state = onlineClass != null && "Scheduled".equalsIgnoreCase(onlineClass.getStatus()) ? ""
                    : onlineClass != null && "Completed".equalsIgnoreCase(onlineClass.getStatus()) ? "  |  COMPLETED"
                    : onlineClass != null && "Cancelled".equalsIgnoreCase(onlineClass.getStatus()) ? "  |  CANCELLED"
                    : onlineClass != null ? "  |  LIVE" : "";
            return String.format("%s  |  %d accepted student(s)%s", display(), students, state);
        }
    }
}
