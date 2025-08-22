package org.ashot.shellflow.utils;

import javafx.scene.control.Control;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class DebugUtils {

    public static void addBorder(Pane node){
        node.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,  new CornerRadii(1), BorderWidths.DEFAULT)));
    }
    public static void addBorder(Pane... node){
        Arrays.stream(node).toList().forEach(e -> e.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,  new CornerRadii(1), BorderWidths.DEFAULT))));
    }

    public static void addBorder(Control node){
        node.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,  new CornerRadii(1), BorderWidths.DEFAULT)));
    }
}
