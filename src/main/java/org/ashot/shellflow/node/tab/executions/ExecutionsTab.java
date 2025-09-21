package org.ashot.shellflow.node.tab.executions;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecutionsTab extends Tab {
    private static final Logger log = LoggerFactory.getLogger(ExecutionsTab.class);

    private final TabPane executionsTabPane;
    private static final String TAB_NAME = "Executions";

    public ExecutionsTab() {
        this.executionsTabPane = new TabPane();
        executionsTabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        executionsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        Platform.runLater(() -> {
            setClosable(false);
            setContent(executionsTabPane);
            setText(TAB_NAME);
            setDisable(true);
            MenuItem menuItem = new MenuItem("Close All");
            menuItem.setDisable(true);
            menuItem.setOnAction(_ -> stopAll());
            menuItem.setGraphic(Icons.getCloseButtonIcon(IconSizeDefaults.CLOSE_ICON_SIZE.getSize()));
            setupContextMenu(menuItem);
            getExecutionsTabPane().getTabs().addListener((ListChangeListener<Tab>) _ -> {
                boolean isEmpty = executionsTabPane.getTabs().isEmpty();
                setDisable(isEmpty);
                menuItem.setDisable(isEmpty);
                if (isEmpty) {
                    getTabPane().getSelectionModel().selectFirst();
                }
            });
        });

    }

    private void setupContextMenu(MenuItem... menuItems) {
        setContextMenu(new ContextMenu(menuItems));
    }

    public void stopAll() {
        log.debug("Stopping all processes / terminals");
        TerminalRegistry.stopAllTerminals();
        getExecutionsTabPane().getTabs().clear();
    }

    public TabPane getExecutionsTabPane() {
        return executionsTabPane;
    }
}
