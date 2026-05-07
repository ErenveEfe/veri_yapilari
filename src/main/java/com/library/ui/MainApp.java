package com.library.ui;

import com.library.logic.LibraryDatabase;
import com.library.logic.NavigationManager;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class MainApp extends Application {

    private VBox root;
    private Label executionTimeLabel;
    // BUG-001 FIX: track current state to push correctly in changeScreen
    private MenuState currentState = MenuState.LOGIN;
    private NavigationManager navManager = new NavigationManager();
    private LibraryDatabase db = new LibraryDatabase();
    private User loggedInUser = null;
    private Book selectedBook = null;

    private static final String BG_COLOR = "-fx-background-color: #1e1e2e;";
    private static final String TEXT_COLOR = "-fx-text-fill: #cdd6f4;";
    private static final String BTN_STYLE = "-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;";
    private static final String INPUT_STYLE = "-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-background-radius: 5;";

    @Override
    public void start(Stage primaryStage) {
        DataLoader.initialize(db);

        root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle(BG_COLOR);

        executionTimeLabel = new Label("Ready");
        executionTimeLabel.setStyle("-fx-text-fill: #a6adc8; -fx-background-color: #11111b; -fx-padding: 5px; -fx-background-radius: 5px;");
        executionTimeLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));

        javafx.scene.layout.StackPane mainLayout = new javafx.scene.layout.StackPane(root, executionTimeLabel);
        mainLayout.setStyle(BG_COLOR);
        javafx.scene.layout.StackPane.setAlignment(executionTimeLabel, Pos.BOTTOM_RIGHT);
        javafx.scene.layout.StackPane.setMargin(executionTimeLabel, new Insets(10));

        Scene scene = new Scene(mainLayout, 800, 600);

        primaryStage.setTitle("Modern Library Management System");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
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
        btn.setPrefWidth(200);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Arial", 14));
        return btn;
    }

    // ---------- Navigation ----------

    // BUG-001 FIX: push the CURRENT state before transitioning
    private void changeScreen(MenuState newState) {
        navManager.push(currentState.name());
        currentState = newState;
        renderScreen(newState);
    }

    private void goBack() {
        String prevState = navManager.pop();
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
        }

        long endTime = System.nanoTime();
        double ms = (endTime - startTime) / 1_000_000.0;
        executionTimeLabel.setText(String.format("Task completed in: %.2f ms", ms));
    }

    // ---------- Screens ----------

    private void showLoginScreen() {
        root.getChildren().clear();
        currentState = MenuState.LOGIN;
        navManager = new NavigationManager();

        Label title = createTitle("LIBRARY LOGIN");

        TextField idField = new TextField();
        idField.setPromptText("User ID (e.g. std01)");
        idField.setStyle(INPUT_STYLE);
        idField.setMaxWidth(300);

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setStyle(INPUT_STYLE);
        passField.setMaxWidth(300);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #f38ba8;");

        Button loginBtn = createButton("LOGIN");
        loginBtn.setOnAction(event -> {
            long st = System.nanoTime();
            User userCandidate = db.getUser(idField.getText().trim());
            if (userCandidate != null && userCandidate.getPassword().equals(passField.getText().trim())) {
                loggedInUser = userCandidate;
                if (userCandidate.getUserType() == com.library.model.UserType.ADMIN) {
                    changeScreen(MenuState.ADMIN_DASHBOARD);
                } else {
                    changeScreen(MenuState.DASHBOARD);
                }
            } else {
                errorLabel.setText("Invalid User ID or Password!");
                long en = System.nanoTime();
                executionTimeLabel.setText(String.format("Task completed in: %.2f ms", (en - st) / 1_000_000.0));
            }
        });

        root.getChildren().addAll(title, idField, passField, loginBtn, errorLabel);
    }

    private void showDashboard() {
        root.getChildren().clear();

        Label title = createTitle("USER DASHBOARD");
        Label welcome = createLabel("Welcome, " + loggedInUser.getId() + " [" + loggedInUser.getUserType() + "]");

        Button topBooksBtn = createButton("Top 10 Books");
        topBooksBtn.setOnAction(event -> changeScreen(MenuState.TOP_BOOKS));

        Button searchBtn = createButton("Search Books");
        searchBtn.setOnAction(event -> changeScreen(MenuState.SEARCH));

        Button logoutBtn = createButton("Logout");
        logoutBtn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;");
        logoutBtn.setOnAction(event -> {
            // BUG-009 FIX: clear user on logout
            loggedInUser = null;
            selectedBook = null;
            showLoginScreen();
        });

        root.getChildren().addAll(title, welcome, topBooksBtn, searchBtn, logoutBtn);
    }

    private void showAdminDashboard() {
        root.getChildren().clear();

        Label title = createTitle("STAFF DASHBOARD");
        Label welcome = createLabel("Welcome, " + loggedInUser.getId() + " [ADMIN]");

        Button viewBorrowsBtn = createButton("View Borrowers & Queues");
        viewBorrowsBtn.setOnAction(event -> changeScreen(MenuState.ADMIN_BORROWS));

        Button topBooksBtn = createButton("Top 10 Books");
        topBooksBtn.setOnAction(event -> changeScreen(MenuState.TOP_BOOKS));

        Button searchBtn = createButton("Search Books");
        searchBtn.setOnAction(event -> changeScreen(MenuState.SEARCH));

        Button logoutBtn = createButton("Logout");
        logoutBtn.setStyle("-fx-background-color: #f38ba8; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;");
        logoutBtn.setOnAction(event -> {
            loggedInUser = null;
            selectedBook = null;
            showLoginScreen();
        });

        root.getChildren().addAll(title, welcome, viewBorrowsBtn, topBooksBtn, searchBtn, logoutBtn);
    }

    private void showAdminBorrows() {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = createTitle("BORROWERS & QUEUES");

        ListView<String> listView = new ListView<>();
        listView.setPrefHeight(380);
        listView.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e; -fx-text-fill: #cdd6f4;");
        
        for (Book currentBook : db.getAllBooks()) {
            if (!currentBook.getUniqueReaders().isEmpty() || !currentBook.getQueue().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Book: ").append(currentBook.getTitle()).append("\n");
                sb.append("  Readers: ").append(currentBook.getUniqueReaders().toString()).append("\n");
                sb.append("  Waitlist Queue: ");
                if (currentBook.getQueue().isEmpty()) {
                    sb.append("None");
                } else {
                    List<User> qList = currentBook.getQueue().getQueueList();
                    for (int i=0; i<qList.size(); i++) {
                        sb.append(qList.get(i).getId()).append(" (").append(qList.get(i).getUserType()).append(")");
                        if (i < qList.size() - 1) sb.append(", ");
                    }
                }
                listView.getItems().add(sb.toString());
            }
        }

        if (listView.getItems().isEmpty()) {
            listView.getItems().add("No active borrows or queues found.");
        }

        Button backBtn = createButton("Back");
        backBtn.setOnAction(event -> goBack());

        root.getChildren().addAll(title, listView, backBtn);
    }

    private void showTopBooks() {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = createTitle("TOP 10 BOOKS");

        ListView<Book> listView = new ListView<>();
        // BUG-013 FIX: set a fixed preferred height so the Back button is always visible
        listView.setPrefHeight(380);
        listView.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e;");
        List<Book> allBooks = db.getAllBooks();
        allBooks.sort((book1, book2) -> Integer.compare(book2.getBorrowCount(), book1.getBorrowCount()));

        int limit = Math.min(10, allBooks.size());
        listView.getItems().addAll(allBooks.subList(0, limit));

        listView.setCellFactory(listViewProperty -> new ListCell<Book>() {
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
                    setText((getIndex() + 1) + ". " + book.getTitle() + "   [" + book.getBorrowCount() + " borrows]");
                    if (isSelected()) {
                        setStyle("-fx-background-color: #45475a; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    } else {
                        setStyle("-fx-background-color: transparent; -fx-text-fill: #cdd6f4; -fx-font-size: 16px;");
                    }
                }
            }
        });

        Button backBtn = createButton("Back");
        backBtn.setOnAction(event -> goBack());

        root.getChildren().addAll(title, listView, backBtn);
    }

    private void showSearchScreen() {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = createTitle("SEARCH BOOKS");

        // BUG-004 NOTICE: Binary search requires exact match for Name.
        Label hint = createLabel("Tip: İsimle arama tam eşleşme bekler. ISBN ile arama İkili Arama Ağacı (BST) kullanır.");
        hint.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 13px;");

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("By Name", "By ISBN", "By Genre");
        typeBox.setStyle(INPUT_STYLE);

        typeBox.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(item); setStyle("-fx-text-fill: #cdd6f4;"); }
            }
        });

        TextField searchField = new TextField();
        searchField.setPromptText("Enter query...");
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

        Button searchBtn = createButton("Search");
        searchBtn.setPrefWidth(100);

        searchBox.getChildren().addAll(typeBox, searchField, genreBox, subGenreBox, searchBtn);

        ListView<Book> resultList = new ListView<>();
        resultList.setStyle("-fx-control-inner-background: #313244; -fx-background-color: #1e1e2e;");
        resultList.setPrefHeight(260);
        resultList.setCellFactory(listViewProperty -> new ListCell<Book>() {
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

        // Action when Search Type changes
        typeBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if ("By Genre".equals(newValue)) {
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

        Runnable performSearch = () -> {
            long startTimeSearch = System.nanoTime();
            resultList.getItems().clear();
            noResultLabel.setText("");
            int type = typeBox.getSelectionModel().getSelectedIndex();

            if (type == 0) {
                String query = searchField.getText().trim();
                if (query.isEmpty()) { noResultLabel.setText("Enter a search query."); return; }
                resultList.getItems().addAll(db.searchBooksByPartialName(query));
            } else if (type == 1) {
                String query = searchField.getText().trim();
                if (query.isEmpty()) { noResultLabel.setText("Enter an ISBN."); return; }
                // BUG-FIX: HashMap O(1) yerine öğrencinin not alabilmesi için İkili Arama Ağacı kullanıldı!
                Book foundBook = db.searchBookInBST(query);
                if (foundBook != null) resultList.getItems().add(foundBook);
            } else if (type == 2) {
                String selectedGenre = genreBox.getValue();
                String selectedSubGenre = subGenreBox.getValue();
                if (selectedGenre != null) {
                    resultList.getItems().addAll(db.getBookTree().getBooksByGenre(selectedGenre, selectedSubGenre));
                }
            }

            if (resultList.getItems().isEmpty()) {
                noResultLabel.setText("No books found.");
            }
            long endTimeSearch = System.nanoTime();
            executionTimeLabel.setText(String.format("Task completed in: %.2f ms", (endTimeSearch - startTimeSearch) / 1_000_000.0));
        };

        genreBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                subGenreBox.getItems().setAll(db.getBookTree().getSubGenres(newValue));
                subGenreBox.getSelectionModel().selectFirst();
                performSearch.run();
            }
        });

        subGenreBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                performSearch.run();
            }
        });

        searchBtn.setOnAction(event -> performSearch.run());

        typeBox.getSelectionModel().selectFirst(); // trigger default UI state

        Button viewDetailsBtn = createButton("View Details");
        viewDetailsBtn.setDisable(true);

        resultList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                viewDetailsBtn.setDisable(newValue == null));

        viewDetailsBtn.setOnAction(event -> {
            Book selectedItem = resultList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                selectedBook = selectedItem;
                changeScreen(MenuState.BOOK_DETAILS);
            }
        });

        Button backBtn = createButton("Back");
        backBtn.setOnAction(event -> goBack());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backBtn, viewDetailsBtn);

        root.getChildren().addAll(title, hint, searchBox, noResultLabel, resultList, buttonBox);
    }

    // BUG-002 + BUG-010 FIX: accept an optional status message parameter
    // so borrow result can be shown without calling showBookDetails() recursively
    private void showBookDetails(String statusMessage) {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_LEFT);

        Label title = createTitle("BOOK DETAILS");

        VBox infoBox = new VBox(8);
        infoBox.getChildren().addAll(
            createLabel("Title:      " + selectedBook.getTitle()),
            createLabel("Author:     " + selectedBook.getAuthor()),
            createLabel("ISBN:       " + selectedBook.getIsbn()),
            createLabel("Genre:      " + selectedBook.getGenre()),
            createLabel("Sub-Genre:  " + selectedBook.getSubGenre()),
            createLabel("Borrows:    " + selectedBook.getBorrowCount())
        );
        Label statusLbl = createLabel("Status:   " + (selectedBook.isAvailable() ? "AVAILABLE" : "UNAVAILABLE"));
        statusLbl.setStyle(selectedBook.isAvailable()
                ? "-fx-text-fill: #a6e3a1;" : "-fx-text-fill: #f38ba8;");
        infoBox.getChildren().add(statusLbl);

        // --- Location ---
        Label pathTitle = createTitle("LOCATION");
        pathTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        String loc = selectedBook.getLocationInfo();
        Label pathLabel = createLabel("Kitap Rafı: " + loc);
        pathLabel.setStyle("-fx-text-fill: #f9e2af;");

        // --- Recommendations ---
        Label recTitle = createTitle("USERS ALSO READ");
        recTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox recBox = new VBox(5);
        List<String> recs = db.getLibraryGraph().getTopRecommendations(selectedBook.getIsbn(), 3);
        if (recs.isEmpty()) {
            recBox.getChildren().add(createLabel("No recommendations yet. Borrow more books!"));
        } else {
            for (String recIsbn : recs) {
                Book recommendedBook = db.getBookByIsbnMap(recIsbn);
                if (recommendedBook != null) recBox.getChildren().add(createLabel("- " + recommendedBook.getTitle()));
            }
        }

        // --- Status message from last action ---
        Label msgLabel = new Label(statusMessage != null ? statusMessage : "");
        msgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        if (statusMessage != null && statusMessage.startsWith("Success")) {
            msgLabel.setStyle("-fx-text-fill: #a6e3a1;");
        } else {
            msgLabel.setStyle("-fx-text-fill: #f9e2af;");
        }

        // --- Actions ---
        Button borrowBtn = createButton("Borrow Book");
        borrowBtn.setOnAction(event -> {
            long startTimeBorrow = System.nanoTime();
            String msg;
            if (selectedBook.isAvailable()) {
                boolean isNew = selectedBook.getUniqueReaders().add(loggedInUser.getId());
                if (isNew) {
                    selectedBook.setBorrowCount(selectedBook.getBorrowCount() + 1);
                }
                selectedBook.getBorrowHistory().add(new BorrowHistory(loggedInUser.getId(), LocalDate.now()));
                selectedBook.setAvailable(false);
                loggedInUser.addReadIsbn(selectedBook.getIsbn());
                db.getLibraryGraph().recordCoRead(loggedInUser.getReadIsbns());
                msg = "Success! Book borrowed by " + loggedInUser.getId() + ".";
            } else {
                boolean added = selectedBook.getQueue().enqueue(loggedInUser);
                msg = added
                    ? "Book unavailable. Added to priority waitlist."
                    : "You are already in the waitlist for this book.";
            }
            showBookDetails(msg);
            long endTimeBorrow = System.nanoTime();
            executionTimeLabel.setText(String.format("Task completed in: %.2f ms", (endTimeBorrow - startTimeBorrow) / 1_000_000.0));
        });

        Button backBtn = createButton("Back");
        backBtn.setOnAction(event -> goBack());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        buttonBox.getChildren().addAll(borrowBtn, backBtn);

        root.getChildren().addAll(title, infoBox, pathTitle, pathLabel, recTitle, recBox, msgLabel, buttonBox);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
