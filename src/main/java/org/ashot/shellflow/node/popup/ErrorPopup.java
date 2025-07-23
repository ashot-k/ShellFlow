package org.ashot.shellflow.node.popup;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.constant.TextStyleClass;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.util.Set;

public class ErrorPopup extends Stage {
    StyleClassedTextArea styleClassedTextArea = new StyleClassedTextArea();
    boolean criticalError = false;
    Button closeButton = new Button();

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

    private void showPopup() {
        Platform.runLater(() -> {
            if (criticalError) {
                this.setOnCloseRequest((_) -> Main.getPrimaryStage().close());
                this.closeButton.setOnAction((_) -> {
                    this.close();
                    Main.getPrimaryStage().close();
                    Platform.exit();
                });
                this.initModality(Modality.WINDOW_MODAL);
                this.initOwner(Main.getPrimaryStage().getOwner());
                this.setTitle("Critical Error");
                this.closeButton.setText("Exit");
                this.showAndWait();
            } else {
                this.show();
            }
        });
    }

    private void setupErrorPopup(String msg, String highlighted) {
        styleClassedTextArea.setWrapText(true);
        styleClassedTextArea.setEditable(false);
        styleClassedTextArea.setPrefWidth(500); // or 600 if you want default max
        styleClassedTextArea.setBackground(Background.EMPTY);
        styleClassedTextArea.setStyleClass(0, msg.length(), TextStyleClass.getTextColorClass());
        VBox.setVgrow(styleClassedTextArea, Priority.ALWAYS);
        styleClassedTextArea.appendText(msg + "\n");
        if (highlighted != null) {
            styleClassedTextArea.append(highlighted, TextStyleClass.getErrorTextColorClass());
        }
        styleClassedTextArea.setParagraphStyle(1, Set.of("centered-highlighted-text"));

        closeButton = new Button("Close");
        closeButton.setOnAction(_ -> this.close());
        closeButton.setPrefWidth(100);

        VBox errorContainer = new VBox(styleClassedTextArea, closeButton);
        errorContainer.setAlignment(Pos.CENTER);
        errorContainer.getStyleClass().add("error-message");
        errorContainer.setFillWidth(true);

        Scene scene = new Scene(errorContainer);
        scene.getStylesheets().addAll(Main.getUserAgentStylesheet(), Main.class.getResource("main.css").toExternalForm());
        this.setScene(scene);
        this.setTitle("Error");
        this.setResizable(false);
        this.setMaxWidth(900);
        showPopup();
    }
}
