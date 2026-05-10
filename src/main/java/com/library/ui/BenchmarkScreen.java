package com.library.ui;

import com.library.logic.PerformanceBenchmark;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class BenchmarkScreen {

    public static void render(VBox root, Runnable goBackAction) {
        root.getChildren().clear();
        root.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("PERFORMANS KARŞILAŞTIRMASI");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #a6e3a1;");

        Label desc = new Label("Farklı veri yapılarının aynı işlemdeki performans farkını ölçer.");
        desc.setFont(Font.font("Arial", 16));
        desc.setStyle("-fx-text-fill: #6c7086; -fx-font-size: 13px;");

        TableView<PerformanceBenchmark.BenchmarkResult> table = new TableView<>();
        table.setPrefHeight(320);

        TableColumn<PerformanceBenchmark.BenchmarkResult, String> testCol = new TableColumn<>("Test");
        testCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTestName()));
        testCol.setPrefWidth(125);

        TableColumn<PerformanceBenchmark.BenchmarkResult, String> sizeCol = new TableColumn<>("Boyut");
        sizeCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getDataSize())));
        sizeCol.setPrefWidth(75);

        TableColumn<PerformanceBenchmark.BenchmarkResult, String> s1Col = new TableColumn<>("Yapı 1");
        s1Col.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStructure1()));
        s1Col.setPrefWidth(215);

        TableColumn<PerformanceBenchmark.BenchmarkResult, String> t1Col = new TableColumn<>("Süre 1(ms)");
        t1Col.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.4f", c.getValue().getTime1Ms())));
        t1Col.setPrefWidth(85);

        TableColumn<PerformanceBenchmark.BenchmarkResult, String> s2Col = new TableColumn<>("Yapı 2");
        s2Col.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStructure2()));
        s2Col.setPrefWidth(185);

        TableColumn<PerformanceBenchmark.BenchmarkResult, String> t2Col = new TableColumn<>("Süre 2(ms)");
        t2Col.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.4f", c.getValue().getTime2Ms())));
        t2Col.setPrefWidth(85);

        table.getColumns().addAll(testCol, sizeCol, s1Col, t1Col, s2Col, t2Col);

        Label statusLabel = new Label("Testi başlatmak için butona tıklayın.");
        statusLabel.setFont(Font.font("Arial", 16));
        statusLabel.setStyle("-fx-text-fill: #cdd6f4;");

        Button startBtn = createBtn("Testi Başlat");
        startBtn.setOnAction(event -> {
            statusLabel.setText("Test çalışıyor, lütfen bekleyin...");
            new Thread(() -> {
                PerformanceBenchmark benchmark = new PerformanceBenchmark();
                int[] sizes = {200_000};
                List<PerformanceBenchmark.BenchmarkResult> results = benchmark.runAllBenchmarks(sizes);
                Platform.runLater(() -> {
                    table.setItems(FXCollections.observableArrayList(results));
                    statusLabel.setText("Test tamamlandı! (" + results.size() + " karşılaştırma)");
                    statusLabel.setStyle("-fx-text-fill: #a6e3a1;");
                });
            }).start();
        });

        Button backBtn = createBtn("Geri");
        backBtn.setOnAction(event -> goBackAction.run());

        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startBtn, backBtn);

        root.getChildren().addAll(title, desc, statusLabel, table, buttonBox);
    }

    private static Button createBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;");
        btn.setPrefWidth(200);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Arial", 14));
        return btn;
    }
}
