package com.tutorly.patterns.facade;

/**
 * Subsystem responsible for controlling the camera.
 */
public class CameraService {

    private boolean cameraOn;

    public void startCamera() {

        if (cameraOn) {
            System.out.println("Camera is already on.");
            return;
        }

        cameraOn = true;

        System.out.println("Camera started.");
    }

    public void stopCamera() {

        if (!cameraOn) {
            System.out.println("Camera is already off.");
            return;
        }

        cameraOn = false;

        System.out.println("Camera stopped.");
    }

    public boolean isCameraOn() {
        return cameraOn;
    }
}
