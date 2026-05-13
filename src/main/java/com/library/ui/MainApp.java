package com.library.ui;

import com.library.logic.LibraryDatabase;
import com.library.logic.NavigationManager;
import com.library.model.Book;
import com.library.model.User;
import com.library.util.DataLoader;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainApp extends Application {

    private VBox root;
    private Label timeLabel;
    private MenuState currentState = MenuState.LOGIN;
    private NavigationManager navigator = new NavigationManager();
    private LibraryDatabase db = new LibraryDatabase();
    private User user = null;
    private Book book = null;

    @Override
    public void start(Stage primaryStage) {
        DataLoader.initialize(db);

        root = new VBox(20);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle(UIUtils.BG_COLOR);

        timeLabel = new Label("Hazır");
        timeLabel.setStyle(
                "-fx-text-fill: #a6adc8; -fx-background-color: #11111b; -fx-padding: 5px; -fx-background-radius: 5px;");
        timeLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));

        javafx.scene.layout.StackPane mainLayout = new javafx.scene.layout.StackPane(root, timeLabel);
        mainLayout.setStyle(UIUtils.BG_COLOR);
        javafx.scene.layout.StackPane.setAlignment(timeLabel, Pos.BOTTOM_RIGHT);
        javafx.scene.layout.StackPane.setMargin(timeLabel, new Insets(10));

        Scene scene = new Scene(mainLayout, 900, 650);

        primaryStage.setTitle("Kütüphane Yönetim Sistemi");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        new LoginScreen(this).render();
    }

    public void changeScreen(MenuState newState) {
        navigator.push(currentState.name());
        currentState = newState;
        renderScreen(newState);
    }

    public void goBack() {
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
        root.setSpacing(20);

        switch (state) {
            case LOGIN:
                new LoginScreen(this).render();
                break;
            case DASHBOARD:
                new DashboardScreen(this).render();
                break;
            case TOP_BOOKS:
                new TopBooksScreen(this).render();
                break;
            case SEARCH:
                new SearchScreen(this).render();
                break;
            case BOOK_DETAILS:
                new BookDetailsScreen(this).render(null);
                break;
            case ADMIN_DASHBOARD:
                new AdminDashboardScreen(this).render();
                break;
            case ADMIN_BORROWS:
                new AdminBorrowsScreen(this).render();
                break;
            case BENCHMARK:
                BenchmarkScreen.render(root, this::goBack);
                break;
        }

        long endTime = System.nanoTime();
        double ms = (endTime - startTime) / 1_000_000.0;

        // sağ alttaki label a işlem süresini yazar.
        timeLabel.setText(String.format("İşlem süresi: %.2f ms", ms));
    }

    public void showBookDetails(String statusMessage) {
        new BookDetailsScreen(this).render(statusMessage);
    }

    public VBox getRoot() { return root; }
    public Label getTimeLabel() { return timeLabel; }
    public LibraryDatabase getDb() { return db; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public void setCurrentState(MenuState state) { this.currentState = state; }
    public void resetNavigator() { this.navigator = new NavigationManager(); }

    public static void main(String[] args) {
        launch(args);
    }
}
