package org.ashot.shellflow.node.execution.tab;

import atlantafx.base.controls.Popover;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.data.constant.NotificationType;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.notification.SystemTray;
import org.ashot.shellflow.node.toolbar.ExecutionTabPopover;
import org.ashot.shellflow.utils.GUIAnimations;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.ashot.shellflow.data.message.NotificationMessages.failNotificationMessage;
import static org.ashot.shellflow.data.message.NotificationMessages.finishedNotificationMessage;


public class SequenceExecutionsTab extends ExecutionTab {
    private final Logger log = LoggerFactory.getLogger(SequenceExecutionsTab.class);
    private final TabPane sequenceExecutionTabPane;

    public SequenceExecutionsTab(String name, List<SingleExecutionTab> placeholders) {
        this(name);
        this.sequenceExecutionTabPane.getTabs().addAll(placeholders);
    }

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
        updateState(ExecutionState.IN_PROGRESS);
    }

    public void updateState(ExecutionState state) {
        log.debug("Sequence: {}, state: {}", getText(), state);
        switch (state) {
            case FINISHED -> setFinished();
            case FAILURE, INTERNAL_FAILURE -> setFailed();
            case CANCELLED -> setCancelled();
            case IN_PROGRESS -> setInProgress();
            default -> throw new IllegalStateException("Unexpected value: " + state);
        }
    }

    private Node createTabGraphic(Glyph icon, Node... content) {
        Popover popoverToolbar = new ExecutionTabPopover();
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

    @Override
    public void triggerErrorMode(String errorMessage) {
        Text errorText = new Text(errorMessage);
        errorText.setTextAlignment(TextAlignment.CENTER);
        HBox hbox = new HBox(errorText);
        hbox.setAlignment(Pos.CENTER);
        hbox.setFillHeight(true);
        hbox.setPadding(new Insets(5));
        setContent(hbox);

        getTabPane().widthProperty().addListener((_, _, newValue) -> errorText.setWrappingWidth(newValue.doubleValue() - 50));
        setFailed();
    }

    public void handleSequenceState(ExecutionState sequenceState) {
        updateState(sequenceState);
        switch (sequenceState) {
            case IN_PROGRESS -> log.info("Sequence in progress: {}", getText());
            case FINISHED ->
                    SystemTray.displayNotification(sequenceState.getValue(), finishedNotificationMessage(getText()), NotificationType.SUCCESS);
            case FAILURE ->
                    SystemTray.displayNotification(sequenceState.getValue(), failNotificationMessage(getText()), NotificationType.EXECUTION_FAILURE);
            case INTERNAL_FAILURE ->
                    SystemTray.displayNotification(sequenceState.getValue(), failNotificationMessage(getText()), NotificationType.INTERNAL_FAILURE);
            case CANCELLED -> log.info("Sequence Canceled: {}", getText());
            default -> throw new IllegalStateException("Unexpected value: " + sequenceState);
        }
    }

    public void handleSequencePartState(ExecutionState sequenceState) {
        log.debug("Sequence part state: {}", sequenceState.getValue());
        switch (sequenceState) {
            case IN_PROGRESS -> handleSequenceState(ExecutionState.IN_PROGRESS);
            case FINISHED -> getSequenceTabPane().getSelectionModel().selectNext();
            case FAILURE -> {
                handleSequenceState(ExecutionState.FAILURE);
                SystemTray.displayNotification(sequenceState.getValue(), failNotificationMessage(getText()), NotificationType.EXECUTION_FAILURE);
            }
            case INTERNAL_FAILURE -> {
                handleSequenceState(ExecutionState.INTERNAL_FAILURE);
                SystemTray.displayNotification(sequenceState.getValue(), failNotificationMessage(getText()), NotificationType.INTERNAL_FAILURE);
            }
            case CANCELLED -> handleSequenceState(ExecutionState.CANCELLED);
            default -> throw new IllegalStateException("Unexpected value: " + sequenceState);
        }
    }
}
