package com.tutorly.patterns.facade;

import com.tutorly.model.User;
import com.tutorly.patterns.proxy.VideoProxy;

import java.util.function.Consumer;

/**
 * Facade for managing an online live class.
 */
public class LiveClassFacade {

    private final CameraService cameraService;
    private final AudioService audioService;
    private final VideoConnectionService videoConnectionService;
    private final VideoProxy videoProxy;
    private final WhiteboardService whiteboardService;
    private final RecordingService recordingService;

    private boolean classRunning;

    public LiveClassFacade(
            User user,
            String meetingUrl
    ) {

        cameraService = new CameraService();
        audioService = new AudioService();

        videoConnectionService =
                new VideoConnectionService();

        videoProxy =
                new VideoProxy(
                        user,
                        meetingUrl
                );

        whiteboardService =
                new WhiteboardService();

        recordingService =
                new RecordingService();

        classRunning = false;
    }

    public void startClass() {

        if (classRunning) {
            return;
        }

        videoConnectionService.connect();

        videoProxy.startVideo();

        if (!videoProxy.isRunning()) {
            videoConnectionService.disconnect();
            return;
        }

        cameraService.startCamera();
        audioService.startMicrophone();

        recordingService.stopRecording();
        whiteboardService.close();

        classRunning = true;
    }

    public void endClass() {

        if (!classRunning) {
            return;
        }

        recordingService.stopRecording();

        cameraService.stopCamera();

        audioService.stopMicrophone();

        whiteboardService.close();

        videoProxy.stopVideo();

        videoConnectionService.disconnect();

        classRunning = false;
    }

    public void startCamera() {
        cameraService.startCamera();
    }

    public void stopCamera() {
        cameraService.stopCamera();
    }

    public void startAudio() {
        audioService.startMicrophone();
    }

    public void stopAudio() {
        audioService.stopMicrophone();
    }

    public void startRecording() {
        recordingService.startRecording();
    }

    public void stopRecording() {
        recordingService.stopRecording();
    }

    public void openWhiteboard() {
        whiteboardService.open();
    }

    public void closeWhiteboard() {
        whiteboardService.close();
    }

    public boolean isClassRunning() {
        return classRunning;
    }

    public boolean isCameraOn() {
        return cameraService.isCameraOn();
    }

    public boolean isMicrophoneOn() {
        return audioService.isMicrophoneOn();
    }

    public boolean isRecording() {
        return recordingService.isRecording();
    }

    public boolean isWhiteboardOpen() {
        return whiteboardService.isActive();
    }
}