package com.tutorly.patterns.proxy;

/**
 * Subject interface for the Proxy pattern.
 *
 * Defines operations available to the video service.
 */
public interface VideoService {

    void startVideo();

    void stopVideo();

    boolean isRunning();
}
