package com.tutorly.live;

import com.tutorly.model.User;
import com.tutorly.patterns.facade.AudioService;
import com.tutorly.patterns.facade.LiveClassFacade;
import com.tutorly.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.net.URI;

/** Native Tutorly classroom UI with chat, shared whiteboard and real tutor media. */
public class LiveClassRoomController {
    @FXML private Label titleLabel;
    @FXML private Label connectionLabel;
    @FXML private Label participantsLabel;
    @FXML private Label cameraLabel;
    @FXML private Label microphoneLabel;
    @FXML private ImageView cameraView;
    @FXML private TextArea whiteboardArea;
    @FXML private TextArea chatArea;
    @FXML private TextField chatInput;
    @FXML private Button sendButton;
    @FXML private Button endButton;
    @FXML private Button whiteboardSendButton;
    @FXML private Button chatToggleButton;
    @FXML private Button whiteboardToggleButton;
    @FXML private TitledPane chatPane;
    @FXML private TitledPane whiteboardPane;

    private LiveClassClient client;
    private LiveMediaClient mediaClient;
    private LiveClassServer server;
    private LiveClassFacade facade;
    private boolean tutor;

    public void configureTutor(User user, LiveClassServer server, String meetingUrl) {
        this.tutor = true;
        this.server = server;
        this.facade = LiveClassSession.getFacade();
        titleLabel.setText("Tutorly Live Classroom — Tutor");
        connectionLabel.setText("Your classroom is live. Students can join now.");
        endButton.setVisible(true); endButton.setManaged(true);
        microphoneLabel.setText("Microphone: " + (facade != null && facade.isMicrophoneOn() ? "ON" : "OFF"));
        cameraLabel.setText("Camera: " + (facade != null && facade.isCameraOn() ? "ON" : "OFF"));
        if (facade != null) facade.setCameraFrameListener(this::showTutorFrame);
        connect(user, meetingUrl);
    }

    public void configureStudent(User user, String meetingUrl) {
        this.tutor = false;
        titleLabel.setText("Tutorly Live Classroom — Student");
        endButton.setVisible(false); endButton.setManaged(false);
        whiteboardArea.setEditable(false);
        whiteboardSendButton.setVisible(false); whiteboardSendButton.setManaged(false);
        microphoneLabel.setText("Microphone: tutor stream");
        cameraLabel.setText("Camera: tutor stream");
        connect(user, meetingUrl);
    }

    private void connect(User user, String meetingUrl) {
        try {
            URI uri = URI.create(meetingUrl);
            String host = uri.getHost();
            int port = uri.getPort();
            String path = uri.getPath();
            String token = path == null ? "" : path.replaceFirst("^/", "");
            if (host == null || port <= 0 || token.isBlank()) throw new IllegalArgumentException("Invalid Tutorly classroom address.");

            client = new LiveClassClient(host, port, token, tutor ? "tutor" : "student", user.getUserId());
            client.setChatListener(message -> chatArea.appendText(message + "\n"));
            client.setWhiteboardListener(content -> { if (!tutor) whiteboardArea.setText(content); });
            client.setParticipantsListener(names -> participantsLabel.setText("Participants: " + (names.isBlank() ? "None" : names.replace(",", ", "))));
            client.setSystemListener(message -> {
                connectionLabel.setText(message);
                if (!tutor) closeMediaAndReturn();
            });
            client.setErrorListener(connectionLabel::setText);
            client.connect();

            mediaClient = new LiveMediaClient(host, port + 1, port + 2, token, tutor ? "tutor" : "student", user.getUserId());
            mediaClient.setVideoListener(this::showTutorFrame);
            mediaClient.setAudioListener(this::playAudio);
            mediaClient.setErrorListener(message -> connectionLabel.setText(message));
            if (!tutor) mediaClient.connect();
            connectionLabel.setText("Connected to Tutorly classroom.");
        } catch (Exception e) {
            connectionLabel.setText("Unable to join classroom: " + e.getMessage());
            sendButton.setDisable(true);
        }
    }

    private void showTutorFrame(byte[] jpeg) {
        if (jpeg == null || jpeg.length == 0) return;
        cameraView.setImage(new Image(new ByteArrayInputStream(jpeg)));
    }

    private void playAudio(byte[] pcm) {
        // The Java Sound playback is created lazily by a dedicated helper.
        TutorAudioPlayback.play(pcm);
    }

    @FXML private void handleSendChat() {
        String text = chatInput.getText() == null ? "" : chatInput.getText().trim();
        if (text.isBlank() || client == null) return;
        client.sendChat(text); chatInput.clear();
    }

    @FXML private void handleSendWhiteboard() {
        if (!tutor || client == null) return;
        client.sendWhiteboard(whiteboardArea.getText());
    }

    @FXML private void handleToggleChat() {
        if (chatPane != null) chatPane.setExpanded(!chatPane.isExpanded());
    }

    @FXML private void handleToggleWhiteboard() {
        if (whiteboardPane != null) whiteboardPane.setExpanded(!whiteboardPane.isExpanded());
    }

    @FXML private void handleEndClass() {
        closeMedia();
        if (client != null) client.disconnect();
        if (tutor) LiveClassSession.end(); else Navigator.navigate("/fxml/student/bookings.fxml");
    }

    @FXML private void handleBack() {
        closeMedia();
        if (client != null) client.disconnect();
        if (tutor) Navigator.navigate("/fxml/tutor/online-class.fxml");
        else Navigator.navigate("/fxml/student/bookings.fxml");
    }

    private void closeMediaAndReturn() {
        closeMedia();
        Navigator.navigate("/fxml/student/bookings.fxml");
    }

    private void closeMedia() {
        if (mediaClient != null) { mediaClient.disconnect(); mediaClient = null; }
        TutorAudioPlayback.stop();
    }

    /** Minimal PCM playback for the tutor's microphone stream. */
    private static final class TutorAudioPlayback {
        private static javax.sound.sampled.SourceDataLine line;
        private static synchronized void play(byte[] pcm) {
            try {
                if (line == null) {
                    javax.sound.sampled.AudioFormat format = AudioService.getFormat();
                    line = (javax.sound.sampled.SourceDataLine) javax.sound.sampled.AudioSystem.getLine(new javax.sound.sampled.DataLine.Info(javax.sound.sampled.SourceDataLine.class, format));
                    line.open(format); line.start();
                }
                line.write(pcm, 0, pcm.length);
            } catch (Exception ignored) { }
        }
        private static synchronized void stop() {
            if (line != null) { try { line.drain(); } catch(Exception ignored){} try { line.stop(); } catch(Exception ignored){} try { line.close(); } catch(Exception ignored){} line=null; }
        }
    }
}
