package com.tutorly.patterns.facade;

/**
 * Subsystem responsible for class recording.
 */
public class RecordingService {

    private boolean recording;

    public void startRecording() {

        if (recording) {
            System.out.println("Recording is already active.");
            return;
        }

        recording = true;

        System.out.println("Recording started.");
    }

    public void stopRecording() {

        if (!recording) {
            System.out.println("Recording is not active.");
            return;
        }

        recording = false;

        System.out.println("Recording stopped.");
    }

    public boolean isRecording() {
        return recording;
    }
}
