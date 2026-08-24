package com.tutorly.patterns.facade;
import com.tutorly.model.User;
import com.tutorly.patterns.proxy.VideoProxy;

/**
 * Facade for managing an online live class.
 *
 * Hides the complexity of camera, audio, video connection,
 * whiteboard, and recording services behind a simple API.
 */
public class LiveClassFacade {

    private final CameraService cameraService;
    private final AudioService audioService;
    private final VideoConnectionService videoConnectionService;
    private final com.tutorly.patterns.proxy.VideoProxy videoProxy;
    private final WhiteboardService whiteboardService;
    private final RecordingService recordingService;


    private boolean classRunning;

    public LiveClassFacade(User user) {

        cameraService = new CameraService();
        audioService = new AudioService();
        videoConnectionService =
                new VideoConnectionService();
        videoProxy =
                new VideoProxy(user);
        whiteboardService = new WhiteboardService();
        recordingService = new RecordingService();

        classRunning = false;
    }

    /**
     * Starts the complete online class.
     */
    public void startClass() {

        if (classRunning) {
            System.out.println(
                    "Online class is already running."
            );
            return;
        }

        System.out.println(
                "Starting online class..."
        );

        videoConnectionService.connect();
        videoProxy.startVideo();
        cameraService.startCamera();
        audioService.startMicrophone();
        whiteboardService.open();
        recordingService.startRecording();

        classRunning = true;

        System.out.println(
                "Online class started."
        );
    }

    /**
     * Ends the complete online class.
     */
    public void endClass() {

        if (!classRunning) {
            System.out.println(
                    "Online class is not running."
            );
            return;
        }

        System.out.println(
                "Ending online class..."
        );

        recordingService.stopRecording();
        whiteboardService.close();
        audioService.stopMicrophone();
        cameraService.stopCamera();
        videoProxy.stopVideo();
        videoConnectionService.disconnect();

        classRunning = false;

        System.out.println(
                "Online class ended."
        );
    }

    /**
     * Controls the camera through the facade.
     */
    public void startCamera() {
        cameraService.startCamera();
    }

    public void stopCamera() {
        cameraService.stopCamera();
    }

    /**
     * Controls the microphone through the facade.
     */
    public void startAudio() {
        audioService.startMicrophone();
    }

    public void stopAudio() {
        audioService.stopMicrophone();
    }

    /**
     * Controls recording through the facade.
     */
    public void startRecording() {
        recordingService.startRecording();
    }

    public void stopRecording() {
        recordingService.stopRecording();
    }

    /**
     * Controls the whiteboard through the facade.
     */
    public void openWhiteboard() {
        whiteboardService.open();
    }

    public void closeWhiteboard() {
        whiteboardService.close();
    }
    public boolean isWhiteboardOpen() {
        return whiteboardService.isActive();
    }

    /**
     * Returns whether the online class is running.
     */
    public boolean isClassRunning() {
        return classRunning;
    }

    /**
     * Returns whether the camera is currently on.
     */
    public boolean isCameraOn() {
        return cameraService.isCameraOn();
    }

    /**
     * Returns whether the microphone is currently on.
     */
    public boolean isMicrophoneOn() {
        return audioService.isMicrophoneOn();
    }

    /**
     * Returns whether recording is currently active.
     */
    public boolean isRecording() {
        return recordingService.isRecording();
    }

    /**
     * Returns whether the whiteboard is currently open.
     */
    public boolean isWhiteboardActive() {
        return whiteboardService.isActive();
    }


}
