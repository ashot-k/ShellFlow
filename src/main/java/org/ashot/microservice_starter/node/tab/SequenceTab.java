package org.ashot.microservice_starter.node.tab;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.List;

public class SequenceTab extends Tab {
    TabPane outputTabs;

    public SequenceTab(String text, TabPane content) {
        super(text, content);
        this.outputTabs = content;
    }


    public List<OutputTab> getSequencePartOutputTabs(){
        return outputTabs.getTabs().stream().filter(e -> e instanceof OutputTab).map(o -> (OutputTab) o).toList();
    }
}
