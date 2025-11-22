package org.ashot.shellflow.node.execution.tab;

import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.command.Command;

public class ExecutionTabFactory {
    private ExecutionTabFactory() {
    }

    public static SingleExecutionTab constructTabFromCommand(Command command) {
        SingleExecutionTab executionTab = new SingleExecutionTab();
        executionTab.setText(command.isNameSet() ? command.getName() : command.getArgumentsString());
        executionTab.setTooltip(new Tooltip(command.getArgumentsString()));
        return executionTab;
    }

    public static SingleExecutionTab constructSequencePartOutputTab(Command command) {
        SingleExecutionTab executionTab = new SingleExecutionTab();
        executionTab.setText(command.isNameSet() ? command.getName() : command.getArgumentsString());
        executionTab.setTooltip(new Tooltip(command.getArgumentsString()));
        executionTab.setDisable(true);
        executionTab.setClosable(false);
        return executionTab;
    }
}
