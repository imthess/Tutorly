package com.tutorly.patterns.facade;

/**
 * Subsystem responsible for the online whiteboard.
 */
public class WhiteboardService {

    private boolean active;

    public void open() {

        if (active) {
            System.out.println("Whiteboard is already open.");
            return;
        }

        active = true;

        System.out.println("Whiteboard opened.");
    }

    public void close() {

        if (!active) {
            System.out.println("Whiteboard is already closed.");
            return;
        }

        active = false;

        System.out.println("Whiteboard closed.");
    }

    public boolean isActive() {
        return active;
    }
}
