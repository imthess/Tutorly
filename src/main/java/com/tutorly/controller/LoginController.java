package com.tutorly.controller;

import com.tutorly.util.Navigator;
import javafx.fxml.FXML;

public class LoginController {

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/home.fxml");
    }

    @FXML
    private void handleSignup() {
        Navigator.navigate("/fxml/signup.fxml");
    }

    @FXML
    private void handleLogin() {
        // Authentication will be implemented in the next phase.
    }
}
