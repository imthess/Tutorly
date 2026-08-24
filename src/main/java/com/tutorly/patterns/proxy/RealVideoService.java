package com.tutorly.patterns.proxy;

/**
 * Real Subject of the Proxy pattern.
 *
 * Represents the actual video service.
 */
public class RealVideoService implements VideoService {

    private boolean running;

    @Override
    public void startVideo() {

        if (running) {
            System.out.println(
                    "Video service is already running."
            );
            return;
        }

        System.out.println(
                "Starting actual video service..."
        );

        running = true;

        System.out.println(
                "Video service started."
        );
    }

    @Override
    public void stopVideo() {

        if (!running) {
            System.out.println(
                    "Video service is not running."
            );
            return;
        }

        System.out.println(
                "Stopping actual video service..."
        );

        running = false;

        System.out.println(
                "Video service stopped."
        );
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
