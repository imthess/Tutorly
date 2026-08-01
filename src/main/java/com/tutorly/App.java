package com.tutorly;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/login.fxml"));

        Scene scene = new Scene(loader.load());

        stage.setTitle("Tutorly");

        stage.setWidth(1000);
        stage.setHeight(700);

        stage.setMinWidth(1000);
        stage.setMinHeight(700);

        stage.setScene(scene);
        stage.show();
    }
}