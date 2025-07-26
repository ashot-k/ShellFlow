package org.ashot.shellflow.node.popup;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.constant.TextStyleClass;
import org.ashot.shellflow.node.icon.Icons;


public class ErrorPopup extends Stage {
    TextFlow styleClassedTextArea = new TextFlow();
    Text message = new Text();
    Text hightlightText = new Text();
    boolean criticalError = false;
    Button closeButton = new Button();

    public ErrorPopup() {
        setupErrorPopup("", null);
    }
    public ErrorPopup(boolean criticalError) {
        this.criticalError = criticalError;
        setupErrorPopup("", null);
    }

    public ErrorPopup(String msg) {
        setupErrorPopup(msg, null);
    }

    public ErrorPopup(String msg, boolean criticalError) {
        this.criticalError = criticalError;
        setupErrorPopup(msg, null);
    }

    public ErrorPopup(String msg, String highlightedMessage) {
        setupErrorPopup(msg, highlightedMessage);
    }

    public ErrorPopup(String msg, String highlightedMessage, boolean criticalError) {
        this.criticalError = criticalError;
        setupErrorPopup(msg, highlightedMessage);
    }

    public void showPopup() {
        Platform.runLater(() -> {
            if (criticalError) {
                this.setOnCloseRequest(_ -> Main.getPrimaryStage().close());
                this.closeButton.setOnAction(_ -> {
                    this.close();
                    Main.getPrimaryStage().close();
                    Platform.exit();
                });
                this.setTitle("Critical Error");
                this.closeButton.setText("Exit");
                this.showAndWait();
            } else {
                super.show();
            }
        });
    }

    public void setMessage(String msg, String highlighted){
        message.setText(msg + "\n");
        hightlightText.setText(highlighted + "\n");
    }

    private void setupErrorPopup(String msg, String highlighted) {
        message.setWrappingWidth(50);
        message.setText(msg);
        styleClassedTextArea.setPrefWidth(400);
        styleClassedTextArea.setPrefHeight(20);
        styleClassedTextArea.setMaxHeight(400);
        styleClassedTextArea.setBackground(Background.EMPTY);
        styleClassedTextArea.getStyleClass().add(TextStyleClass.getTextColorClass());
        styleClassedTextArea.getChildren().add(message);
        VBox.setVgrow(styleClassedTextArea, Priority.ALWAYS);
        HBox.setHgrow(styleClassedTextArea, Priority.ALWAYS);

        if (highlighted != null) {
            hightlightText.setText(highlighted);
            hightlightText.getStyleClass().add(TextStyleClass.getErrorTextColorClass());
            styleClassedTextArea.getChildren().add(hightlightText);
        }

        closeButton = new Button("Close");
        closeButton.setFont(Font.font(14));
        closeButton.setOnAction(_ -> this.close());
        closeButton.setPrefWidth(80);

        HBox iconMsgContainer = new HBox(10, Icons.getErrorIcon(32), styleClassedTextArea);
        iconMsgContainer.setPadding(new Insets(0, 15, 0, 15));
        iconMsgContainer.setAlignment(Pos.TOP_LEFT);
        iconMsgContainer.setFillHeight(true);

        VBox errorContainer = new VBox(iconMsgContainer, closeButton);
        errorContainer.setAlignment(Pos.CENTER);
        errorContainer.getStyleClass().add("error-popup");
        errorContainer.setFillWidth(true);
        errorContainer.setMaxHeight(600);

        Scene scene = new Scene(errorContainer);
        scene.getStylesheets().addAll(Application.getUserAgentStylesheet(), Main.CSS_FILE_LOCATION);
        getIcons().clear();
        initModality(Modality.APPLICATION_MODAL);
        initOwner(Main.getPrimaryStage().getOwner());
        setScene(scene);
        setTitle("Error");
        setResizable(false);
        setMaxWidth(900);
    }
}
