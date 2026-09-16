package com.tutorly.patterns.facade;

import com.tutorly.live.LiveMediaServer;

import javax.sound.sampled.*;

/** Real microphone subsystem. Captures PCM audio and optionally publishes it to the class. */
public class AudioService {
    private static final AudioFormat FORMAT = new AudioFormat(44100f, 16, 1, true, false);
    private final LiveMediaServer mediaServer;
    private volatile boolean microphoneOn;
    private TargetDataLine microphone;
    private Thread captureThread;
    private volatile String lastError;

    public AudioService() { this(null); }
    public AudioService(LiveMediaServer mediaServer) { this.mediaServer = mediaServer; }

    public synchronized void startMicrophone() {
        if (microphoneOn) return;
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, FORMAT);
            if (!AudioSystem.isLineSupported(info)) throw new IllegalStateException("No supported microphone input was found.");
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(FORMAT);
            microphone.start();
            microphoneOn = true;
            lastError = null;
            captureThread = new Thread(this::captureLoop, "Tutorly-MicrophoneCapture");
            captureThread.setDaemon(true);
            captureThread.start();
            System.out.println("Microphone started.");
        } catch (Exception e) {
            microphoneOn = false;
            closeLine();
            lastError = e.getMessage();
            throw new IllegalStateException("Unable to open the microphone. Check the system input device and permissions.", e);
        }
    }

    private void captureLoop() {
        byte[] buffer = new byte[1764]; // about 20 ms at 44.1 kHz, mono, 16-bit
        try {
            while (microphoneOn) {
                int read = microphone.read(buffer, 0, buffer.length);
                if (read > 0 && mediaServer != null && mediaServer.isRunning()) {
                    byte[] packet = java.util.Arrays.copyOf(buffer, read);
                    mediaServer.broadcastAudio(packet);
                }
            }
        } catch (Exception e) {
            if (microphoneOn) System.err.println("Microphone capture stopped: " + e.getMessage());
        }
    }

    public synchronized void stopMicrophone() {
        if (!microphoneOn) return;
        microphoneOn = false;
        closeLine();
        System.out.println("Microphone stopped.");
    }

    private void closeLine() {
        if (microphone != null) {
            try { microphone.stop(); } catch (Exception ignored) { }
            try { microphone.close(); } catch (Exception ignored) { }
            microphone = null;
        }
    }

    public String getLastError() { return lastError; }
    public boolean isMicrophoneOn() { return microphoneOn; }
    public static AudioFormat getFormat() { return FORMAT; }
}
