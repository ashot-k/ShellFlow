package org.ashot.shellflow.node.execution.tab;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.List;

public class ParallelExecutionsTab extends ExecutionTab {
    private final TabPane parallelExecutionTabPane;

    public ParallelExecutionsTab(String name) {
        parallelExecutionTabPane = new TabPane();
        parallelExecutionTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        parallelExecutionTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        parallelExecutionTabPane.setTabMaxWidth(TAB_NAME_MAX_WIDTH);
        setContent(parallelExecutionTabPane);
        setText(name.isBlank() ? "Execution - Unknown" : name);
        parallelExecutionTabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            if (c.next() && c.getList().isEmpty()) {
                getTabPane().getTabs().remove(this);
            }
        });
    }

    public List<SingleExecutionTab> getSingularExecutionTabs() {
        return parallelExecutionTabPane.getTabs().stream().filter(SingleExecutionTab.class::isInstance).map(o -> (SingleExecutionTab) o).toList();
    }

    public TabPane getParallelExecutionTabPane() {
        return parallelExecutionTabPane;
    }
}
