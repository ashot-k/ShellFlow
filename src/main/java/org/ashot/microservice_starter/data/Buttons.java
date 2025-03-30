package org.ashot.microservice_starter.data;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import org.ashot.microservice_starter.Utils;

public class Buttons {
    public static int size = 24;
    public static Button deleteEntryButton() {
        Button btn = new Button("");
        Image closeImg = new Image(
                Utils.getIconAsInputStream("close-icon.png"),
                size, size,
                true, false
        );
        btn.setShape(new Circle(size));
        btn.setMaxSize(size, size);
        btn.setMinSize(size, size);
        btn.setGraphic(new ImageView(closeImg));
        btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btn.getStyleClass().add("close-btn");
        return btn;
    }
}
