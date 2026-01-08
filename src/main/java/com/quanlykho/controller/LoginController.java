package com.quanlykho.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblMessage;

    @FXML
    private void handleLogin() {
        String user = txtUsername.getText();
        String pass = txtPassword.getText();

        // Demo login (sau này nối DB)
        if ("admin".equals(user) && "123".equals(pass)) {
            try {
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/quanlykho/fxml/MainView.fxml"));
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Drug Store");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            lblMessage.setText("Sai tài khoản hoặc mật khẩu");
        }
    }
}
