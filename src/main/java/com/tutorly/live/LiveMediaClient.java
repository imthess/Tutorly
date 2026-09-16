package com.tutorly.live;

import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;

/** Receives the tutor's real webcam frames and microphone PCM stream. */
public final class LiveMediaClient {
    private final String host, token, role;
    private final int userId, videoPort, audioPort;
    private volatile boolean running;
    private Socket videoSocket, audioSocket;
    private Consumer<byte[]> videoListener = ignored -> {};
    private Consumer<byte[]> audioListener = ignored -> {};
    private Consumer<String> errorListener = ignored -> {};

    public LiveMediaClient(String host, int videoPort, int audioPort, String token, String role, int userId) {
        this.host=host; this.videoPort=videoPort; this.audioPort=audioPort; this.token=token; this.role=role; this.userId=userId;
    }
    public void setVideoListener(Consumer<byte[]> listener) { videoListener=listener == null ? ignored -> {} : listener; }
    public void setAudioListener(Consumer<byte[]> listener) { audioListener=listener == null ? ignored -> {} : listener; }
    public void setErrorListener(Consumer<String> listener) { errorListener=listener == null ? ignored -> {} : listener; }

    public void connect() throws IOException {
        videoSocket = connect(videoPort);
        audioSocket = connect(audioPort);
        running = true;
        Thread vt = new Thread(() -> readLoop(videoSocket, videoListener, 4 * 1024 * 1024, true), "Tutorly-VideoReceiver");
        Thread at = new Thread(() -> readLoop(audioSocket, audioListener, 64 * 1024, false), "Tutorly-AudioReceiver");
        vt.setDaemon(true); at.setDaemon(true); vt.start(); at.start();
    }

    private Socket connect(int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5000);
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        out.writeUTF(token); out.writeUTF(role); out.writeInt(userId); out.flush();
        if (!in.readBoolean()) { socket.close(); throw new IOException("Media access denied for this class."); }
        return socket;
    }

    private void readLoop(Socket socket, Consumer<byte[]> listener, int maxSize, boolean fxThread) {
        try (socket; DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {
            while (running) {
                int size = in.readInt();
                if (size <= 0 || size > maxSize) throw new IOException("Invalid media packet.");
                byte[] data = in.readNBytes(size);
                if (data.length != size) throw new EOFException();
                if (fxThread) Platform.runLater(() -> listener.accept(data));
                else listener.accept(data);
            }
        } catch (Exception e) {
            if (running) Platform.runLater(() -> errorListener.accept("Live media connection closed."));
        }
    }

    public void disconnect() {
        running=false;
        try { if(videoSocket!=null) videoSocket.close(); } catch(IOException ignored){}
        try { if(audioSocket!=null) audioSocket.close(); } catch(IOException ignored){}
    }
}
