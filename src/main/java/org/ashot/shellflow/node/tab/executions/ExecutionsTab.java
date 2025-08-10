package org.ashot.shellflow.node.tab.executions;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ExecutionsTab extends Tab {
    TabPane executionsTabPane;
    String name = "Executions";

    public ExecutionsTab() {
        this.executionsTabPane = new TabPane();
        executionsTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        executionsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        Platform.runLater(() -> {
            this.setClosable(false);
            this.setContent(executionsTabPane);
            this.setText(name);
            this.setDisable(true);
            this.getExecutionsTabPane().getTabs().addListener((ListChangeListener<Tab>) _-> {
                if (this.executionsTabPane.getTabs().isEmpty()) {
                    this.getTabPane().getSelectionModel().selectFirst();
                    this.setDisable(true);
                } else {
                    this.setDisable(false);
                }
            });
        });
    }

    public TabPane getExecutionsTabPane() {
        return executionsTabPane;
    }
}
