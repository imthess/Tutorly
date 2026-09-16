package com.tutorly.patterns.proxy;

import com.tutorly.live.LiveClassServer;
import com.tutorly.model.User;

/** Proxy protecting tutor-only class hosting. */
public class VideoProxy implements VideoService {
    private RealVideoService realVideoService;
    private final User user;
    private final String meetingUrl;
    private final LiveClassServer server;

    public VideoProxy(User user, String meetingUrl) { this(user, null, meetingUrl); }
    public VideoProxy(User user, LiveClassServer server, String meetingUrl) {
        this.user = user; this.server = server; this.meetingUrl = meetingUrl;
    }
    @Override public void startVideo() {
        if (!hasAccess()) { System.out.println("Access denied: only tutors can start video classes."); return; }
        getRealVideoService().startVideo();
    }
    @Override public void stopVideo() { if (hasAccess() && realVideoService != null) realVideoService.stopVideo(); }
    @Override public boolean isRunning() { return realVideoService != null && realVideoService.isRunning(); }
    private boolean hasAccess() { return user != null && "tutor".equalsIgnoreCase(user.getRole()); }
    private RealVideoService getRealVideoService() {
        if (realVideoService == null) realVideoService = new RealVideoService(user, server, meetingUrl);
        return realVideoService;
    }
}
