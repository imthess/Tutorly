package com.tutorly.patterns.facade;

/**
 * Subsystem responsible for establishing and
 * closing the video connection.
 */
public class VideoConnectionService {

    private boolean connected;

    public void connect() {

        if (connected) {
            System.out.println("Video connection is already active.");
            return;
        }

        System.out.println("Connecting to video service...");

        connected = true;

        System.out.println("Video connection established.");
    }

    public void disconnect() {

        if (!connected) {
            System.out.println("Video connection is not active.");
            return;
        }

        connected = false;

        System.out.println("Video connection closed.");
    }

    public boolean isConnected() {
        return connected;
    }
}
