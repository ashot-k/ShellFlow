package org.ashot.shellflow.utils;

import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class DebugUtils {
    private DebugUtils(){}

    public static void addBorder(Node... node) {
        Arrays.stream(node).toList().forEach(e -> {
            if(e instanceof Pane pane){
                addBorderToPane(pane);
            }
            else if (e instanceof Control control){
                addBorderToControl(control);
            }
        });
    }

    private static void addBorderToPane(Pane node) {
        node.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(1), BorderWidths.DEFAULT)));
    }

    private static void addBorderToControl(Control node) {
        node.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(1), BorderWidths.DEFAULT)));
    }
}
