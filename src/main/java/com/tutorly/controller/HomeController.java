package com.tutorly.controller;

import com.tutorly.util.Navigator;
import javafx.fxml.FXML;

public class HomeController {

    @FXML
    private void handleLogin() {
        System.out.println("HOME: Login button clicked");
        Navigator.navigate("/fxml/login.fxml");
    }

    @FXML
    private void handleSignup() {
        System.out.println("HOME: Signup button clicked");
        Navigator.navigate("/fxml/signup.fxml");
    }
}