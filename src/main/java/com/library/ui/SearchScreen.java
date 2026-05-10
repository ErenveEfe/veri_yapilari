package com.library.ui;

import com.library.model.Book;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SearchScreen {

    private MainApp app;

    public SearchScreen(MainApp app) {
        this.app = app;
    }

    public void render() {
        app.getRoot().getChildren().clear();
        app.getRoot().setAlignment(Pos.TOP_CENTER);

        Label title = UIUtils.createTitle("KİTAP ARAMA EKRANI");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("İsme Göre", "ISBN'ye Göre", "Türe Göre");
        typeBox.setStyle(UIUtils.INPUT_STYLE);

        typeBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #cdd6f4;");
                }
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Aranacak kelime...");
        searchField.setStyle(UIUtils.INPUT_STYLE);
        searchField.setPrefWidth(200);

        ComboBox<String> genreBox = new ComboBox<>();
        genreBox.setStyle(UIUtils.INPUT_STYLE);
        genreBox.setPrefWidth(120);
        genreBox.setVisible(false);
        genreBox.setManaged(false);
        genreBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #cdd6f4;");
                }
            }
        });

        ComboBox<String> subGenreBox = new ComboBox<>();
        subGenreBox.setStyle(UIUtils.INPUT_STYLE);
        subGenreBox.setPrefWidth(120);
        subGenreBox.setVisible(false);
        subGenreBox.setManaged(false);
        subGenreBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #cdd6f4;");
                }
            }
        });

        Button searchBtn = UIUtils.createButton("Ara");
        searchBtn.setPrefWidth(100);

        searchBox.getChildren().addAll(typeBox, searchField, genreBox, subGenreBox, searchBtn);

        ListView<Book> resultList = new ListView<>();
        resultList.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e;");
        resultList.setPrefHeight(260);
        resultList.setCellFactory(listProp -> new ListCell<Book>() {
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
                    setText(book.getTitle() + "   (ISBN: " + book.getIsbn() + ")");
                    if (isSelected()) {
                        setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    }
                }
            }
        });

        Label noResultLabel = new Label();
        noResultLabel.setStyle("-fx-text-fill: #f38ba8;");

        // Tür araması seçilirse Ağaç kullanılacak
        typeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            if ("Türe Göre".equals(newVal)) {
                searchField.setVisible(false);
                searchField.setManaged(false);
                searchBtn.setVisible(false);
                searchBtn.setManaged(false);

                genreBox.setVisible(true);
                genreBox.setManaged(true);
                subGenreBox.setVisible(true);
                subGenreBox.setManaged(true);

                genreBox.getItems().setAll(app.getDb().getBookTree().getGenres());
                genreBox.getSelectionModel().clearSelection();
                subGenreBox.getItems().clear();
                resultList.getItems().clear();
                noResultLabel.setText("");
            } else {
                searchField.setVisible(true);
                searchField.setManaged(true);
                searchBtn.setVisible(true);
                searchBtn.setManaged(true);

                genreBox.setVisible(false);
                genreBox.setManaged(false);
                subGenreBox.setVisible(false);
                subGenreBox.setManaged(false);
                resultList.getItems().clear();
                noResultLabel.setText("");
            }
        });

        Runnable searchAction = () -> {
            long startTime = System.nanoTime();
            resultList.getItems().clear();
            noResultLabel.setText("");
            int type = typeBox.getSelectionModel().getSelectedIndex();

            if (type == 0) { // İsme Göre
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    noResultLabel.setText("Lütfen aramak için bir kelime yazın.");
                    return;
                }
                resultList.getItems().addAll(app.getDb().searchByName(query));
            } else if (type == 1) { // ISBN'ye Göre (BST ARAMA KISMI)
                String query = searchField.getText().trim();
                if (query.isEmpty()) {
                    noResultLabel.setText("Lütfen bir ISBN numarası girin.");
                    return;
                }
                // İkili arama ağacı (BST) kullanılıyor
                Book foundBook = app.getDb().searchByIsbn(query);
                if (foundBook != null)
                    resultList.getItems().add(foundBook);
            } else if (type == 2) { // Türe Göre (TREE ARAMA KISMI)
                String selectedGenre = genreBox.getValue();
                String selectedSubGenre = subGenreBox.getValue();
                if (selectedGenre != null) {
                    resultList.getItems().addAll(app.getDb().getBookTree().getBooksByGenre(selectedGenre, selectedSubGenre));
                }
            }

            if (resultList.getItems().isEmpty()) {
                noResultLabel.setText("Maalesef eşleşen kitap bulunamadı.");
            }
            long endTime = System.nanoTime();
            app.getTimeLabel().setText(String.format("Arama süresi: %.2f ms", (endTime - startTime) / 1_000_000.0));
        };

        genreBox.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                subGenreBox.getItems().setAll(app.getDb().getBookTree().getSubGenres(newVal));
                subGenreBox.getSelectionModel().selectFirst();
                searchAction.run();
            }
        });

        subGenreBox.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                searchAction.run();
            }
        });

        searchBtn.setOnAction(event -> searchAction.run());

        typeBox.getSelectionModel().selectFirst();

        Button viewDetailsBtn = UIUtils.createButton("Detayları Gör");
        viewDetailsBtn.setDisable(true);

        resultList.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldVal, newVal) -> viewDetailsBtn.setDisable(newVal == null));

        viewDetailsBtn.setOnAction(event -> {
            Book selectedItem = resultList.getSelectionModel().getSelectedItem();
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

        app.getRoot().getChildren().addAll(title, searchBox, noResultLabel, resultList, buttonBox);
    }
}
