package org.ashot.shellflow.node.execution.tab;

import atlantafx.base.controls.Popover;
import com.pty4j.PtyProcess;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.ashot.shellflow.data.constant.ExecutionState;
import org.ashot.shellflow.execution.container.TerminalContainer;
import org.ashot.shellflow.execution.task.manager.TerminalSession;
import org.ashot.shellflow.node.icon.Icons;
import org.ashot.shellflow.node.toolbar.ExecutionTabToolbar;
import org.ashot.shellflow.node.toolbar.TerminalToolBar;
import org.ashot.shellflow.terminal.ShellFlowTerminalWidget;
import org.ashot.shellflow.terminal.tty.PtyProcessTtyConnector;
import org.ashot.shellflow.utils.GUIAnimations;
import org.ashot.shellflow.utils.ThemeHandler;
import org.controlsfx.glyphfont.Glyph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ashot.shellflow.data.constant.ExecutionState.*;

public class SingleExecutionTab extends ExecutionTab implements TerminalContainer {
    private static final Logger log = LoggerFactory.getLogger(SingleExecutionTab.class);
    private ShellFlowTerminalWidget terminal;
    private final VBox terminalWrapper = new VBox();
    private final SimpleObjectProperty<ExecutionState> state = new SimpleObjectProperty<>();
    private final StackPane stackPane = new StackPane();

    public SingleExecutionTab() {
        super();
    }

    public void setupTerminalWidget(TerminalSession terminalSession) {
        if (terminal == null) {
            terminal = terminalSession.terminalWidget();
            terminalWrapper.setFillWidth(true);
            terminalWrapper.setPadding(new Insets(5));
            terminalWrapper.getStyleClass().addAll(ThemeHandler.getSelectedTheme().isDark() ? "dark" : "light", "terminal-wrapper");
            setContent(stackPane);
        }
        terminal = terminalSession.terminalWidget();
        TerminalToolBar terminalToolBar = terminal.getTerminalToolBar();
        stackPane.getChildren().setAll(terminalWrapper, terminalToolBar);
        StackPane.setAlignment(terminalToolBar, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(terminalToolBar, new Insets(0, 25, 15, 0));
        terminalWrapper.getChildren().setAll(terminal.getPane());
        VBox.setVgrow(terminal.getPane(), Priority.ALWAYS);
    }

    @Override
    public void attachSession(TerminalSession session) {
        setupTerminalWidget(session);
    }

    @Override
    public ShellFlowTerminalWidget getTerminal() {
        return terminal;
    }

    public void updateState(ExecutionState newState, boolean sequence) {
        log.debug("Execution: {} [{}], updated state: {}", getText(), getTooltip().getText(), newState);
        switch (newState) {
            case IN_PROGRESS -> setInProgress(sequence);
            case INTERNAL_FAILURE, FAILURE -> setFailed(sequence);
            case FINISHED -> setFinished(sequence);
            case CANCELLED -> setCancelled(sequence);
        }
    }

    private Node createTabGraphic(Glyph icon, boolean sequence, Node... content) {
        Popover popoverToolbar = new ExecutionTabToolbar();
        HBox popoverContent = new HBox(10);

        Hyperlink toolbarLink = new Hyperlink("", Icons.getExtrasIcon(TAB_ICON_SIZE));
        toolbarLink.setOnAction(_ -> popoverToolbar.show(toolbarLink));
        HBox toolBarButtons = new HBox(5);

        if (!sequence) {
            toolBarButtons.getChildren().addAll(restartButton, renameField);
            popoverContent.getChildren().addAll(toolbarLink);
        }

        toolBarButtons.getChildren().addAll(content);
        toolBarButtons.setAlignment(Pos.CENTER);
        popoverToolbar.setContentNode(toolBarButtons);
        popoverContent.setPadding(new Insets(1));
        popoverContent.getChildren().add(icon);
        return popoverContent;
    }

    public void setInProgress(boolean sequence) {
        Glyph icon = Icons.getExecutionInProgressIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon, sequence));
        setDisable(false);
        setClosable(!sequence);
        state.setValue(IN_PROGRESS);
    }

    public void setFailed(boolean sequence) {
        Glyph icon = Icons.getExecutionErrorIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon, sequence));
        setDisable(false);
        setClosable(!sequence);
        GUIAnimations.rotateInAndWobble(icon);
        state.setValue(FAILURE);
    }

    public void setFinished(boolean sequence) {
        Glyph icon = Icons.getExecutionFinishedIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon, sequence));
        setClosable(!sequence);
        setDisable(false);
        GUIAnimations.rotateInAndWobble(icon);
        state.setValue(FINISHED);
    }

    public void setCancelled(boolean sequence) {
        Glyph icon = Icons.getExecutionCancelledIcon(TAB_ICON_SIZE);
        setGraphic(createTabGraphic(icon, sequence));
        setDisable(false);
        setClosable(!sequence);
        GUIAnimations.rotateInAndWobble(icon);
        state.setValue(CANCELLED);
    }

    public void setOnClose(EventHandler<Event> event) {
        this.setOnCloseRequest(closeEvent -> {
            if (event != null) {
                event.handle(closeEvent);
                return;
            }
            if (!closeEvent.isConsumed()) {
                this.terminal.close();
            }
        });
    }

    @Override
    public SimpleObjectProperty<ExecutionState> stateProperty() {
        return state;
    }

    public boolean isInProgress() {
        return state.get().equals(IN_PROGRESS);
    }

    public boolean isFinished() {
        return state.get().equals(FINISHED);
    }

    public boolean isCanceled() {
        return state.get().equals(CANCELLED);
    }

    public boolean isFailed() {
        return state.get().equals(FAILURE);
    }

    public PtyProcess getProcess() {
        return (PtyProcess) ((PtyProcessTtyConnector) terminal.getTtyConnector()).getProcess();
    }

}
