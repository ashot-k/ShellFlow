package org.ashot.shellflow.utils;

import javafx.scene.layout.Region;

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

}
