package com.tutorly.live;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** Media transport used by a Tutorly live-class host. */
public final class LiveMediaServer {
    private static final int VIDEO_PORT = 50506;
    private static final int AUDIO_PORT = 50507;
    private final String token;
    private final int tutorUserId;
    private final Set<Integer> allowedStudentUserIds;
    private final ServerSocket videoSocket;
    private final ServerSocket audioSocket;
    private final Set<DataOutputStream> videoClients = ConcurrentHashMap.newKeySet();
    private final Set<DataOutputStream> audioClients = ConcurrentHashMap.newKeySet();
    private volatile boolean running;

    private LiveMediaServer(String token, int tutorUserId, Set<Integer> allowedStudentUserIds) throws IOException {
        this.token = token;
        this.tutorUserId = tutorUserId;
        this.allowedStudentUserIds = Set.copyOf(allowedStudentUserIds);
        videoSocket = bind(VIDEO_PORT);
        audioSocket = bind(AUDIO_PORT);
    }

    public static LiveMediaServer start(String token, int tutorUserId, Set<Integer> allowedStudentUserIds) throws IOException {
        LiveMediaServer server = new LiveMediaServer(token, tutorUserId, allowedStudentUserIds);
        server.running = true;
        server.accept(videoSocketType(server), true);
        server.accept(audioSocketType(server), false);
        return server;
    }

    private static ServerSocket bind(int port) throws IOException {
        ServerSocket socket = new ServerSocket();
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress("0.0.0.0", port));
        return socket;
    }

    // Small helpers keep accept-loop creation in one place without exposing sockets.
    private static ServerSocket videoSocketType(LiveMediaServer s) { return s.videoSocket; }
    private static ServerSocket audioSocketType(LiveMediaServer s) { return s.audioSocket; }

    private void accept(ServerSocket socket, boolean video) {
        Thread t = new Thread(() -> {
            while (running) {
                try {
                    Socket client = socket.accept();
                    Thread worker = new Thread(() -> handle(client, video), video ? "Tutorly-VideoClient" : "Tutorly-AudioClient");
                    worker.setDaemon(true);
                    worker.start();
                } catch (IOException e) {
                    if (running) System.err.println("Tutorly media accept error: " + e.getMessage());
                }
            }
        }, video ? "Tutorly-VideoServer" : "Tutorly-AudioServer");
        t.setDaemon(true);
        t.start();
    }

    private void handle(Socket socket, boolean video) {
        try (socket; DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))) {
            String suppliedToken = in.readUTF();
            String role = in.readUTF();
            int userId = in.readInt();
            if (!token.equals(suppliedToken) || (!"tutor".equalsIgnoreCase(role) && !"student".equalsIgnoreCase(role))) {
                out.writeBoolean(false); out.flush(); return;
            }
            if ("tutor".equalsIgnoreCase(role) && userId != tutorUserId) { out.writeBoolean(false); out.flush(); return; }
            if ("student".equalsIgnoreCase(role) && !allowedStudentUserIds.contains(userId)) { out.writeBoolean(false); out.flush(); return; }
            out.writeBoolean(true); out.flush();
            if (video) videoClients.add(out); else audioClients.add(out);
            // Keep this connection open until the host ends the class.
            while (running && !socket.isClosed()) Thread.sleep(1000);
        } catch (Exception ignored) {
        }
    }

    public void broadcastVideo(byte[] jpeg) { broadcast(videoClients, jpeg); }
    public void broadcastAudio(byte[] pcm) { broadcast(audioClients, pcm); }

    private void broadcast(Set<DataOutputStream> clients, byte[] data) {
        if (!running || data == null || data.length == 0) return;
        for (DataOutputStream out : clients) {
            try {
                synchronized (out) {
                    out.writeInt(data.length);
                    out.write(data);
                    out.flush();
                }
            } catch (IOException e) {
                clients.remove(out);
                try { out.close(); } catch (IOException ignored) { }
            }
        }
    }

    public int getVideoPort() { return VIDEO_PORT; }
    public int getAudioPort() { return AUDIO_PORT; }
    public boolean isRunning() { return running; }

    public void stop() {
        if (!running) return;
        running = false;
        closeAll(videoClients); closeAll(audioClients);
        try { videoSocket.close(); } catch (IOException ignored) { }
        try { audioSocket.close(); } catch (IOException ignored) { }
    }

    private static void closeAll(Set<DataOutputStream> clients) {
        for (DataOutputStream out : clients) try { out.close(); } catch (IOException ignored) { }
        clients.clear();
    }
}
