package com.library.ui;

import com.library.model.User;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginScreen {

    private MainApp app;

    public LoginScreen(MainApp app) {
        this.app = app;
    }

    public void render() {
        app.getRoot().getChildren().clear();
        app.setCurrentState(MenuState.LOGIN);
        app.resetNavigator();

        Label title = UIUtils.createTitle("KÜTÜPHANE GİRİŞİ");

        TextField idField = new TextField();
        idField.setPromptText("Kullanıcı Adı (Örn: std01)");
        idField.setStyle(UIUtils.INPUT_STYLE);
        idField.setMaxWidth(300);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Şifre");
        passField.setStyle(UIUtils.INPUT_STYLE);
        passField.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #f38ba8;");

        Button loginBtn = UIUtils.createButton("GİRİŞ YAP");
        loginBtn.setOnAction(event -> {
            long startTime = System.nanoTime();
            User tempUser = app.getDb().getUser(idField.getText().trim());

            // Şifre kontrolü yapıyoruz
            if (tempUser != null && tempUser.getPassword().equals(passField.getText().trim())) {
                app.setUser(tempUser);
                if (tempUser.getUserType() == com.library.model.UserType.ADMIN) {
                    app.changeScreen(MenuState.ADMIN_DASHBOARD);
                } else {
                    app.changeScreen(MenuState.DASHBOARD);
                }
            } else {
                errorLabel.setText("Hatalı kullanıcı adı veya şifre");
                long endTime = System.nanoTime();
                app.getTimeLabel().setText(String.format("İşlem süresi: %.2f ms", (endTime - startTime) / 1_000_000.0));
            }
        });

        app.getRoot().getChildren().addAll(title, idField, passField, loginBtn, errorLabel);
    }
}
