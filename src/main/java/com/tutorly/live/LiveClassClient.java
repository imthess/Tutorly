package com.tutorly.live;

import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Client used by Tutorly tutor/student classroom windows. */
public final class LiveClassClient {
    private final String host;
    private final int port;
    private final String token;
    private final String role;
    private final int userId;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread readerThread;
    private volatile boolean connected;

    private Consumer<String> chatListener = ignored -> { };
    private Consumer<String> whiteboardListener = ignored -> { };
    private Consumer<String> participantsListener = ignored -> { };
    private Consumer<String> errorListener = ignored -> { };
    private Consumer<String> systemListener = ignored -> { };

    public LiveClassClient(String host, int port, String token, String role, int userId) {
        this.host = host; this.port = port; this.token = token; this.role = role; this.userId = userId;
    }

    public void setChatListener(Consumer<String> listener) { this.chatListener = listener; }
    public void setWhiteboardListener(Consumer<String> listener) { this.whiteboardListener = listener; }
    public void setParticipantsListener(Consumer<String> listener) { this.participantsListener = listener; }
    public void setErrorListener(Consumer<String> listener) { this.errorListener = listener; }
    public void setSystemListener(Consumer<String> listener) { this.systemListener = listener; }

    public void connect() throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 5000);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        writer.println("HELLO|" + token + "|" + enc(role) + "|" + enc(String.valueOf(userId)));
        writer.flush();
        String response = reader.readLine();
        if (response == null) throw new IOException("Classroom server closed the connection.");
        if (response.startsWith("ERROR|")) throw new IOException(dec(response.substring(6)));
        connected = true;
        readerThread = new Thread(this::readLoop, "Tutorly-LiveReader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void readLoop() {
        try {
            String line;
            while (connected && (line = reader.readLine()) != null) dispatch(line);
        } catch (IOException e) {
            if (connected) fire(systemListener, "Classroom connection closed.");
        } finally { connected = false; }
    }

    private void dispatch(String line) {
        String[] p = line.split("\\|", 3);
        if (p.length < 2) return;
        switch (p[0]) {
            case "CHAT" -> { if (p.length == 3) fire(chatListener, dec(p[1]) + ": " + dec(p[2])); }
            case "WHITEBOARD" -> { if (p.length == 3) fire(whiteboardListener, dec(p[2])); }
            case "PARTICIPANTS" -> fire(participantsListener, dec(p[1]));
            case "END" -> fire(systemListener, dec(p[1]));
            case "ERROR" -> fire(errorListener, dec(p[1]));
            default -> { }
        }
    }

    public void sendChat(String message) { send("CHAT|x|" + enc(message)); }
    public void sendWhiteboard(String content) { send("WHITEBOARD|x|" + enc(content)); }

    private synchronized void send(String line) {
        if (connected && writer != null) { writer.println(line); writer.flush(); }
    }

    public boolean isConnected() { return connected; }

    public void disconnect() {
        connected = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) { }
    }

    private static void fire(Consumer<String> listener, String value) {
        Platform.runLater(() -> listener.accept(value));
    }

    private static String enc(String value) { return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
    private static String dec(String value) { return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8); }
}
