package org.ashot.shellflow.node.tab.executions;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.utils.TabUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelExecutionsTab extends Tab {
    private static final Logger log = LoggerFactory.getLogger(ParallelExecutionsTab.class);
    private final TabPane parallelExecutionTabPane;

    public ParallelExecutionsTab() {
        this.parallelExecutionTabPane = new TabPane();
        parallelExecutionTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        parallelExecutionTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        setContent(parallelExecutionTabPane);
        setOnClosed(_ -> {
            for (Tab tab : parallelExecutionTabPane.getTabs()) {
                if (tab instanceof ExecutionTab executionTab) {
                    TabUtils.setCancelled(executionTab);
                    executionTab.shutDownTerminal();
                }
            }
        });
        parallelExecutionTabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (c.next() && c.getList().isEmpty()) {
                this.getTabPane().getTabs().remove(this);
            }
        });
    }

    public TabPane getParallelExecutionTabPane() {
        return parallelExecutionTabPane;
    }

    public void setName(String name) {
        setText(name.isBlank() ? "Execution - " + ((int) (Math.random() * 100)) : name);
    }
}
