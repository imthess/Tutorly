package com.tutorly.live;

import com.tutorly.patterns.facade.LiveClassFacade;

/** Holds the currently hosted classroom and its facade for the running Tutorly process. */
public final class LiveClassSession {
    private static LiveClassServer server;
    private static String meetingUrl;
    private static LiveClassFacade facade;
    private static Runnable endAction = () -> {};
    private LiveClassSession() {}

    public static synchronized void start(LiveClassServer s, String url, LiveClassFacade f, Runnable onEnd) {
        server=s; meetingUrl=url; facade=f; endAction=onEnd==null?()->{}:onEnd;
    }
    public static synchronized LiveClassServer getServer() { return server; }
    public static synchronized String getMeetingUrl() { return meetingUrl; }
    public static synchronized LiveClassFacade getFacade() { return facade; }
    public static synchronized boolean isActive() { return server != null && server.isRunning(); }
    public static synchronized void end() { Runnable action=endAction; server=null; meetingUrl=null; facade=null; endAction=()->{}; action.run(); }
    public static synchronized void clear() { server=null; meetingUrl=null; facade=null; endAction=()->{}; }
}
