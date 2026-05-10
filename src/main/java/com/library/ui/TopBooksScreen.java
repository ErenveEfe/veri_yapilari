package com.library.ui;

import com.library.model.Book;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

public class TopBooksScreen {

    private MainApp app;

    public TopBooksScreen(MainApp app) {
        this.app = app;
    }

    public void render() {
        app.getRoot().getChildren().clear();
        app.getRoot().setAlignment(Pos.TOP_CENTER);

        Label title = UIUtils.createTitle("EN POPÜLER 10 KİTAP");

        ListView<Book> listView = new ListView<>();
        listView.setPrefHeight(330);
        listView.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e;");

        // Sabit boyutlu diziden (Array) okuyoruz — ArrayList değil!
        Book[] topArray = app.getDb().getTopBooksArray();
        for (int i = 0; i < topArray.length; i++) {
            if (topArray[i] != null) {
                listView.getItems().add(topArray[i]);
            }
        }

        listView.setCellFactory(listProp -> new ListCell<Book>() {
            {
                selectedProperty().addListener((observable, wasSelected, isNowSelected) -> {
                    if (getItem() != null) {
                        if (isNowSelected) {
                            setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                        } else {
                            setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText((getIndex() + 1) + ". " + book.getTitle() + "   [" + book.getBorrowCount()
                            + " kez okunmuş]");
                    if (isSelected()) {
                        setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    }
                }
            }
        });

        Button viewDetailsBtn = UIUtils.createButton("Detayları Gör");
        viewDetailsBtn.setDisable(true);

        listView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldVal, newVal) -> viewDetailsBtn.setDisable(newVal == null));

        viewDetailsBtn.setOnAction(event -> {
            Book selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                app.setBook(selectedItem);
                app.changeScreen(MenuState.BOOK_DETAILS);
            }
        });

        Button backBtn = UIUtils.createButton("Geri");
        backBtn.setOnAction(event -> app.goBack());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, viewDetailsBtn);

        app.getRoot().getChildren().addAll(title, listView, buttonBox);
    }
}
