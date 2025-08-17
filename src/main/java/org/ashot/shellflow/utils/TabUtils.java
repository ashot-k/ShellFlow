package org.ashot.shellflow.utils;

import javafx.application.Platform;
import javafx.scene.control.Tab;
import org.ashot.shellflow.Controller;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.tab.executions.ExecutionTab;
import org.ashot.shellflow.node.tab.executions.ExecutionsTab;
import org.ashot.shellflow.registry.ControllerRegistry;
import org.controlsfx.glyphfont.Glyph;

public class TabUtils {

    private static final int tabIconSize = 18;

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
            tab.setGraphic(Icons.getExecutionInProgressIcon(tabIconSize));
            tab.setDisable(false);
            tab.setClosable(true);
        });
        if (tab instanceof ExecutionTab executionTab) {
            executionTab.setInProgress(true);
        }
    }

    public static void setFailed(Tab tab) {
        Platform.runLater(() -> {
            Glyph icon = Icons.getExecutionErrorIcon(tabIconSize);
            tab.setGraphic(icon);
            tab.setDisable(false);
            Animator.rotateInAndWobble(icon);
        });
        if (tab instanceof ExecutionTab executionTab) {
            executionTab.setInProgress(false);
            executionTab.setFailed(true);
        }
    }

    public static void setFinished(Tab tab) {
        Platform.runLater(() -> {
            Glyph icon = Icons.getExecutionFinishedIcon(tabIconSize);
            tab.setGraphic(icon);
            tab.setDisable(false);
            Animator.rotateInAndWobble(icon);
        });
        if (tab instanceof ExecutionTab executionTab) {
            executionTab.setInProgress(false);
            executionTab.setFinished(true);
        }
    }

    public static void setCanceled(Tab tab) {
        Platform.runLater(() -> {
            Glyph icon = Icons.getExecutionCanceledIcon(tabIconSize);
            tab.setGraphic(icon);
            tab.setDisable(false);
            tab.setClosable(true);
            Animator.rotateInAndWobble(icon);
        });
        if (tab instanceof ExecutionTab executionTab) {
            executionTab.setInProgress(false);
            executionTab.setCanceled(true);
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

/*
    public static void addToProfiler(ExecutionDescriptor executionDescriptor) {
        if (executionDescriptor == null) throw new RuntimeException("Execution Descriptor is null");
        Controller controller = ControllerRegistry.get("main", Controller.class);
        Platform.runLater(() -> {
            ProfilerTab profilerTab = controller.getProfilerTab();
            profilerTab.monitorExecution(executionDescriptor);
        });
    }
*/

}
