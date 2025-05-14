package org.ashot.microservice_starter.node.tab;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import org.ashot.microservice_starter.data.icon.Icons;
import org.ashot.microservice_starter.data.message.ToolTipMessages;
import org.ashot.microservice_starter.node.CustomButton;

public class OutputTabButton extends CustomButton {

    public static Button clearOutputButton(OutputTab outputTab) {
        Button clearButton = new Button("", Icons.getClearIcon(20));
        clearButton.setTooltip(new Tooltip(ToolTipMessages.clearOutput()));
        clearButton.setOnAction(_ -> {
            outputTab.getCodeArea().clear();
            outputTab.getOutputSearchOptions().setAutoScroll(true);
        });
        clearButton.setOpacity(0.5);
        StackPane.setAlignment(clearButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(clearButton, new Insets(0, 20, 5, 0));

        return clearButton;
    }
}
