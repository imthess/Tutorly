package com.tutorly.patterns.proxy;

import com.tutorly.model.User;

/**
 * Proxy for the real video service.
 *
 * Only tutors can start a live class.
 */
public class VideoProxy implements VideoService {

    private RealVideoService realVideoService;

    private final User user;
    private final String meetingUrl;

    public VideoProxy(
            User user,
            String meetingUrl
    ) {
        this.user = user;
        this.meetingUrl = meetingUrl;
    }

    @Override
    public void startVideo() {

        if (!hasAccess()) {

            System.out.println(
                    "Access denied: only tutors can start video classes."
            );

            return;
        }

        getRealVideoService().startVideo();
    }

    @Override
    public void stopVideo() {

        if (!hasAccess()) {
            return;
        }

        if (realVideoService != null) {
            realVideoService.stopVideo();
        }
    }

    @Override
    public boolean isRunning() {

        return realVideoService != null
                && realVideoService.isRunning();
    }

    private boolean hasAccess() {

        return user != null
                && "tutor".equalsIgnoreCase(
                user.getRole()
        );
    }

    private RealVideoService getRealVideoService() {

        if (realVideoService == null) {

            System.out.println(
                    "Creating RealVideoService..."
            );

            realVideoService =
                    new RealVideoService(
                            meetingUrl
                    );
        }

        return realVideoService;
    }
}