package org.ashot.microservice_starter.popup;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ashot.microservice_starter.Main;

public class OutputPopup {
    private static Stage outputPopupStage;

    public static Text outputPopup(String msg) {
        Stage outputStage = new Stage();
        ScrollPane scrollPane = new ScrollPane();
        VBox outputContainer = new VBox();
        Text text = new Text();
        text.setText(msg);
        outputContainer.getChildren().add(text);
        outputContainer.getStyleClass().add("output-message");
        scrollPane.setContent(outputContainer);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 900, 450, Color.BLACK);
        text.setWrappingWidth(scene.getWidth() - 20);
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                text.setWrappingWidth(newValue.doubleValue() - 20);
            }
        });
        scene.getStylesheets().addAll(Main.getUserAgentStylesheet(), Main.class.getResource("main.css").toExternalForm());
        outputStage.setScene(scene);
        outputStage.setTitle("Error");
//        outputStage.setResizable(false);
        outputStage.show();
        outputPopupStage = outputStage;
        return text;
    }

}
