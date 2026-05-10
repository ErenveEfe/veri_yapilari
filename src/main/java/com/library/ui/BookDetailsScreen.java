package com.library.ui;

import com.library.model.Book;
import com.library.model.BorrowHistory;
import com.library.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.List;

public class BookDetailsScreen {

    private MainApp app;

    public BookDetailsScreen(MainApp app) {
        this.app = app;
    }

    // Detay sayfası işlem sonrası mesaj göstermek için statusMessage alıyor
    public void render(String statusMessage) {
        app.getRoot().getChildren().clear();
        app.getRoot().setAlignment(Pos.TOP_LEFT);
        app.getRoot().setSpacing(10);

        Label title = UIUtils.createTitle("KİTAP DETAYLARI");

        // Kopyalanabilir metin alanları kullanıyoruz seçip kopyalama yapılabilir
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
                UIUtils.createCopyableField("Kitap Adı:  " + app.getBook().getTitle()),
                UIUtils.createCopyableField("Yazar:      " + app.getBook().getAuthor()),
                UIUtils.createCopyableField("ISBN:       " + app.getBook().getIsbn()),
                UIUtils.createCopyableField("Tür:        " + app.getBook().getGenre()),
                UIUtils.createCopyableField("Alt Tür:    " + app.getBook().getSubGenre()),
                UIUtils.createCopyableField("Okunma:     " + app.getBook().getBorrowCount() + " kez"));

        TextField statusField = UIUtils.createCopyableField(
                "Durum:      " + (app.getBook().isAvailable() ? "RAFTA (ALINABİLİR)" : "KULLANIMDA (ÖDÜNÇ ALINMIŞ)"));
        statusField.setStyle(app.getBook().isAvailable()
                ? "-fx-text-fill: #a6e3a1; -fx-background-color: transparent; -fx-border-color: transparent;"
                : "-fx-text-fill: #f38ba8; -fx-background-color: transparent; -fx-border-color: transparent;");
        statusField.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        infoBox.getChildren().add(statusField);

        // GRAF BURADA KULLANILIYOR
        Label recTitle = UIUtils.createTitle("BUNU OKUYANLAR ŞUNLARI DA OKUDU");
        recTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox recBox = new VBox(5);
        List<String> recs = app.getDb().getLibraryGraph().getRecommendations(app.getBook().getIsbn(), 3);
        if (recs.isEmpty()) {
            recBox.getChildren().add(UIUtils.createLabel("Henüz öneri yok. Ağ oluşturmak için daha fazla kitap ödünç alın!"));
        } else {
            for (String recIsbn : recs) {
                Book recommendedBook = app.getDb().getBookByIsbnMap(recIsbn);
                if (recommendedBook != null)
                    recBox.getChildren().add(UIUtils.createCopyableField("- " + recommendedBook.getTitle()));
            }
        }

