package org.ashot.shellflow.node.tab.executions;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ParallelExecutionsTab extends Tab {
    private final TabPane parallelExecutionTabPane;

    public ParallelExecutionsTab() {
        this.parallelExecutionTabPane = new TabPane();
        setContent(parallelExecutionTabPane);
        setOnClosed(_ -> {
            for (Tab tab : parallelExecutionTabPane.getTabs()) {
                if (tab instanceof ExecutionTab executionTab) {
                    executionTab.shutDownTerminal();
                }
            }
        });
    }

    public TabPane getParallelExecutionTabPane() {
        return parallelExecutionTabPane;
    }

    public void setName(String name){
        setText(name.isBlank() ? "Execution - " + ((int) (Math.random() * 100)) : name);
    }
}
