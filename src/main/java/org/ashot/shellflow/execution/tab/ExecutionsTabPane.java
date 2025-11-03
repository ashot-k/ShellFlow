package org.ashot.shellflow.execution.tab;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.constant.IconSizeDefaults;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecutionsTabPane extends TabPane {
    private static final Logger log = LoggerFactory.getLogger(ExecutionsTabPane.class);

    public ExecutionsTabPane() {
        setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        setupContextMenu();
    }

    private void setupContextMenu() {
        MenuItem stopAllMenuItem = new MenuItem("Stop all", Icons.getCloseButtonIcon(IconSizeDefaults.CLOSE_ICON_SIZE.getSize()));
        stopAllMenuItem.setOnAction(_ -> stopAll());
        MenuItem closeAllMenuItem = new MenuItem("Close all", Icons.getClearIcon(IconSizeDefaults.CLOSE_ICON_SIZE.getSize()));
        closeAllMenuItem.setOnAction(_ -> closeAll());

        setContextMenu(new ContextMenu(stopAllMenuItem, closeAllMenuItem));
        setOnContextMenuRequested(e -> {
            if (getTabs().stream().anyMatch(tab -> tab.getContent().isHover())) {
                getContextMenu().hide();
                e.consume();
            }
        });
    }

    public void stopAll() {
        log.debug("Stopping all processes / terminals");
        TerminalRegistry.stopAllTerminals();
    }

    public void closeAll() {
        log.debug("Stopping all processes / terminals and clearing tabs");
        TerminalRegistry.stopAllTerminals();
        getTabs().clear();
    }
}
