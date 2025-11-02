package org.ashot.shellflow.execution.tab;

import javafx.scene.control.TabPane;
import org.ashot.shellflow.registry.TerminalRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExecutionsTabPane extends TabPane {
    private static final Logger log = LoggerFactory.getLogger(ExecutionsTabPane.class);

    public ExecutionsTabPane() {
        setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
    }

    public void stopAll() {
        log.debug("Stopping all processes / terminals");
        TerminalRegistry.stopAllTerminals();
        getTabs().clear();
    }
}
