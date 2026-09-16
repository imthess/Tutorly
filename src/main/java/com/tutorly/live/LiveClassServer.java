package com.tutorly.live;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lightweight Tutorly classroom server. The tutor application hosts one
 * classroom and accepted students connect directly to it.
 */
public final class LiveClassServer {
    private static final int DEFAULT_PORT = 50505;
    private final ServerSocket serverSocket;
    private final String token;
    private final int tutorUserId;
    private final Set<Integer> allowedStudentUserIds;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private final LiveMediaServer mediaServer;
    private final Set<String> participantNames = ConcurrentHashMap.newKeySet();
    private volatile boolean running;
    private Thread acceptThread;

    private LiveClassServer(String token, int tutorUserId, Set<Integer> allowedStudentUserIds) throws IOException {
        this.serverSocket = new ServerSocket();
        this.serverSocket.setReuseAddress(true);
        this.serverSocket.bind(new InetSocketAddress("0.0.0.0", DEFAULT_PORT));
        this.token = token;
        this.tutorUserId = tutorUserId;
        this.allowedStudentUserIds = Set.copyOf(allowedStudentUserIds);
        this.mediaServer = LiveMediaServer.start(token, tutorUserId, allowedStudentUserIds);
    }

    public static LiveClassServer start(String token, int tutorUserId, Set<Integer> allowedStudentUserIds) throws IOException {
        LiveClassServer server = new LiveClassServer(token, tutorUserId, allowedStudentUserIds);
        server.startAccepting();
        return server;
    }

    private void startAccepting() {
        running = true;
        acceptThread = new Thread(() -> {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    Thread thread = new Thread(handler, "Tutorly-LiveClient");
                    thread.setDaemon(true);
                    thread.start();
                } catch (IOException e) {
                    if (running) System.err.println("Tutorly classroom accept error: " + e.getMessage());
                }
            }
        }, "Tutorly-LiveServer");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    public String getConnectHost() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress()) return address.getHostAddress();
                }
            }
        } catch (SocketException ignored) { }
        return "127.0.0.1";
    }

    public int getPort() { return DEFAULT_PORT; }
    public LiveMediaServer getMediaServer() { return mediaServer; }
    public boolean isRunning() { return running; }

    public void broadcastWhiteboard(String sender, String content) {
        broadcast("WHITEBOARD|" + enc(sender) + "|" + enc(content));
    }

    public void broadcastChat(String sender, String content) {
        broadcast("CHAT|" + enc(sender) + "|" + enc(content));
    }

    public void stop() {
        if (!running) return;
        running = false;
        broadcast("END|" + enc("Tutor ended the class."));
        for (ClientHandler client : clients) client.close();
        clients.clear();
        participantNames.clear();
        try { serverSocket.close(); } catch (IOException ignored) { }
        mediaServer.stop();
    }

    private void broadcast(String line) {
        for (ClientHandler client : clients) client.send(line);
    }

    private void broadcastParticipants() {
        String names = String.join(",", participantNames.stream().sorted().toList());
        broadcast("PARTICIPANTS|" + enc(names));
    }

    private static String enc(String value) {
        return Base64.getEncoder().encodeToString((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
    }

    private static String dec(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private final class ClientHandler implements Runnable {
        private final Socket socket;
        private BufferedReader reader;
        private PrintWriter writer;
        private String name;

        private ClientHandler(Socket socket) { this.socket = socket; }

        @Override public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                String hello = reader.readLine();
                if (hello == null) { close(); return; }
                String[] parts = hello.split("\\|", 4);
                if (parts.length != 4 || !"HELLO".equals(parts[0])) { reject("Invalid classroom handshake."); return; }
                String suppliedToken = parts[1];
                String role = dec(parts[2]);
                int userId;
                try { userId = Integer.parseInt(dec(parts[3])); } catch (NumberFormatException e) { reject("Invalid user identity."); return; }
                if (!token.equals(suppliedToken)) { reject("Invalid classroom code."); return; }
                if ("tutor".equalsIgnoreCase(role)) {
                    if (userId != tutorUserId) { reject("Only the class tutor can host this classroom."); return; }
                    name = "Tutor";
                } else if ("student".equalsIgnoreCase(role)) {
                    if (!allowedStudentUserIds.contains(userId)) { reject("You are not enrolled in this live class."); return; }
                    name = "Student " + userId;
                } else { reject("Invalid classroom role."); return; }

                clients.add(this);
                participantNames.add(name);
                send("WELCOME|" + enc(name));
                broadcastParticipants();

                String line;
                while (running && (line = reader.readLine()) != null) {
                    handle(line, role);
                }
            } catch (IOException ignored) {
            } finally {
                clients.remove(this);
                if (name != null) participantNames.remove(name);
                broadcastParticipants();
                close();
            }
        }

        private void handle(String line, String role) {
            String[] p = line.split("\\|", 3);
            if (p.length < 2) return;
            if ("CHAT".equals(p[0]) && p.length == 3) {
                broadcastChat(name, dec(p[2]));
            } else if ("WHITEBOARD".equals(p[0]) && p.length == 3 && "tutor".equalsIgnoreCase(role)) {
                broadcastWhiteboard(name, dec(p[2]));
            }
        }

        private synchronized void send(String line) {
            if (writer != null) { writer.println(line); writer.flush(); }
        }

        private void reject(String message) {
            send("ERROR|" + enc(message));
            close();
        }

        private void close() {
            try { socket.close(); } catch (IOException ignored) { }
        }
    }
}
