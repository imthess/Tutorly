package com.tutorly.patterns.proxy;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

/**
 * Real Subject of the Proxy pattern.
 *
 * Opens the actual Jitsi meeting using the system browser.
 */
public class RealVideoService implements VideoService {

    private final String meetingUrl;

    private volatile boolean running;

    public RealVideoService(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    @Override
    public void startVideo() {

        if (running) {
            return;
        }

        running = true;

        CompletableFuture.runAsync(() -> {

            try {

                if (!Desktop.isDesktopSupported()) {

                    System.out.println(
                            "System browser is not supported."
                    );

                    running = false;
                    return;
                }

                Desktop.getDesktop().browse(
                        URI.create(meetingUrl)
                );

                System.out.println(
                        "Jitsi live class opened in browser."
                );

            } catch (Exception e) {

                running = false;

                System.out.println(
                        "Failed to open Jitsi: "
                                + e.getMessage()
                );
            }

        });
    }

    @Override
    public void stopVideo() {

        if (!running) {
            return;
        }

        running = false;

        System.out.println(
                "Jitsi live class stopped."
        );
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}