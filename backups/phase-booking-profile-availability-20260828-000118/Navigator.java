package com.tutorly.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class Navigator {

    private static Stage stage;
    private static Scene scene;

    private Navigator() {
    }

    public static void initialize(Stage primaryStage) {

        stage = primaryStage;

        scene = new Scene(
                new javafx.scene.layout.StackPane(),
                1000,
                700
        );

        URL cssUrl = Navigator.class
                .getResource("/css/application.css");

        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("WARNING: application.css not found.");
        }

        stage.setScene(scene);
    }

    public static void navigate(String fxmlPath) {

        if (stage == null || scene == null) {
            throw new IllegalStateException(
                    "Navigator has not been initialized."
            );
        }

        try {

            URL fxmlUrl = Navigator.class.getResource(fxmlPath);

            if (fxmlUrl == null) {
                System.err.println(
                        "FXML file not found: " + fxmlPath
                );
                return;
            }

            System.out.println(
                    "Navigating to: " + fxmlPath
            );

            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            Parent root = loader.load();

            scene.setRoot(root);

            System.out.println(
                    "Navigation successful: " + fxmlPath
            );

        } catch (IOException | RuntimeException e) {

            System.err.println(
                    "Failed to navigate to: " + fxmlPath
            );

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