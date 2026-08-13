package com.tutorly.controller;

import com.tutorly.util.Navigator;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    private void handleLogin() {
        Navigator.navigate("/fxml/login.fxml");
    }

    @FXML
    private void handleSignup() {
        Navigator.navigate("/fxml/signup.fxml");
    }
}
