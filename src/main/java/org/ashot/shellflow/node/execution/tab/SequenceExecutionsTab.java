package org.ashot.shellflow.node.execution.tab;

import atlantafx.base.controls.Popover;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import org.ashot.shellflow.data.constant.SequenceExecutionState;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.toolbar.ExecutionTabToolbar;
import org.ashot.shellflow.utils.GUIAnimations;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class SequenceExecutionsTab extends ExecutionTab {
    private final Logger log = LoggerFactory.getLogger(SequenceExecutionsTab.class);
    private final TabPane sequenceExecutionTabPane;

    public SequenceExecutionsTab(String text) {
        sequenceExecutionTabPane = new TabPane();
        sequenceExecutionTabPane.setTabMaxWidth(TAB_NAME_MAX_WIDTH);
        setContent(sequenceExecutionTabPane);
        setText(text.isEmpty() ? "Sequence - Unknown" : text);
    }

    public List<SingleExecutionTab> getTabsInSequence() {
        return sequenceExecutionTabPane.getTabs().stream().filter(SingleExecutionTab.class::isInstance).map(o -> (SingleExecutionTab) o).toList();
    }

    public TabPane getSequenceTabPane() {
        return sequenceExecutionTabPane;
    }

    public void reset() {
        getTabsInSequence().forEach(tabInSequence -> {
            tabInSequence.setGraphic(null);
            tabInSequence.setDisable(true);
            tabInSequence.setClosable(false);
        });
        getSequenceTabPane().getSelectionModel().selectFirst();
        updateState(SequenceExecutionState.IN_PROGRESS);
    }

    public void updateState(SequenceExecutionState state) {
        log.debug("Sequence: {}, updated state: {}", getText(), state);
        switch (state) {
            case FINISHED, EXECUTION_IN_SEQUENCE_FINISHED -> setFinished();
            case FAILURE, INTERNAL_FAILURE -> setFailed();
            case CANCELLED -> setCancelled();
            case IN_PROGRESS -> setInProgress();
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private Node createTabGraphic(Glyph icon, Node... content) {
        Popover popoverToolbar = new ExecutionTabToolbar();
        HBox popoverContent = new HBox(10);

        Hyperlink toolbarLink = new Hyperlink("", Icons.getExtrasIcon(TAB_ICON_SIZE));
        toolbarLink.setOnAction(_ -> popoverToolbar.show(toolbarLink));

        HBox toolBarButtons = new HBox(5);

        toolBarButtons.getChildren().addAll(restartButton, renameField);
        popoverContent.getChildren().add(toolbarLink);

        toolBarButtons.getChildren().addAll(content);
        popoverToolbar.setContentNode(toolBarButtons);
        toolBarButtons.setAlignment(Pos.CENTER);
        popoverContent.setPadding(new Insets(1));
        popoverContent.getChildren().add(icon);
        return popoverContent;
    }

    public void setInProgress() {
        Glyph icon = Icons.getExecutionInProgressIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
        setDisable(false);
    }

    public void setFailed() {
        Glyph icon = Icons.getExecutionErrorIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
        setDisable(false);
        GUIAnimations.rotateInAndWobble(icon);
    }

    public void setFinished() {
        Glyph icon = Icons.getExecutionFinishedIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon));
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
