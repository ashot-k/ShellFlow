package org.ashot.microservice_starter.node.tabs;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class OutputTabOptions {
    private OutputTab currentOutputTab;

    public OutputTabOptions(OutputTab outputTab) {
        this.currentOutputTab = outputTab;
    }

    public void toggleWrapText(boolean option) {
        currentOutputTab.getCodeArea().setWrapText(option);
        currentOutputTab.getScrollPane().setHbarPolicy(option ? ScrollPane.ScrollBarPolicy.NEVER : ScrollPane.ScrollBarPolicy.AS_NEEDED);
    }

    public List<Node> getOptions() {
        return List.of();
    }

    public void selectOutputTab(OutputTab outputTab) {
        this.currentOutputTab = outputTab;
    }
}
