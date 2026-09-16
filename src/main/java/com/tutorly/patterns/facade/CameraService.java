package com.tutorly.patterns.facade;

import com.tutorly.live.LiveMediaServer;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

/** Real webcam subsystem. Captures the default webcam and optionally publishes JPEG frames. */
public class CameraService {
    private final LiveMediaServer mediaServer;
    private volatile boolean cameraOn;
    private volatile byte[] latestFrame;
    private volatile Consumer<byte[]> frameListener = ignored -> {};
    private volatile String lastError;
    private OpenCVFrameGrabber grabber;
    private Thread captureThread;

    public CameraService() { this(null); }
    public CameraService(LiveMediaServer mediaServer) { this.mediaServer = mediaServer; }

    public synchronized void startCamera() {
        if (cameraOn) return;
        try {
            grabber = new OpenCVFrameGrabber(0);
            grabber.setImageWidth(640);
            grabber.setImageHeight(480);
            grabber.setFrameRate(15);
            grabber.start();
            cameraOn = true;
            lastError = null;
            captureThread = new Thread(this::captureLoop, "Tutorly-WebcamCapture");
            captureThread.setDaemon(true);
            captureThread.start();
            System.out.println("Camera started.");
        } catch (Exception e) {
            cameraOn = false;
            releaseGrabber();
            lastError = e.getMessage();
            throw new IllegalStateException("Unable to open the webcam. Check that a camera is connected and not being used by another application.", e);
        }
    }

    private void captureLoop() {
        Java2DFrameConverter converter = new Java2DFrameConverter();
        try {
            while (cameraOn) {
                Frame frame = grabber.grab();
                if (frame == null || frame.image == null) continue;
                BufferedImage image = converter.getBufferedImage(frame);
                if (image == null) continue;
                ByteArrayOutputStream out = new ByteArrayOutputStream(64 * 1024);
                ImageIO.write(image, "jpg", out);
                byte[] jpeg = out.toByteArray();
                latestFrame = jpeg;
                frameListener.accept(jpeg);
                if (mediaServer != null && mediaServer.isRunning()) mediaServer.broadcastVideo(jpeg);
                Thread.sleep(66);
            }
        } catch (Exception e) {
            if (cameraOn) System.err.println("Webcam capture stopped: " + e.getMessage());
        } finally {
            converter.close();
        }
    }

    public synchronized void stopCamera() {
        if (!cameraOn) return;
        cameraOn = false;
        releaseGrabber();
        System.out.println("Camera stopped.");
    }

    private void releaseGrabber() {
        if (grabber != null) {
            try { grabber.stop(); } catch (Exception ignored) { }
            try { grabber.release(); } catch (Exception ignored) { }
            grabber = null;
        }
    }

    public boolean isCameraOn() { return cameraOn; }
    public byte[] getLatestFrame() { return latestFrame; }
    public String getLastError() { return lastError; }
    public void setFrameListener(Consumer<byte[]> listener) { frameListener = listener == null ? ignored -> {} : listener; }
}
