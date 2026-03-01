package org.ashot.shellflow.node.execution.tab;

import javafx.scene.control.Tooltip;
import org.ashot.shellflow.data.command.Command;
import org.ashot.shellflow.data.command.CommandSequence;

import java.util.ArrayList;
import java.util.List;

public class ExecutionTabFactory {
    private ExecutionTabFactory() {
    }

    public static SingleExecutionTab constructTabForSingular(Command command) {
        SingleExecutionTab executionTab = new SingleExecutionTab();
        executionTab.setText(command.isNameSet() ? command.getName() : command.getArgumentsString());
        executionTab.setTooltip(new Tooltip(command.getArgumentsString()));
        executionTab.setInitializing();
        return executionTab;
    }

    public static SingleExecutionTab constructTabForParallel(Command command) {
        SingleExecutionTab executionTab = new SingleExecutionTab();
        executionTab.setText(command.isNameSet() ? command.getName() : command.getArgumentsString());
        executionTab.setTooltip(new Tooltip(command.getArgumentsString()));
        //todo instead of disabling, show message
        executionTab.setDisable(true);
        executionTab.setInitializing();
        return executionTab;
    }

    public static SingleExecutionTab constructTabForSequence(Command command) {
        SingleExecutionTab executionTab = new SingleExecutionTab(true);
        executionTab.setText(command.isNameSet() ? command.getName() : command.getArgumentsString());
        executionTab.setTooltip(new Tooltip(command.getArgumentsString()));
        executionTab.setDisable(true);
        executionTab.setClosable(false);
        executionTab.setInitializing();
        return executionTab;
    }

    public static List<SingleExecutionTab> constructParallelExecutionPlaceholderTabs(List<Command> commands) {
        List<SingleExecutionTab> singleExecutionTabs = new ArrayList<>();

        for (Command command : commands) {
            SingleExecutionTab tab = ExecutionTabFactory.constructTabForParallel(command);
            singleExecutionTabs.add(tab);
        }
        return singleExecutionTabs;
    }

    public static List<SingleExecutionTab> constructSequencePlaceHolderTabs(CommandSequence commandSequence) {
        List<SingleExecutionTab> tabs = new ArrayList<>();
        for (Command command : commandSequence.commandList()) {
            SingleExecutionTab tab = ExecutionTabFactory.constructTabForSequence(command);
            tabs.add(tab);
        }
        return tabs;
    }
}
