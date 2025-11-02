package org.ashot.shellflow.node.popup;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.ShellFlow;

import static java.lang.Double.MAX_VALUE;

public class AlertPopup extends Alert {

    public AlertPopup(String title, String msg, boolean criticalError) {
        super(AlertType.ERROR);
        if (criticalError) {
            setupCriticalErrorAlert();
        }
        setupAlertPopup(title, msg, null);
    }

    public AlertPopup(String title, String msg, String expendableText, boolean criticalError) {
        super(AlertType.ERROR);
        if (criticalError) {
            setupCriticalErrorAlert();
        }
        setupAlertPopup(title, msg, expendableText);
    }

    public AlertPopup(String title, String msg, AlertType alertType) {
        this(title, msg, null, alertType);
    }

    public AlertPopup(String title, String msg, String expendableText, AlertType alertType) {
        super(alertType);
        setupAlertPopup(title, msg, expendableText);
    }

    private void setupAlertPopup(String title, String msg, String expendableText) {
        if (title != null) {
            setTitle(title);
        }
        if (msg != null) {
            setContentText(msg);
        }
        if (expendableText != null) {
            setExpandableText(expendableText);
        }

        setHeaderText(null);
        setStyle();
        if (ShellFlow.getPrimaryStage() != null && ShellFlow.getPrimaryStage().getScene() != null) {
            initOwner(ShellFlow.getPrimaryStage().getScene().getWindow());
        } else {
            initOwner(null);
        }
    }

    private void setStyle() {
        setWidth(500);
        setHeight(200);
        setResizable(true);
    }

    private void setupCriticalErrorAlert() {
        setOnCloseRequest(_ -> {
            close();
            ShellFlow.getPrimaryStage().close();
            Platform.exit();
        });
    }

    public void setExpandableText(String text) {
        TextArea textArea = new TextArea();
        textArea.setText(text);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMaxWidth(MAX_VALUE);
        textArea.setMaxHeight(MAX_VALUE);
        VBox content = new VBox(textArea);
        getDialogPane().setExpandableContent(content);
        getDialogPane().setExpanded(false);
    }

}
