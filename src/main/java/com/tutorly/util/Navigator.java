package com.tutorly.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class Navigator {

    private static Stage stage;
    private static Scene scene;

    private Navigator() {
    }

    public static void initialize(Stage primaryStage) {
        stage = primaryStage;

        scene = new Scene(new javafx.scene.layout.StackPane(), 1000, 700);

        String css = Navigator.class
                .getResource("/css/application.css")
                .toExternalForm();

        scene.getStylesheets().add(css);

        stage.setScene(scene);
    }

    public static void navigate(String fxmlPath) {
        if (stage == null || scene == null) {
            throw new IllegalStateException(
                    "Navigator has not been initialized."
            );
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    Navigator.class.getResource(fxmlPath)
            );

            Parent root = loader.load();

            scene.setRoot(root);

        } catch (IOException | RuntimeException e) {
            System.err.println("Failed to load: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static Scene getScene() {
        return scene;
    }
}
