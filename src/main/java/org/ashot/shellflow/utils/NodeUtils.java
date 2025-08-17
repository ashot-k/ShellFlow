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

    public static void addPaddingVertical(Region node, int top, int bottom){
        node.setPadding(new Insets(top, 0 ,bottom, 0));
    }

    public static void addPaddingVertical(Region node, int vertical){
        node.setPadding(new Insets(vertical, 0 ,vertical, 0));
    }

    public static void addPaddingHorizontal(Region node, int right, int left){
        node.setPadding(new Insets(0, right,0, left));
    }

    public static void addPaddingHorizontal(Region node, int horizontal){
        node.setPadding(new Insets(0, horizontal,0, horizontal));
    }

}
