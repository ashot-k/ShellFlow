package org.ashot.shellflow.node.tab.executions;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.command.CommandSequence;

import java.util.List;

public class SequenceExecutionsTab extends Tab {
    private final TabPane sequenceExecutionTabPane;
    private final CommandSequence commandSequence;

    public SequenceExecutionsTab(CommandSequence commandSequence) {
        super(commandSequence.getSequenceName());
        this.commandSequence = commandSequence;
        this.sequenceExecutionTabPane = new TabPane();
        setContent(sequenceExecutionTabPane);
        setOnClosed(_ -> {
            for (Tab tab : sequenceExecutionTabPane.getTabs()) {
                if (tab instanceof ExecutionTab executionTab) {
                    executionTab.shutDownTerminal();
                }
            }
        });
    }

    public List<ExecutionTab> getSequentialExecutionTabPaneTabs() {
        return sequenceExecutionTabPane.getTabs().stream().filter(e -> e instanceof ExecutionTab).map(o -> (ExecutionTab) o).toList();
    }

    public TabPane getSequenceTabPane() {
        return sequenceExecutionTabPane;
    }

    public CommandSequence getCommandSequence() {
        return commandSequence;
    }
}
