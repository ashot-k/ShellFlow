package org.ashot.microservice_starter.data;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.ashot.microservice_starter.Utils;

public class Buttons {
    public static final int SIZE = 24;

    public static Button deleteEntryButton() {
        Button btn = new Button("");
        Image closeImg = new Image(
                Utils.getIconAsInputStream("close-icon.png"),
                SIZE, SIZE,
                true, false
        );
        btn.setShape(new Circle(SIZE));
        btn.setMaxSize(SIZE, SIZE);
        btn.setMinSize(SIZE, SIZE);
        btn.setGraphic(new ImageView(closeImg));
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.getStyleClass().add("close-btn");
        return btn;
    }
}
