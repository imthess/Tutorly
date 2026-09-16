package com.tutorly.patterns.proxy;

import com.tutorly.live.LiveClassRoom;
import com.tutorly.live.LiveClassServer;
import com.tutorly.model.User;

/** Real subject of the Proxy pattern. Opens Tutorly's own in-app classroom. */
public class RealVideoService implements VideoService {
    private final String meetingUrl;
    private final User user;
    private final LiveClassServer server;
    private volatile boolean running;

    public RealVideoService(String meetingUrl) { this(null, null, meetingUrl); }
    public RealVideoService(User user, String meetingUrl) { this(user, null, meetingUrl); }
    public RealVideoService(User user, LiveClassServer server, String meetingUrl) {
        this.user = user; this.server = server; this.meetingUrl = meetingUrl;
    }

    @Override public void startVideo() {
        if (running) return;
        try {
            LiveClassRoom.openTutor(user, server, meetingUrl);
            running = true;
        } catch (Exception e) {
            running = false;
            System.err.println("Failed to open Tutorly classroom: " + e.getMessage());
        }
    }

    @Override public void stopVideo() { running = false; }
    @Override public boolean isRunning() { return running; }
}
