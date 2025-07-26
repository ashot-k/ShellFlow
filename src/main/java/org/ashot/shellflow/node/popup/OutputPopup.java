package org.ashot.shellflow.node.popup;

import atlantafx.base.theme.PrimerDark;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.ashot.shellflow.Main;
import org.ashot.shellflow.data.constant.Fonts;

public class OutputPopup {
    private static Stage outputPopupStage;
    private static final int SIZE_X = 700;
    private static final int SIZE_Y = 450;
    private static final boolean RESIZABLE = true;

    public static TextArea outputPopup(String msg, String title, boolean sequential) {
        Stage outputStage = new Stage();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane, SIZE_X, SIZE_Y, Color.BLACK);
        scene.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        scene.getStylesheets().add(Main.CSS_FILE_LOCATION);

        TextArea textArea = new TextArea();
        if (!sequential) {
            textArea.setText(formatText(msg));
        } else {
            textArea.setText(msg);
        }
        textArea.setWrapText(true);
        textArea.setFont(Fonts.title);
        textArea.setEditable(false);

        HBox outputContainer = new HBox();
        outputContainer.getChildren().add(textArea);
        outputContainer.getStyleClass().add("output-message");
        scrollPane.setContent(outputContainer);
        outputStage.setScene(scene);
        outputStage.setTitle(title);
        outputStage.setResizable(RESIZABLE);
        outputStage.show();
        outputPopupStage = outputStage;
        return textArea;
    }

    private static String formatText(String msg) {
        String[] strings = msg.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            stringBuilder.append(i + 1).append(".").append(" ").append(s).append("\n\n");
        }
        return stringBuilder.toString();
    }

    public static void closePopup() {
        outputPopupStage.close();
    }
}
