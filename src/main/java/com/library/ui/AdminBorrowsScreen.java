package com.library.ui;

import com.library.model.Book;
import com.library.model.User;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class AdminBorrowsScreen {

    private MainApp app;

    public AdminBorrowsScreen(MainApp app) {
        this.app = app;
    }

    public void render() {
        app.getRoot().getChildren().clear();
        app.getRoot().setAlignment(Pos.TOP_CENTER);

        Label title = UIUtils.createTitle("ÖDÜNÇ ALANLAR VE KUYRUKLAR");

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(380);
        listView.setStyle(
                "-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e; -fx-text-fill: #cdd6f4;");

        // Tüm kitapları gezip şu an aktif olarak ödünç alınmış VEYA kuyrukta bekleyeni
        // olanları listeliyoruz
        for (Book bookItem : app.getDb().getAllBooks()) {
            if (!bookItem.isAvailable() || !bookItem.getQueue().isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Kitap: ").append(bookItem.getTitle()).append("\n");

                builder.append("  Şu Anki Okuyucu: ");
                if (!bookItem.isAvailable() && !bookItem.getBorrowHistory().isEmpty()) {
                    builder.append(bookItem.getBorrowHistory().getLast().getUserId()).append("\n");
                } else {
                    builder.append("Yok\n");
                }

                builder.append("  Bekleme Kuyruğu (Priority Queue): ");
                if (bookItem.getQueue().isEmpty()) {
                    builder.append("Yok");
                } else {
                    List<User> queueList = bookItem.getQueue().getQueueList();
                    for (int i = 0; i < queueList.size(); i++) {
                        builder.append(queueList.get(i).getId()).append(" (").append(queueList.get(i).getUserType())
                                .append(")");
                        if (i < queueList.size() - 1)
                            builder.append(", ");
                    }
                }
                listView.getItems().add(builder.toString());
            }
        }

        if (listView.getItems().isEmpty()) {
            listView.getItems().add("Aktif ödünç alma veya bekleme kuyruğu bulunamadı.");
        }

        Button backBtn = UIUtils.createButton("Geri");
        backBtn.setOnAction(event -> app.goBack());

        app.getRoot().getChildren().addAll(title, listView, backBtn);
    }
}
