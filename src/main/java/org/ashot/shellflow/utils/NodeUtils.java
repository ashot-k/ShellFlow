package org.ashot.shellflow.utils;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import org.ashot.shellflow.node.icon.Icons;

import static org.ashot.shellflow.data.constant.ButtonDefaults.CLOSE_BUTTON_SIZE;

public class NodeUtils {

    public static void setHeights(Region node, double height){
        node.setPrefHeight(height);
        node.setMaxHeight(height);
        node.setMinHeight(height);
    }

    public static void setWidths(Region node, double width){
        node.setPrefWidth(width);
        node.setMaxWidth(width);
        node.setMinWidth(width);
    }

    public static void setupCloseIconButton(Button button){
        button.setGraphic(Icons.getCloseButtonIcon(CLOSE_BUTTON_SIZE));
        button.setPadding(Insets.EMPTY);
        button.getStyleClass().add("no-outline-btn");
    }

}
