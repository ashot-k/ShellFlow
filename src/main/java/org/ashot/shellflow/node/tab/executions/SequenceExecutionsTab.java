package org.ashot.shellflow.node.tab.executions;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.command.CommandSequence;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.Animations;
import org.controlsfx.glyphfont.Glyph;

import java.util.List;

import static org.ashot.shellflow.data.constant.SequenceExecutionState.EXECUTION_IN_SEQUENCE_FINISHED;
import static org.ashot.shellflow.utils.TabUtils.TAB_ICON_SIZE;

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

    public List<ExecutionTab> getTabsInSequence() {
        return sequenceExecutionTabPane.getTabs().stream().filter(e -> e instanceof ExecutionTab).map(o -> (ExecutionTab) o).toList();
    }

    public TabPane getSequenceTabPane() {
        return sequenceExecutionTabPane;
    }

    public CommandSequence getCommandSequence() {
        return commandSequence;
    }

    public SequenceExecutionState updateState(SequenceExecutionState state){
        switch (state) {
            case FINISHED -> setFinished();
            case FAILURE, INTERNAL_FAILURE -> setFailed();
            case CANCELLED -> setCancelled();
            case IN_PROGRESS -> setInProgress();
        }
        return state;
    }

    public void setInProgress() {
        Glyph icon = Icons.getExecutionInProgressIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        Animations.rotateInAndWobble(icon);
    }

    public void setFailed() {
        Glyph icon = Icons.getExecutionErrorIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        Animations.rotateInAndWobble(icon);
    }

    public void setFinished() {
        Glyph icon = Icons.getExecutionFinishedIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        Animations.rotateInAndWobble(icon);
    }

    public void setCancelled() {
        Glyph icon = Icons.getExecutionCancelledIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        Animations.rotateInAndWobble(icon);
    }
}
