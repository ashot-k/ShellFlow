package org.ashot.shellflow.node.popup;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.ShellFlow;

import static java.lang.Double.MAX_VALUE;

public class AlertPopup extends Alert{

    public static final String DEFAULT_CRITICAL_ERROR_TITLE = "Critical Error";

    public AlertPopup(AlertType alertType){
        super(alertType);
        getDialogPane().getScene().getStylesheets().addAll(Application.getUserAgentStylesheet());
    }

    public AlertPopup(String title, String header, String msg, boolean criticalError) {
        this(title, header, msg, null, criticalError);
    }

    public AlertPopup(String title, String header, String msg, String expendableText, boolean criticalError) {
        super(AlertType.ERROR);
        if(criticalError){
            setupCriticalErrorAlert();
        }
        setupAlertPopup(title, header, msg, expendableText);
    }

    public AlertPopup(String title, String header, String msg, AlertType alertType) {
        this(title, header, msg, null, alertType);
    }

    public AlertPopup(String title, String header, String msg, String expendableText, AlertType alertType) {
        super(alertType);
        setupAlertPopup(title, header, msg, expendableText);
    }

    private void setupAlertPopup(String title, String header, String msg, String expendableText){
        getDialogPane().getScene().getStylesheets().addAll(Application.getUserAgentStylesheet(), ShellFlow.getUserAgentStylesheet());
        setTitle(title);
        setHeaderText(header);
        setContentText(msg);

        TextArea textArea = new TextArea();
        if(expendableText != null){
            textArea.setText(expendableText);
        }else{
            textArea.setText(msg);
        }
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMaxWidth(MAX_VALUE);
        textArea.setMaxHeight(MAX_VALUE);

        VBox content = new VBox(textArea);
        getDialogPane().setExpandableContent(content);

        setWidth(500);
        initOwner(ShellFlow.getPrimaryStage().getScene().getWindow());
    }

    private void setupCriticalErrorAlert(){
        setOnCloseRequest(_->{
            close();
            ShellFlow.getPrimaryStage().close();
            Platform.exit();
        });
    }

}
