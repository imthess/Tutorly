package com.tutorly.patterns.proxy;

import java.awt.Desktop;
import java.net.URI;

/**
 * Real Subject of the Proxy pattern.
 *
 * Opens the actual Jitsi meeting using the system browser.
 */
public class RealVideoService implements VideoService {

    private final String meetingUrl;

    private boolean running;

    public RealVideoService(String meetingUrl) {
        this.meetingUrl = meetingUrl;
    }

    @Override
    public void startVideo() {

        if (running) {
            return;
        }

        try {

            if (!Desktop.isDesktopSupported()) {
                System.out.println(
                        "System browser is not supported."
                );
                return;
            }

            Desktop.getDesktop().browse(
                    URI.create(meetingUrl)
            );

            running = true;

            System.out.println(
                    "Jitsi live class opened in browser."
            );

        } catch (Exception e) {

            System.out.println(
                    "Failed to open Jitsi: "
                            + e.getMessage()
            );
        }
    }

    @Override
    public void stopVideo() {

        if (!running) {
            return;
        }

        running = false;

        /*
         * We cannot force-close the user's browser tab.
         * The user can leave the Jitsi meeting normally.
         */
        System.out.println(
                "Jitsi live class ended."
        );
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}