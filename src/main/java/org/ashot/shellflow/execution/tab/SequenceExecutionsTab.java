package org.ashot.shellflow.execution.tab;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.utils.GUIAnimations;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.ashot.shellflow.utils.TabUtils.TAB_ICON_SIZE;

public class SequenceExecutionsTab extends Tab {
    private final Logger log = LoggerFactory.getLogger(SequenceExecutionsTab.class);
    private final TabPane sequenceExecutionTabPane;

    public SequenceExecutionsTab(String text) {
        super(text.isEmpty() ? "Sequence - Unknown" : text);
        this.sequenceExecutionTabPane = new TabPane();
        setContent(sequenceExecutionTabPane);
        setOnClosed(_ -> {
            for (Tab tab : sequenceExecutionTabPane.getTabs()) {
                if (tab instanceof SingleExecutionTab singleExecutionTab) {
                    singleExecutionTab.shutDownTerminal();
                }
            }
        });
    }

    public List<SingleExecutionTab> getTabsInSequence() {
        return sequenceExecutionTabPane.getTabs().stream().filter(SingleExecutionTab.class::isInstance).map(o -> (SingleExecutionTab) o).toList();
    }

    public TabPane getSequenceTabPane() {
        return sequenceExecutionTabPane;
    }

    public SequenceExecutionState updateState(SequenceExecutionState state) {
        log.debug("Sequence: {}, updated state: {}", getText(), state);
        switch (state) {
            case FINISHED, EXECUTION_IN_SEQUENCE_FINISHED -> setFinished();
            case FAILURE, INTERNAL_FAILURE -> setFailed();
            case CANCELLED -> setCancelled();
            case IN_PROGRESS -> setInProgress();
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
        return state;
    }

    public void setInProgress() {
        Glyph icon = Icons.getExecutionInProgressIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        GUIAnimations.rotateInAndWobble(icon);
    }

    public void setFailed() {
        Glyph icon = Icons.getExecutionErrorIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        GUIAnimations.rotateInAndWobble(icon);
    }

    public void setFinished() {
        Glyph icon = Icons.getExecutionFinishedIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        GUIAnimations.rotateInAndWobble(icon);
    }

    public void setCancelled() {
        Glyph icon = Icons.getExecutionCancelledIcon(TAB_ICON_SIZE);
        setGraphic(icon);
        setDisable(false);
        GUIAnimations.rotateInAndWobble(icon);
    }
}
