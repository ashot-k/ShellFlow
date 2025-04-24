package org.ashot.microservice_starter.node;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;

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
        this.wrapTextOption.setOnAction(_ -> currentOutputTab.getCodeArea().setWrapText(wrapTextOption.isSelected()));
    }

    public List<Node> getOptions() {
        return List.of(wrapTextOption);
    }

    public void selectOutputTab(OutputTab outputTab) {
        this.currentOutputTab = outputTab;
    }
}
