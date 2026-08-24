package com.tutorly.controller.tutor;

import com.tutorly.model.User;
import com.tutorly.patterns.facade.LiveClassFacade;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class OnlineClassController {

    @FXML
    private Label statusLabel;

    @FXML
    private Label cameraStatusLabel;

    @FXML
    private Label microphoneStatusLabel;

    @FXML
    private Label recordingStatusLabel;

    @FXML
    private Label whiteboardStatusLabel;

    private LiveClassFacade liveClassFacade;

    @FXML
    private void initialize() {

        User user = Session.getCurrentUser();

        if (user == null) {

            Navigator.navigate(
                    "/fxml/login.fxml"
            );

            return;
        }

        liveClassFacade =
                new LiveClassFacade(user);

        updateStatus();
    }

    @FXML
    private void handleStartClass() {

        liveClassFacade.startClass();

        statusLabel.setText(
                "Online class is running."
        );

        updateStatus();
    }

    @FXML
    private void handleEndClass() {

        liveClassFacade.endClass();

        statusLabel.setText(
                "Online class ended."
        );

        updateStatus();
    }

    @FXML
    private void handleCamera() {

        if (liveClassFacade.isCameraOn()) {

            liveClassFacade.stopCamera();

        } else {

            liveClassFacade.startCamera();
        }

        updateStatus();
    }

    @FXML
    private void handleMicrophone() {

        if (liveClassFacade.isMicrophoneOn()) {

            liveClassFacade.stopAudio();

        } else {

            liveClassFacade.startAudio();
        }

        updateStatus();
    }

    @FXML
    private void handleRecording() {

        if (liveClassFacade.isRecording()) {

            liveClassFacade.stopRecording();

        } else {

            liveClassFacade.startRecording();
        }

        updateStatus();
    }

    @FXML
    private void handleWhiteboard() {

        if (liveClassFacade.isWhiteboardOpen()) {

            liveClassFacade.closeWhiteboard();

        } else {

            liveClassFacade.openWhiteboard();
        }

        updateStatus();
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/tutor/dashboard.fxml"
        );
    }

    private void updateStatus() {

        statusLabel.setText(
                liveClassFacade.isClassRunning()
                        ? "Online class is running."
                        : "No class is currently running."
        );

        cameraStatusLabel.setText(
                liveClassFacade.isCameraOn()
                        ? "Camera: ON"
                        : "Camera: OFF"
        );

        microphoneStatusLabel.setText(
                liveClassFacade.isMicrophoneOn()
                        ? "Microphone: ON"
                        : "Microphone: OFF"
        );

        recordingStatusLabel.setText(
                liveClassFacade.isRecording()
                        ? "Recording: ON"
                        : "Recording: OFF"
        );

        whiteboardStatusLabel.setText(
                liveClassFacade.isWhiteboardOpen()
                        ? "Whiteboard: OPEN"
                        : "Whiteboard: CLOSED"
        );
    }
}
