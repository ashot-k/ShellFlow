package org.ashot.shellflow.node.popup;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.Main;

import static java.lang.Double.MAX_VALUE;

public class AlertPopup extends Alert{

    public static final String DEFAULT_CRITICAL_ERROR_TITLE = "Critical Error";

    public AlertPopup(AlertType alertType){
        super(alertType);
        getDialogPane().getScene().getStylesheets().addAll(Application.getUserAgentStylesheet(), Main.CSS_FILE_LOCATION);
    }

    public AlertPopup(String title, String header, String msg, boolean criticalError) {
        super(AlertType.ERROR);
        if(criticalError){
            setupCriticalErrorAlert();
        }
        setupAlertPopup(title, header, msg);
    }

    public AlertPopup(String title, String header, String msg, AlertType alertType) {
        super(alertType);
        setupAlertPopup(title, header, msg);
    }

    private void setupAlertPopup(String title, String header, String msg){
        getDialogPane().getScene().getStylesheets().addAll(Application.getUserAgentStylesheet(), Main.getUserAgentStylesheet());
        setTitle(title);
        setHeaderText(header);
        setContentText(msg);

        TextArea textArea = new TextArea(msg);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setMaxWidth(MAX_VALUE);
        textArea.setMaxHeight(MAX_VALUE);

        VBox content = new VBox(textArea);
        getDialogPane().setExpandableContent(content);

        setWidth(500);
        initOwner(Main.getPrimaryStage().getScene().getWindow());
    }

    private void setupCriticalErrorAlert(){
        setOnCloseRequest(_->{
            close();
            Main.getPrimaryStage().close();
            Platform.exit();
        });
    }

}
