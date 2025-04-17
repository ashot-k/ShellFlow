package org.ashot.microservice_starter.node.popup;

import atlantafx.base.theme.PrimerDark;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.ashot.microservice_starter.Main;

public class OutputPopup {
    private static Stage outputPopupStage;
    private static final int SIZE_X = 700;
    private static final int SIZE_Y = 450;
    private static final boolean RESIZABLE = true;
    private static final int TEXT_OFFSET = 50;
    private static final int TEXT_SIZE = 24;

    public static Text outputPopup(String msg, String title) {
        Stage outputStage = new Stage();
        ScrollPane scrollPane = new ScrollPane();

        Scene scene = new Scene(scrollPane, SIZE_X, SIZE_Y, Color.BLACK);
        scene.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        scene.getStylesheets().add(Main.getUserAgentStylesheet());

        Text text = new Text();
        text.setText(msg);
        text.setWrappingWidth(scene.getWidth() - TEXT_OFFSET);
        text.setFont(Font.font(TEXT_SIZE));

        HBox outputContainer = new HBox();
        outputContainer.getChildren().add(text);
        outputContainer.getStyleClass().add("output-message");
        scrollPane.setContent(outputContainer);


        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                text.setWrappingWidth(newValue.doubleValue() - TEXT_OFFSET);
            }
        });

        outputStage.setScene(scene);
        outputStage.setTitle(title);
        outputStage.setResizable(RESIZABLE);
        outputStage.show();
        outputPopupStage = outputStage;
        return text;
    }

    public static void closePopup() {
        outputPopupStage.close();
    }
}
