package com.tutorly.live;

import com.tutorly.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/** Opens the Tutorly classroom inside the JavaFX application. */
public final class LiveClassRoom {
    private LiveClassRoom() { }

    public static LiveClassRoomController load() throws IOException {
        FXMLLoader loader = new FXMLLoader(LiveClassRoom.class.getResource("/fxml/live/classroom.fxml"));
        Parent root = loader.load();
        com.tutorly.util.Navigator.getScene().setRoot(root);
        return loader.getController();
    }

    public static void openTutor(User user, LiveClassServer server, String meetingUrl) throws IOException {
        load().configureTutor(user, server, meetingUrl);
    }

    public static LiveClassRoomController openTutorWithCallback(User user, LiveClassServer server, String meetingUrl) throws IOException {
        LiveClassRoomController controller = load();
        controller.configureTutor(user, server, meetingUrl);
        return controller;
    }

    public static void openStudent(User user, String meetingUrl) throws IOException {
        load().configureStudent(user, meetingUrl);
    }
}
