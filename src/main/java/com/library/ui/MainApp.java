package com.library.ui;

import com.library.logic.LibraryDatabase;
import com.library.logic.NavigationManager;
import com.library.logic.PerformanceBenchmark;
import com.library.model.Book;
import com.library.model.BorrowHistory;
import com.library.model.User;
import com.library.util.DataLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class MainApp extends Application {

    private VBox root;
    private Label timeLabel;
    // BUG-001 FIX: track current state to push correctly in changeScreen
    private MenuState currentState = MenuState.LOGIN;
    private NavigationManager navigator = new NavigationManager();
    private LibraryDatabase db = new LibraryDatabase();
    private User user = null;
    private Book book = null;

    private static final String BG_COLOR = "-fx-background-color: #1e1e2e;";
    private static final String TEXT_COLOR = "-fx-text-fill: #cdd6f4;";
    private static final String BTN_STYLE = "-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;";
    private static final String BTN_DANGER_STYLE = "-fx-background-color: #f38ba8; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;";
    private static final String INPUT_STYLE = "-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-background-radius: 5;";

    @Override
    public void start(Stage primaryStage) {
        DataLoader.initialize(db);

        root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle(BG_COLOR);

        timeLabel = new Label("Hazır");
        timeLabel.setStyle("-fx-text-fill: #a6adc8; -fx-background-color: #11111b; -fx-padding: 5px; -fx-background-radius: 5px;");
        timeLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));

        javafx.scene.layout.StackPane mainLayout = new javafx.scene.layout.StackPane(root, timeLabel);
        mainLayout.setStyle(BG_COLOR);
        javafx.scene.layout.StackPane.setAlignment(timeLabel, Pos.BOTTOM_RIGHT);
        javafx.scene.layout.StackPane.setMargin(timeLabel, new Insets(10));

        Scene scene = new Scene(mainLayout, 1100, 650);

        primaryStage.setTitle("Kütüphane Yönetim Sistemi");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        showLoginScreen();
    }

    // ---------- UI Helpers ----------

    private Label createTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lbl.setStyle("-fx-text-fill: #a6e3a1;");
        return lbl;
    }

    private Label createLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 16));
        lbl.setStyle(TEXT_COLOR);
        return lbl;
    }

    private Button createButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_STYLE);
        btn.setPrefWidth(260);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Arial", 14));
        return btn;
    }

    private Button createDangerButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_DANGER_STYLE);
        btn.setPrefWidth(260);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Arial", 14));
        return btn;
    }

    // Kopyalanabilir metin alanı — Label yerine kullanılır, düzenlenemez ama seçilebilir
    private TextField createCopyableField(String text) {
        TextField field = new TextField(text);
        field.setEditable(false);
        field.setStyle(INPUT_STYLE + " -fx-border-color: transparent; -fx-background-color: transparent; -fx-text-fill: #cdd6f4;");
        field.setFont(Font.font("Arial", 15));
        field.setPrefWidth(550);
        return field;
    }

    // ---------- Navigation ----------

    // BUG-001 FIX: push the CURRENT state before transitioning
    private void changeScreen(MenuState newState) {
        navigator.push(currentState.name());
        currentState = newState;
        renderScreen(newState);
    }

    private void goBack() {
        String prevState = navigator.pop();
        if (prevState != null) {
            currentState = MenuState.valueOf(prevState);
            renderScreen(currentState);
        }
    }

    private void renderScreen(MenuState state) {
        long startTime = System.nanoTime();
        
        root.getChildren().clear();
        root.setAlignment(Pos.CENTER);

        switch (state) {
            case LOGIN:       showLoginScreen();  break;
            case DASHBOARD:   showDashboard();    break;
            case TOP_BOOKS:   showTopBooks();     break;
            case SEARCH:      showSearchScreen(); break;
            case BOOK_DETAILS: showBookDetails(null); break;
            case ADMIN_DASHBOARD: showAdminDashboard(); break;
            case ADMIN_BORROWS: showAdminBorrows(); break;
            case BENCHMARK: BenchmarkScreen.render(root, this::goBack); break;
        }

        long endTime = System.nanoTime();
        double ms = (endTime - startTime) / 1_000_000.0;
        // Hocaya hızı göstermek için eklendi:
        timeLabel.setText(String.format("İşlem süresi: %.2f ms", ms));
    }

    // ---------- Screens ----------

    private void showLoginScreen() {
        root.getChildren().clear();
        currentState = MenuState.LOGIN;
        navigator = new NavigationManager();

        Label title = createTitle("KÜTÜPHANE GİRİŞİ");

        TextField idField = new TextField();
        idField.setPromptText("Kullanıcı Adı (Örn: std01)");
        idField.setStyle(INPUT_STYLE);
        idField.setMaxWidth(300);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Şifre");
        passField.setStyle(INPUT_STYLE);
        passField.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #f38ba8;");

        Button loginBtn = createButton("GİRİŞ YAP");
        loginBtn.setOnAction(event -> {
            long startTime = System.nanoTime();
            User tempUser = db.getUser(idField.getText().trim());
            // Şifre kontrolü yapıyoruz
            if (tempUser != null && tempUser.getPassword().equals(passField.getText().trim())) {
                user = tempUser;
                if (tempUser.getUserType() == com.library.model.UserType.ADMIN) {
                    changeScreen(MenuState.ADMIN_DASHBOARD);
                } else {
                    changeScreen(MenuState.DASHBOARD);
                }
            } else {
                errorLabel.setText("Hatalı Kullanıcı Adı veya Şifre!");
                long endTime = System.nanoTime();
                timeLabel.setText(String.format("İşlem süresi: %.2f ms", (endTime - startTime) / 1_000_000.0));
            }
        });

        root.getChildren().addAll(title, idField, passField, loginBtn, errorLabel);
    }

    private void showDashboard() {
        root.getChildren().clear();

        Label title = createTitle("KULLANICI PANELİ");
        Label welcome = createLabel("Hoş Geldin, " + user.getId());

        Button topBooksBtn = createButton("En Popüler 10 Kitap");
        topBooksBtn.setOnAction(event -> changeScreen(MenuState.TOP_BOOKS));

        Button searchBtn = createButton("Kitap Ara");
        searchBtn.setOnAction(event -> changeScreen(MenuState.SEARCH));

        Button benchmarkBtn = createButton("Performans Testi");
        benchmarkBtn.setOnAction(event -> changeScreen(MenuState.BENCHMARK));

        Button logoutBtn = createDangerButton("Çıkış Yap");
        logoutBtn.setOnAction(event -> {
            // BUG-009 FIX: clear user on logout
            user = null;
            book = null;
            showLoginScreen();
        });

        root.getChildren().addAll(title, welcome, topBooksBtn, searchBtn, benchmarkBtn, logoutBtn);
    }

    private void showAdminDashboard() {
        root.getChildren().clear();

        Label title = createTitle("YÖNETİCİ PANELİ");
        Label welcome = createLabel("Hoş Geldin, " + user.getId());

        Button viewBorrowsBtn = createButton("Ödünç Alanlar & Kuyruklar");
        viewBorrowsBtn.setOnAction(event -> changeScreen(MenuState.ADMIN_BORROWS));

        Button topBooksBtn = createButton("En Popüler 10 Kitap");
        topBooksBtn.setOnAction(event -> changeScreen(MenuState.TOP_BOOKS));

        Button searchBtn = createButton("Kitap Ara");
        searchBtn.setOnAction(event -> changeScreen(MenuState.SEARCH));

        Button benchmarkBtn = createButton("Performans Testi");
        benchmarkBtn.setOnAction(event -> changeScreen(MenuState.BENCHMARK));

        Button logoutBtn = createDangerButton("Çıkış Yap");
        logoutBtn.setOnAction(event -> {
            user = null;
            book = null;
            showLoginScreen();
        });

        root.getChildren().addAll(title, welcome, viewBorrowsBtn, topBooksBtn, searchBtn, benchmarkBtn, logoutBtn);
    }

    private void showAdminBorrows() {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = createTitle("ÖDÜNÇ ALANLAR VE KUYRUKLAR");

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(380);
        listView.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e; -fx-text-fill: #cdd6f4;");
        
        // Tüm kitapları gezip kuyrukta bekleyen var mı diye bakıyoruz
        for (Book bookItem : db.getAllBooks()) {
            if (!bookItem.getUniqueReaders().isEmpty() || !bookItem.getQueue().isEmpty()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Kitap: ").append(bookItem.getTitle()).append("\n");
                builder.append("  Okuyanlar: ").append(bookItem.getUniqueReaders().toString()).append("\n");
                builder.append("  Bekleme Kuyruğu (Priority Queue): ");
                if (bookItem.getQueue().isEmpty()) {
                    builder.append("Yok");
                } else {
                    List<User> queueList = bookItem.getQueue().getQueueList();
                    for (int i=0; i<queueList.size(); i++) {
                        builder.append(queueList.get(i).getId()).append(" (").append(queueList.get(i).getUserType()).append(")");
                        if (i < queueList.size() - 1) builder.append(", ");
                    }
                }
                listView.getItems().add(builder.toString());
            }
        }

        if (listView.getItems().isEmpty()) {
            listView.getItems().add("Aktif ödünç alma veya bekleme kuyruğu bulunamadı.");
        }

        Button backBtn = createButton("Geri");
        backBtn.setOnAction(event -> goBack());

        root.getChildren().addAll(title, listView, backBtn);
    }

    private void showTopBooks() {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = createTitle("EN POPÜLER 10 KİTAP");

        ListView<Book> listView = new ListView<>();
        listView.setPrefHeight(330);
        listView.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e;");

        // Sabit boyutlu diziden (Array) okuyoruz — ArrayList değil!
        Book[] topArray = db.getTopBooksArray();
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
                    setText((getIndex() + 1) + ". " + book.getTitle() + "   [" + book.getBorrowCount() + " kez okunmuş]");
                    if (isSelected()) {
                        setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    }
                }
            }
        });

        Button viewDetailsBtn = createButton("Detayları Gör");
        viewDetailsBtn.setDisable(true);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) ->
                viewDetailsBtn.setDisable(newVal == null));

        viewDetailsBtn.setOnAction(event -> {
            Book selectedItem = listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                book = selectedItem;
                changeScreen(MenuState.BOOK_DETAILS);
            }
        });

        Button backBtn = createButton("Geri");
        backBtn.setOnAction(event -> goBack());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, viewDetailsBtn);

        root.getChildren().addAll(title, listView, buttonBox);
    }

    private void showSearchScreen() {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = createTitle("KİTAP ARAMA EKRANI");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("İsme Göre", "ISBN'ye Göre", "Türe Göre");
        typeBox.setStyle(INPUT_STYLE);

        typeBox.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(item); setStyle("-fx-text-fill: #cdd6f4;"); }
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Aranacak kelime...");
        searchField.setStyle(INPUT_STYLE);
        searchField.setPrefWidth(200);

        ComboBox<String> genreBox = new ComboBox<>();
        genreBox.setStyle(INPUT_STYLE);
        genreBox.setPrefWidth(120);
        genreBox.setVisible(false);
        genreBox.setManaged(false);
        genreBox.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(item); setStyle("-fx-text-fill: #cdd6f4;"); }
            }
        });

        ComboBox<String> subGenreBox = new ComboBox<>();
        subGenreBox.setStyle(INPUT_STYLE);
        subGenreBox.setPrefWidth(120);
        subGenreBox.setVisible(false);
        subGenreBox.setManaged(false);
        subGenreBox.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(item); setStyle("-fx-text-fill: #cdd6f4;"); }
            }
        });

        Button searchBtn = createButton("Ara");
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

        // Tür araması seçilirse (Ağaç - Tree kullanılacak)
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

                genreBox.getItems().setAll(db.getBookTree().getGenres());
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
                if (query.isEmpty()) { noResultLabel.setText("Lütfen aramak için bir kelime yazın."); return; }
                resultList.getItems().addAll(db.searchByName(query));
            } else if (type == 1) { // ISBN'ye Göre (BST ARAMA KISMI)
                String query = searchField.getText().trim();
                if (query.isEmpty()) { noResultLabel.setText("Lütfen bir ISBN numarası girin."); return; }
                // DİKKAT: Burada hocanın zorunlu tuttuğu İkili Arama Ağacı (BST) kullanılıyor! O(log n) hızında bulur.
                Book foundBook = db.searchByIsbn(query);
                if (foundBook != null) resultList.getItems().add(foundBook);
            } else if (type == 2) { // Türe Göre (TREE ARAMA KISMI)
                String selectedGenre = genreBox.getValue();
                String selectedSubGenre = subGenreBox.getValue();
                if (selectedGenre != null) {
                    resultList.getItems().addAll(db.getBookTree().getBooksByGenre(selectedGenre, selectedSubGenre));
                }
            }

            if (resultList.getItems().isEmpty()) {
                noResultLabel.setText("Maalesef eşleşen kitap bulunamadı.");
            }
            long endTime = System.nanoTime();
            timeLabel.setText(String.format("Arama süresi: %.2f ms", (endTime - startTime) / 1_000_000.0));
        };

        genreBox.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != null) {
                subGenreBox.getItems().setAll(db.getBookTree().getSubGenres(newVal));
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

        typeBox.getSelectionModel().selectFirst(); // trigger default UI state

        Button viewDetailsBtn = createButton("Detayları Gör");
        viewDetailsBtn.setDisable(true);

        resultList.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) ->
                viewDetailsBtn.setDisable(newVal == null));

        viewDetailsBtn.setOnAction(event -> {
            Book selectedItem = resultList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                book = selectedItem;
                changeScreen(MenuState.BOOK_DETAILS);
            }
        });

        Button backBtn = createButton("Geri");
        backBtn.setOnAction(event -> goBack());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, viewDetailsBtn);

        root.getChildren().addAll(title, searchBox, noResultLabel, resultList, buttonBox);
    }

    // Detay sayfası (İşlem sonrası mesaj göstermek için statusMessage alıyor)
    private void showBookDetails(String statusMessage) {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_LEFT);

        Label title = createTitle("KİTAP DETAYLARI");

        // Kopyalanabilir metin alanları kullanıyoruz — seçip kopyalama yapılabilir
        VBox infoBox = new VBox(5);
        infoBox.getChildren().addAll(
            createCopyableField("Kitap Adı:  " + book.getTitle()),
            createCopyableField("Yazar:      " + book.getAuthor()),
            createCopyableField("ISBN:       " + book.getIsbn()),
            createCopyableField("Tür:        " + book.getGenre()),
            createCopyableField("Alt Tür:    " + book.getSubGenre()),
            createCopyableField("Okunma:     " + book.getBorrowCount() + " kez")
        );

        TextField statusField = createCopyableField("Durum:      " + (book.isAvailable() ? "RAFTA (ALINABİLİR)" : "KULLANIMDA (ÖDÜNÇ ALINMIŞ)"));
        statusField.setStyle(book.isAvailable()
                ? "-fx-text-fill: #a6e3a1; -fx-background-color: transparent; -fx-border-color: transparent;"
                : "-fx-text-fill: #f38ba8; -fx-background-color: transparent; -fx-border-color: transparent;");
        statusField.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        infoBox.getChildren().add(statusField);

        // Lokasyon alanı kaldırıldı

        // --- Recommendations (GRAF YAPISI BURADA KULLANILIYOR) ---
        Label recTitle = createTitle("BUNU OKUYANLAR ŞUNLARI DA OKUDU");
        recTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox recBox = new VBox(5);
        List<String> recs = db.getLibraryGraph().getRecommendations(book.getIsbn(), 3);
        if (recs.isEmpty()) {
            recBox.getChildren().add(createLabel("Henüz öneri yok. Ağ oluşturmak için daha fazla kitap ödünç alın!"));
        } else {
            for (String recIsbn : recs) {
                Book recommendedBook = db.getBookByIsbnMap(recIsbn);
                if (recommendedBook != null) recBox.getChildren().add(createCopyableField("- " + recommendedBook.getTitle()));
            }
        }

        // --- İşlem Sonucu Mesajı ---
        Label msgLabel = new Label(statusMessage != null ? statusMessage : "");
        msgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        if (statusMessage != null && statusMessage.startsWith("Başarılı")) {
            msgLabel.setStyle("-fx-text-fill: #a6e3a1;");
        } else {
            msgLabel.setStyle("-fx-text-fill: #f9e2af;");
        }

        // --- Actions ---
        Button borrowBtn = createButton("Kitabı Ödünç Al");
        borrowBtn.setOnAction(event -> {
            long startTime = System.nanoTime();
            String msg;
            if (book.isAvailable()) {
                boolean isNew = book.getUniqueReaders().add(user.getId());
                if (isNew) {
                    book.setBorrowCount(book.getBorrowCount() + 1);
                }
                book.getBorrowHistory().add(new BorrowHistory(user.getId(), LocalDate.now()));
                book.setAvailable(false);
                user.addReadIsbn(book.getIsbn());
                db.getLibraryGraph().addCoRead(user.getReadIsbns());
                db.updateTopBooks(); // Ödünç alma sonrası diziyi güncelle
                msg = "Başarılı! Kitabı ödünç alan: " + user.getId();
            } else {
                // Öncelikli Kuyruk mantığı burada devreye giriyor!
                boolean added = book.getQueue().enqueue(user);
                msg = added
                    ? "Kitap rafta yok. Öncelikli bekleme listesine eklendiniz."
                    : "Zaten bu kitap için sırada bekliyorsunuz.";
            }
            showBookDetails(msg);
            long endTime = System.nanoTime();
            timeLabel.setText(String.format("İşlem süresi: %.2f ms", (endTime - startTime) / 1_000_000.0));
        });

        Button backBtn = createButton("Geri");
        backBtn.setOnAction(event -> goBack());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(borrowBtn, backBtn);

        root.getChildren().addAll(title, infoBox, recTitle, recBox, msgLabel, buttonBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
