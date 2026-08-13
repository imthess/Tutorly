package com.tutorly;

import com.tutorly.util.Navigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        Navigator.initialize(stage);

        stage.setTitle("Tutorly");
        stage.setMinWidth(900);
        stage.setMinHeight(600);

        Navigator.navigate("/fxml/home.fxml");

        stage.show();
    }

    @Override
    public void stop() {
        // Database cleanup will be implemented later.
    }

    public static void main(String[] args) {
        launch(args);
    }
}
