package org.ashot.shellflow.node.execution.tab;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.utils.TabUtils;

public class ParallelExecutionsTab extends Tab {
    private final TabPane parallelExecutionTabPane;

    public ParallelExecutionsTab() {
        parallelExecutionTabPane = new TabPane();
        parallelExecutionTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        parallelExecutionTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        setContent(parallelExecutionTabPane);
        setOnClosed(_ -> {
            for (Tab tab : parallelExecutionTabPane.getTabs()) {
                if (tab instanceof SingleExecutionTab singleExecutionTab) {
                    TabUtils.setCancelled(singleExecutionTab);
                    singleExecutionTab.shutDownTerminal();
                }
            }
        });
        parallelExecutionTabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (c.next() && c.getList().isEmpty()) {
                getTabPane().getTabs().remove(this);
            }
        });
    }

    public TabPane getParallelExecutionTabPane() {
        return parallelExecutionTabPane;
    }

    public void setName(String name) {
        setText(name.isBlank() ? "Execution - Unknown" : name);
    }
}