        // ÖDÜNÇ ALMA GEÇMİŞİ (Bağlı Liste)
        Label historyTitle = UIUtils.createTitle("ÖDÜNÇ ALMA GEÇMİŞİ (Bağlı Liste)");
        historyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        ListView<String> historyList = new ListView<>();
        historyList.setPrefHeight(80);
        historyList.setMinHeight(60);
        historyList.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e;");
        historyList.setCellFactory(listProp -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #cdd6f4; -fx-background-color: transparent; -fx-font-size: 14px;");
                }
            }
        });

        if (app.getBook().getBorrowHistory().isEmpty()) {
            historyList.getItems().add("Bu kitap henüz ödünç alınmamış.");
        } else {
            for (BorrowHistory bh : app.getBook().getBorrowHistory()) {
                historyList.getItems().add(bh.getBorrowDate() + " - Alan Kullanıcı: " + bh.getUserId());
            }
        }

        // İşlem sonucu mesajı
        Label msgLabel = new Label(statusMessage != null ? statusMessage : "");
        msgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        if (statusMessage != null && statusMessage.startsWith("Başarılı")) {
            msgLabel.setStyle("-fx-text-fill: #a6e3a1;");
        } else {
            msgLabel.setStyle("-fx-text-fill: #f9e2af;");
        }

        // Aksiyonlar
        Button borrowBtn = UIUtils.createButton("Kitabı Ödünç Al");
        borrowBtn.setOnAction(event -> {
            long startTime = System.nanoTime();
            String msg;
            if (app.getBook().isAvailable()) {
                boolean isNew = app.getBook().getUniqueReaders().add(app.getUser().getId());
                if (isNew) {
                    app.getBook().setBorrowCount(app.getBook().getBorrowCount() + 1);
                }
                app.getBook().getBorrowHistory().add(new BorrowHistory(app.getUser().getId(), LocalDate.now()));
                app.getBook().setAvailable(false);
                app.getUser().addReadIsbn(app.getBook().getIsbn());
                app.getDb().getLibraryGraph().addCoRead(app.getUser().getReadIsbns());

                // Ödünç alma sonrası en çok okunanları güncelle
                app.getDb().updateTopBooks();
                msg = "Başarılı! Kitabı ödünç alan: " + app.getUser().getId();
            } else {
                // Öncelikli Kuyruk
                boolean added = app.getBook().getQueue().enqueue(app.getUser());
                msg = added
                        ? "Kitap rafta yok. Öncelikli bekleme listesine eklendiniz."
                        : "Zaten bu kitap için sırada bekliyorsunuz.";
            }
            app.showBookDetails(msg);
            long endTime = System.nanoTime();
            app.getTimeLabel().setText(String.format("İşlem süresi: %.2f ms", (endTime - startTime) / 1_000_000.0));
        });

        Button returnBtn = UIUtils.createButton("Kitabı İade Et");
        returnBtn.setOnAction(event -> {
            long startTime = System.nanoTime();
            String msg;
            if (app.getBook().getQueue().isEmpty()) {
                app.getBook().setAvailable(true);
                msg = "Başarılı! Kitap rafa geri konuldu.";
            } else {
                // Öncelikli kuyruktan sıradaki kişiyi al
                User nextUser = app.getBook().getQueue().dequeue();
                boolean isNew = app.getBook().getUniqueReaders().add(nextUser.getId());
                if (isNew) {
                    app.getBook().setBorrowCount(app.getBook().getBorrowCount() + 1);
                }
                app.getBook().getBorrowHistory().add(new BorrowHistory(nextUser.getId(), LocalDate.now()));
                nextUser.addReadIsbn(app.getBook().getIsbn());
                app.getDb().getLibraryGraph().addCoRead(nextUser.getReadIsbns());
                app.getDb().updateTopBooks();
                msg = "Başarılı! Kitap iade edildi ve sıradaki kullanıcıya (" + nextUser.getId() + ") devredildi.";
            }
            app.showBookDetails(msg);
            long endTime = System.nanoTime();
            app.getTimeLabel().setText(String.format("İşlem süresi: %.2f ms", (endTime - startTime) / 1_000_000.0));
        });

        Button backBtn = UIUtils.createButton("Geri");
        backBtn.setOnAction(event -> app.goBack());

        boolean isAdmin = app.getUser().getUserType() == com.library.model.UserType.ADMIN;
        boolean isCurrentBorrower = !app.getBook().isAvailable() && !app.getBook().getBorrowHistory().isEmpty()
                && app.getBook().getBorrowHistory().getLast().getUserId().equals(app.getUser().getId());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        if (isCurrentBorrower || (isAdmin && !app.getBook().isAvailable())) {
            buttonBox.getChildren().addAll(returnBtn, backBtn);
        } else {
            buttonBox.getChildren().addAll(borrowBtn, backBtn);
        }

        app.getRoot().getChildren().addAll(title, infoBox, historyTitle, historyList, recTitle, recBox, msgLabel, buttonBox);
    }
}
