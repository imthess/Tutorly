package com.tutorly.controller;

import com.tutorly.util.Navigator;
import javafx.fxml.FXML;

public class SignupController {

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/home.fxml");
    }

    @FXML
    private void handleLogin() {
        Navigator.navigate("/fxml/login.fxml");
    }

    @FXML
    private void handleSignup() {
        // User registration will be implemented in the authentication phase.
    }
}
