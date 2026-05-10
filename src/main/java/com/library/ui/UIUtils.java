package com.library.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class UIUtils {

    public static final String BG_COLOR = "-fx-background-color: #1e1e2e;";
    public static final String TEXT_COLOR = "-fx-text-fill: #cdd6f4;";
    public static final String BTN_STYLE = "-fx-background-color: #89b4fa; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;";
    public static final String BTN_DANGER_STYLE = "-fx-background-color: #f38ba8; -fx-text-fill: #11111b; -fx-font-weight: bold; -fx-background-radius: 8;";
    public static final String INPUT_STYLE = "-fx-background-color: #313244; -fx-text-fill: #cdd6f4; -fx-background-radius: 5;";

    public static Label createTitle(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lbl.setStyle("-fx-text-fill: #a6e3a1;");
        return lbl;
    }

    public static Label createLabel(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", 16));
        lbl.setStyle(TEXT_COLOR);
        return lbl;
    }

    public static Button createButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_STYLE);
        btn.setPrefWidth(260);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Arial", 14));
        return btn;
    }

    public static Button createDangerButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(BTN_DANGER_STYLE);
        btn.setPrefWidth(260);
        btn.setPrefHeight(40);
        btn.setFont(Font.font("Arial", 14));
        return btn;
    }

    // metinlerin seçilebilmesi için oluşturuldu.
    public static TextField createCopyableField(String text) {
        TextField field = new TextField(text);
        field.setEditable(false);
        field.setStyle(INPUT_STYLE
                + " -fx-border-color: transparent; -fx-background-color: transparent; -fx-text-fill: #cdd6f4;");
        field.setFont(Font.font("Arial", 15));
        field.setPrefWidth(550);
        return field;
    }
}
