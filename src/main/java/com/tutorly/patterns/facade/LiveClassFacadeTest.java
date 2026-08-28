package com.tutorly.patterns.facade;

import com.tutorly.model.User;

public class LiveClassFacadeTest {

    public static void main(String[] args) {

        System.out.println(
                "===== FACADE DESIGN PATTERN TEST ====="
        );

        // Create a test user
        User user = new User(
                "Test Student",
                "student@test.com",
                "password123",
                "01712345678",
                "student"
        );

        // Create the facade
        LiveClassFacade liveClass =
                new LiveClassFacade(
                        user,
                        "https://tutorly.com/live-class/101"
                );


        // TEST 1: Check initial status

        System.out.println("\n--- TEST 1: INITIAL STATUS ---");

        System.out.println(
                "Class Running: "
                        + liveClass.isClassRunning()
        );

        System.out.println(
                "Camera On: "
                        + liveClass.isCameraOn()
        );

        System.out.println(
                "Microphone On: "
                        + liveClass.isMicrophoneOn()
        );

        System.out.println(
                "Recording: "
                        + liveClass.isRecording()
        );

        System.out.println(
                "Whiteboard Open: "
                        + liveClass.isWhiteboardOpen()
        );


        // TEST 2: Start live class

        System.out.println("\n--- TEST 2: START LIVE CLASS ---");

        liveClass.startClass();

        System.out.println(
                "Class Running: "
                        + liveClass.isClassRunning()
        );

        System.out.println(
                "Camera On: "
                        + liveClass.isCameraOn()
        );

        System.out.println(
                "Microphone On: "
                        + liveClass.isMicrophoneOn()
        );


        // TEST 3: Open whiteboard

        System.out.println("\n--- TEST 3: OPEN WHITEBOARD ---");

        liveClass.openWhiteboard();

        System.out.println(
                "Whiteboard Open: "
                        + liveClass.isWhiteboardOpen()
        );


        // TEST 4: Start recording

        System.out.println("\n--- TEST 4: START RECORDING ---");

        liveClass.startRecording();

        System.out.println(
                "Recording: "
                        + liveClass.isRecording()
        );


        // TEST 5: Stop recording

        System.out.println("\n--- TEST 5: STOP RECORDING ---");

        liveClass.stopRecording();

        System.out.println(
                "Recording: "
                        + liveClass.isRecording()
        );


        // TEST 6: Camera control

        System.out.println("\n--- TEST 6: CAMERA CONTROL ---");

        liveClass.stopCamera();

        System.out.println(
                "Camera On After Stop: "
                        + liveClass.isCameraOn()
        );

        liveClass.startCamera();

        System.out.println(
                "Camera On After Start: "
                        + liveClass.isCameraOn()
        );


        // TEST 7: Audio control

        System.out.println("\n--- TEST 7: AUDIO CONTROL ---");

        liveClass.stopAudio();

        System.out.println(
                "Microphone After Stop: "
                        + liveClass.isMicrophoneOn()
        );

        liveClass.startAudio();

        System.out.println(
                "Microphone After Start: "
                        + liveClass.isMicrophoneOn()
        );


        // TEST 8: Close whiteboard

        System.out.println("\n--- TEST 8: CLOSE WHITEBOARD ---");

        liveClass.closeWhiteboard();

        System.out.println(
                "Whiteboard Open: "
                        + liveClass.isWhiteboardOpen()
        );


        // TEST 9: End live class

        System.out.println("\n--- TEST 9: END LIVE CLASS ---");

        liveClass.endClass();

        System.out.println(
                "Class Running: "
                        + liveClass.isClassRunning()
        );

        System.out.println(
                "Camera On: "
                        + liveClass.isCameraOn()
        );

        System.out.println(
                "Microphone On: "
                        + liveClass.isMicrophoneOn()
        );

        System.out.println(
                "Recording: "
                        + liveClass.isRecording()
        );

        System.out.println(
                "Whiteboard Open: "
                        + liveClass.isWhiteboardOpen()
        );


        System.out.println(
                "\n===== FACADE TEST COMPLETED ====="
        );
    }
}