package com.tutorly.patterns.facade;

import com.tutorly.live.LiveClassRoom;
import com.tutorly.live.LiveClassServer;
import com.tutorly.model.User;
import com.tutorly.patterns.proxy.VideoProxy;

import java.util.function.Consumer;

/** Facade for Tutorly's in-app live classroom and its real media subsystems. */
public class LiveClassFacade {
    private final CameraService cameraService;
    private final AudioService audioService;
    private final VideoConnectionService videoConnectionService = new VideoConnectionService();
    private final VideoProxy videoProxy;
    private final WhiteboardService whiteboardService = new WhiteboardService();
    private final RecordingService recordingService = new RecordingService();
    private final User user;
    private final String meetingUrl;
    private boolean classRunning;

    public LiveClassFacade(User user, String meetingUrl) { this(user, null, meetingUrl); }

    public LiveClassFacade(User user, LiveClassServer server, String meetingUrl) {
        this.user = user;
        this.meetingUrl = meetingUrl;
        this.cameraService = new CameraService(server == null ? null : server.getMediaServer());
        this.audioService = new AudioService(server == null ? null : server.getMediaServer());
        this.videoProxy = new VideoProxy(user, server, meetingUrl);
    }

    public void startClass() {
        if (classRunning) return;
        videoConnectionService.connect();
        videoProxy.startVideo();
        if (!videoProxy.isRunning()) {
            videoConnectionService.disconnect();
            return;
        }
        RuntimeException cameraError = null;
        RuntimeException audioError = null;
        try { cameraService.startCamera(); } catch (RuntimeException e) { cameraError = e; }
        try { audioService.startMicrophone(); } catch (RuntimeException e) { audioError = e; }
        recordingService.stopRecording();
        whiteboardService.close();
        classRunning = true;
        if (cameraError != null) System.err.println(cameraError.getMessage());
        if (audioError != null) System.err.println(audioError.getMessage());
    }

    /** Student-side entry point. The classroom server validates the enrolled student ID. */
    public void joinClass() throws Exception {
        if (classRunning) return;
        if (user == null || !"student".equalsIgnoreCase(user.getRole()))
            throw new SecurityException("Only students can join a live class.");
        videoConnectionService.connect();
        LiveClassRoom.openStudent(user, meetingUrl);
        classRunning = true;
    }

    public void endClass() {
        if (!classRunning) return;
        recordingService.stopRecording();
        cameraService.stopCamera();
        audioService.stopMicrophone();
        whiteboardService.close();
        videoProxy.stopVideo();
        videoConnectionService.disconnect();
        classRunning = false;
    }

    public void startCamera() { cameraService.startCamera(); }
    public void stopCamera() { cameraService.stopCamera(); }
    public void startAudio() { audioService.startMicrophone(); }
    public void stopAudio() { audioService.stopMicrophone(); }
    public void startRecording() { recordingService.startRecording(); }
    public void stopRecording() { recordingService.stopRecording(); }
    public void openWhiteboard() { whiteboardService.open(); }
    public void closeWhiteboard() { whiteboardService.close(); }
    public boolean isClassRunning() { return classRunning; }
    public boolean isCameraOn() { return cameraService.isCameraOn(); }
    public boolean isMicrophoneOn() { return audioService.isMicrophoneOn(); }
    public boolean isRecording() { return recordingService.isRecording(); }
    public boolean isWhiteboardOpen() { return whiteboardService.isActive(); }
    public byte[] getLatestCameraFrame() { return cameraService.getLatestFrame(); }
    public String getCameraError() { return cameraService.getLastError(); }
    public String getMicrophoneError() { return audioService.getLastError(); }
    public void setCameraFrameListener(Consumer<byte[]> listener) { cameraService.setFrameListener(listener); }
}
