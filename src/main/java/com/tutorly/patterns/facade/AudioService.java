package com.tutorly.patterns.facade;

/**
 * Subsystem responsible for controlling audio.
 */
public class AudioService {

    private boolean microphoneOn;

    public void startMicrophone() {

        if (microphoneOn) {
            System.out.println("Microphone is already on.");
            return;
        }

        microphoneOn = true;

        System.out.println("Microphone started.");
    }

    public void stopMicrophone() {

        if (!microphoneOn) {
            System.out.println("Microphone is already off.");
            return;
        }

        microphoneOn = false;

        System.out.println("Microphone stopped.");
    }

    public boolean isMicrophoneOn() {
        return microphoneOn;
    }
}
