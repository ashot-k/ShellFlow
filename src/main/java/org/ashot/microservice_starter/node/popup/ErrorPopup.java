package org.ashot.microservice_starter.node.popup;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ashot.microservice_starter.Main;

public class ErrorPopup {
    private static Stage errorPopupStage;

    public static Text errorPopup(String msg) {
        Stage errorStage = new Stage();
        VBox errorContainer = new VBox();
        Text text = new Text();
        text.setText(msg);
        text.setWrappingWidth(380);
        Button close = new Button("Close");
        close.setOnAction(actionEvent1 -> {
            errorStage.close();
        });
        errorContainer.getChildren().add(text);
        errorContainer.getChildren().add(close);
        errorContainer.getStyleClass().add("error-message");
        Scene scene = new Scene(errorContainer, 400, 150, Color.BLACK);
        scene.getStylesheets().addAll(Main.getUserAgentStylesheet(), Main.class.getResource("main.css").toExternalForm());
        errorStage.setScene(scene);
        errorStage.setTitle("Error");
        errorStage.setResizable(false);
        errorStage.show();
        errorPopupStage = errorStage;
        return text;
    }

    public static void closePopup() {
        errorPopupStage.close();
    }
}
