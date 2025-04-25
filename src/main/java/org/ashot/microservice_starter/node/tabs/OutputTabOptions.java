package org.ashot.microservice_starter.node.tabs;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class OutputTabOptions {
    private CheckBox wrapTextOption;
    private OutputTab currentOutputTab;

    public OutputTabOptions(OutputTab outputTab) {
        this.currentOutputTab = outputTab;
        setupOptions();
    }

    private void setupOptions() {
        this.wrapTextOption = new CheckBox("Wrap Text");
        this.wrapTextOption.setOnAction(_ -> {
            currentOutputTab.getCodeArea().setWrapText(wrapTextOption.isSelected());
            currentOutputTab.getScrollPane().setHbarPolicy(wrapTextOption.isSelected() ? ScrollPane.ScrollBarPolicy.NEVER : ScrollPane.ScrollBarPolicy.AS_NEEDED);
        });
    }

    public List<Node> getOptions() {
        return List.of(wrapTextOption);
    }

    public void selectOutputTab(OutputTab outputTab) {
        this.currentOutputTab = outputTab;
    }
}
