package com.library.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AdminDashboardScreen {

    private MainApp app;

    public AdminDashboardScreen(MainApp app) {
        this.app = app;
    }

    public void render() {
        app.getRoot().getChildren().clear();

        Label title = UIUtils.createTitle("YÖNETİCİ PANELİ");
        Label welcome = UIUtils.createLabel("Hoş Geldin, " + app.getUser().getId());

        Button viewBorrowsBtn = UIUtils.createButton("Ödünç Alanlar & Kuyruklar");
        viewBorrowsBtn.setOnAction(event -> app.changeScreen(MenuState.ADMIN_BORROWS));

        Button topBooksBtn = UIUtils.createButton("En Popüler 10 Kitap");
        topBooksBtn.setOnAction(event -> app.changeScreen(MenuState.TOP_BOOKS));

        Button searchBtn = UIUtils.createButton("Kitap Ara");
        searchBtn.setOnAction(event -> app.changeScreen(MenuState.SEARCH));

        Button benchmarkBtn = UIUtils.createButton("Performans Testi");
        benchmarkBtn.setOnAction(event -> app.changeScreen(MenuState.BENCHMARK));

        Button logoutBtn = UIUtils.createDangerButton("Çıkış Yap");
        logoutBtn.setOnAction(event -> {
            app.setUser(null);
            app.setBook(null);
            app.changeScreen(MenuState.LOGIN);
        });

        app.getRoot().getChildren().addAll(title, welcome, viewBorrowsBtn, topBooksBtn, searchBtn, benchmarkBtn, logoutBtn);
    }
}
