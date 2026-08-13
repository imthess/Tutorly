package com.tutorly.controller.admin;

import com.tutorly.util.Navigator;
import com.tutorly.util.Session;
import javafx.fxml.FXML;

public class AdminDashboardController {

    @FXML
    private void handleLogout() {

        Session.logout();

        Navigator.navigate("/fxml/home.fxml");
    }
}
