package org.ashot.shellflow.utils;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.ashot.shellflow.Controller;
import org.ashot.shellflow.data.icon.Icons;
import org.ashot.shellflow.node.tab.OutputTab;
import org.ashot.shellflow.node.tab.executions.ExecutionsTab;
import org.ashot.shellflow.registry.ControllerRegistry;

public class TabUtils {

    private static final int titleIconsSize = 18;

    public enum ExecutionState {
        IN_PROGRESS("In progress"), FINISHED("Finished"), FAILURE("Failure");

        private final String value;

        ExecutionState(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static void setInProgress(Tab tab) {
        Platform.runLater(() -> {
            tab.setGraphic(Icons.getExecutionInProgressIcon(titleIconsSize));
            tab.setDisable(false);
            tab.setClosable(true);
        });
        if (tab instanceof OutputTab outputTab) {
            outputTab.setInProgress(true);
        }
    }

    public static void setFailed(Tab tab) {
        Platform.runLater(() -> {
            tab.setGraphic(Icons.getExecutionErrorIcon(titleIconsSize));
            tab.setDisable(false);
        });
        if (tab instanceof OutputTab outputTab) {
            outputTab.setInProgress(false);
            outputTab.setFailed(true);
        }
    }

    public static void setFinished(Tab tab) {
        Platform.runLater(() -> {
            tab.setGraphic(Icons.getExecutionFinishedIcon(titleIconsSize));
            tab.setDisable(false);
        });
        if (tab instanceof OutputTab outputTab) {
            outputTab.setInProgress(false);
            outputTab.setFinished(true);
        }
    }

    public static void setCanceled(Tab tab) {
        Platform.runLater(() -> {
            tab.setGraphic(Icons.getExecutionCanceledIcon(titleIconsSize));
            tab.setDisable(false);
            tab.setClosable(true);
        });
        if (tab instanceof OutputTab outputTab) {
            outputTab.setInProgress(false);
            outputTab.setCanceled(true);
        }
    }

    public static void addToExecutions(Tab tab) {
        if (tab == null) throw new RuntimeException("Tab added to executions is null");
        Controller controller = ControllerRegistry.get("main", Controller.class);
        Platform.runLater(() -> {
            ExecutionsTab executionsTab = controller.getExecutionsTab();
            executionsTab.getExecutionsTabPane().getTabs().add(tab);
            executionsTab.getExecutionsTabPane().getSelectionModel().select(tab);
        });
    }

}
