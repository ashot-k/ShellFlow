package org.ashot.shellflow.node.tab.executions;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.command.CommandSequence;

import java.util.List;

public class SequenceExecutionsTab extends Tab {
    private TabPane sequentialExecutionTabPane;
    private CommandSequence commandSequence;

    public SequenceExecutionsTab(CommandSequence commandSequence, TabPane content) {
        super(commandSequence.getSequenceName(), content);
        this.commandSequence = commandSequence;
        this.sequentialExecutionTabPane = content;
        this.setOnClosed(_ -> {
            for (Tab tab : sequentialExecutionTabPane.getTabs()) {
                if (tab instanceof ExecutionTab executionTab) {
                    executionTab.shutDownTerminal();
                }
            }
        });
    }

    public List<ExecutionTab> getSequentialExecutionTabPaneTabs() {
        return sequentialExecutionTabPane.getTabs().stream().filter(e -> e instanceof ExecutionTab).map(o -> (ExecutionTab) o).toList();
    }

    public TabPane getSequentialExecutionTabPane() {
        return sequentialExecutionTabPane;
    }

    public CommandSequence getCommandSequence() {
        return commandSequence;
    }
}
