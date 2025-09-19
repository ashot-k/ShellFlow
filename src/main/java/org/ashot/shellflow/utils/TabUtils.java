package org.ashot.shellflow.utils;

import javafx.scene.control.Tab;
import org.ashot.shellflow.Controller;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.ExecutionsTab;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.controlsfx.glyphfont.Glyph;

public class TabUtils {

    public static final int TAB_ICON_SIZE = 18;

    public static void setInProgress(ExecutionTab tab){
        setInProgress(tab, false);
    }
    public static void setFailed(ExecutionTab tab){
        setFailed(tab, false);
    }
    public static void setCancelled(ExecutionTab tab){
        setCancelled(tab, false);
    }
    public static void setFinished(ExecutionTab tab){
        setFinished(tab, false);
    }

    public static void setInProgress(ExecutionTab tab, boolean sequence) {
        tab.setGraphic(Icons.getExecutionInProgressIcon(TAB_ICON_SIZE));
        tab.setDisable(false);
        tab.setClosable(true);
        tab.setInProgress();
    }

    public static void setFailed(ExecutionTab tab, boolean sequence) {
        Glyph icon = Icons.getExecutionErrorIcon(TAB_ICON_SIZE);
        tab.setGraphic(icon);
        tab.setDisable(false);
        tab.setClosable(!sequence);
        tab.setFailed();
        Animations.rotateInAndWobble(icon);
    }


    public static void setFinished(ExecutionTab tab, boolean sequence) {
        Glyph icon = Icons.getExecutionFinishedIcon(TAB_ICON_SIZE);
        tab.setGraphic(icon);
        tab.setClosable(!sequence);
        tab.setDisable(false);
        tab.setFinished();
        Animations.rotateInAndWobble(icon);
    }

    public static void setCancelled(ExecutionTab tab, boolean sequence) {
        Glyph icon = Icons.getExecutionCancelledIcon(TAB_ICON_SIZE);
        tab.setGraphic(icon);
        tab.setDisable(false);
        tab.setClosable(!sequence);
        Animations.rotateInAndWobble(icon);
        tab.setCancelled();
    }

    public static void addToExecutions(Tab tab) {
        if (tab == null) throw new RuntimeException("Tab added to executions is null");
        Controller controller = ControllerRegistry.getMainController();
        ExecutionsTab executionsTab = controller.getExecutionsTab();
        executionsTab.getExecutionsTabPane().getTabs().add(tab);
        executionsTab.getExecutionsTabPane().getSelectionModel().select(tab);
    }
}
