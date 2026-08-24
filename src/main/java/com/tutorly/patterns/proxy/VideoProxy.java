package com.tutorly.patterns.proxy;

import com.tutorly.model.User;

/**
 * Proxy for the video service.
 *
 * Controls access to the real video service based on
 * the authenticated Tutorly user.
 */
public class VideoProxy implements VideoService {

    private RealVideoService realVideoService;

    private final User user;

    public VideoProxy(User user) {
        this.user = user;
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
            System.out.println(
                    "Access denied: user cannot control the video service."
            );
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

    /**
     * Checks whether the current user is allowed
     * to control the video service.
     */
    private boolean hasAccess() {

        if (user == null) {
            return false;
        }

        return "tutor".equalsIgnoreCase(
                user.getRole()
        );
    }

    /**
     * Creates the real service only when needed.
     */
    private RealVideoService getRealVideoService() {

        if (realVideoService == null) {

            System.out.println(
                    "Creating RealVideoService..."
            );

            realVideoService =
                    new RealVideoService();
        }

        return realVideoService;
    }
}
