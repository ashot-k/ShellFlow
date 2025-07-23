package org.ashot.shellflow.node.tab.executions;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.node.tab.OutputTab;

import java.util.List;

public class SequentialExecutionsTab extends Tab {
    TabPane sequentialExecutionTabPane;

    public SequentialExecutionsTab(String text, TabPane content) {
        super(text, content);
        this.sequentialExecutionTabPane = content;
        this.setOnClosed(_ -> {
            for (Tab tab : sequentialExecutionTabPane.getTabs()) {
                if (tab instanceof OutputTab outputTab) {
                    outputTab.shutDownTerminal();
                }
            }
        });
    }

    public List<OutputTab> getSequentialExecutionTabPaneTabs() {
        return sequentialExecutionTabPane.getTabs().stream().filter(e -> e instanceof OutputTab).map(o -> (OutputTab) o).toList();
    }

    public TabPane getSequentialExecutionTabPane() {
        return sequentialExecutionTabPane;
    }
}
